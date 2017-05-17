package tcb.adventurousdungeons.api.script;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;
import tcb.adventurousdungeons.registries.ScriptComponentRegistry;

public class Script {
	private final List<IScriptComponent> components = new ArrayList<>();

	private int instructions = 0;

	public void addComponent(IScriptComponent component) {
		this.components.add(component);
	}

	public void removeComponent(IScriptComponent component) {
		this.components.remove(component);
	}

	public IScriptComponent getComponent(UUID id) {
		for(IScriptComponent component : this.components) {
			if(component.getID().equals(id)) {
				return component;
			}
		}
		return null;
	}

	public Collection<IScriptComponent> getComponents() {
		return Collections.unmodifiableCollection(this.components);
	}

	public void onComponentExecute(IScriptComponent component) throws ScriptException {
		this.instructions++;

		//System.out.println("Run: " + component.getName());

		//TODO: Make another counter for recursion depth

		if(this.instructions > 1000) {
			this.instructions = 0;
			throw new InstructionsExceededException(component, "Instruction limit exceeded");
		}
	}

	public void initPorts() {
		for(IScriptComponent component : this.components) {
			component.initPorts();
		}
	}

	/**
	 * Resets the instructions counter
	 */
	public void resetScript() {
		this.instructions = 0;
	}

	/**
	 * Exports the specified components to NBT
	 * @param components
	 * @return
	 */
	public static NBTTagCompound exportComponents(List<IScriptComponent> components) {
		Set<IScriptComponent> componentSet = new HashSet<>(components);

		NBTTagCompound nbt = new NBTTagCompound();

		NBTTagList componentsNbt = new NBTTagList();
		NBTTagList connectionsNbt = new NBTTagList();

		for(IScriptComponent component : componentSet) {
			//Component data
			NBTTagCompound componentNbt = new NBTTagCompound();
			try {
				ResourceLocation type = ScriptComponentRegistry.INSTANCE.getComponentID(component.getClass());
				if(type == null) {
					throw new RuntimeException(String.format("IScriptComponent %s is not registered!", component.getClass()));
				}
				if(component instanceof ISerializableScriptComponent && ((ISerializableScriptComponent<?>)component).getSerializer() == NBTTagCompound.class) {
					@SuppressWarnings("unchecked")
					ISerializableScriptComponent<NBTTagCompound> serializable = (ISerializableScriptComponent<NBTTagCompound>) component;
					componentNbt.setTag("data", serializable.serialize(new NBTTagCompound()));
				}
				componentNbt.setString("type", type.toString());
				componentsNbt.appendTag(componentNbt);

				//Connection data
				NBTTagCompound connectionNbt = new NBTTagCompound();
				for(OutputPort<?> port : component.getOutputs()) {
					if(port.isConnected() && componentSet.contains(port.getConnectedPort().getComponent())) {
						NBTTagCompound portNbt = new NBTTagCompound();
						portNbt.setUniqueId("component", port.getConnectedPort().getComponent().getID());
						portNbt.setInteger("input", port.getConnectedPort().getID());
						connectionNbt.setTag("output." + port.getID(), portNbt);
					}
				}
				connectionNbt.setUniqueId("component", component.getID());
				connectionsNbt.appendTag(connectionNbt);
			} catch(Exception ex) {
				FMLLog.log(Level.ERROR, ex,
						"A IScriptComponent type %s has thrown an exception trying to write state. It will not persist",
						component.getClass().getName());
			}
		}

		nbt.setTag("components", componentsNbt);
		nbt.setTag("connections", connectionsNbt);

		return nbt;
	}

	/**
	 * Imports components from NBT
	 * @param script The script
	 * @param nbt The NBT
	 * @param newUuids Whether new UUIDs need to be generated. This is necessary if components are copied into the same script!
	 * @return
	 */
	public static List<IScriptComponent> importComponents(Script script, NBTTagCompound nbt, boolean newUuids) {
		List<IScriptComponent> importedComponents = new ArrayList<>();
		Map<UUID, IScriptComponent> componentIdMap = new HashMap<>();

		//Load components
		NBTTagList componentsNbt = nbt.getTagList("components", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < componentsNbt.tagCount(); i++) {
			NBTTagCompound componentNbt = componentsNbt.getCompoundTagAt(i);
			ResourceLocation type = new ResourceLocation(componentNbt.getString("type"));
			try {
				IScriptComponent component = ScriptComponentRegistry.INSTANCE.createComponent(type, script);
				if(component == null) {
					throw new NullPointerException();
				}
				if(component instanceof ISerializableScriptComponent && ((ISerializableScriptComponent<?>)component).getSerializer() == NBTTagCompound.class) {
					@SuppressWarnings("unchecked")
					ISerializableScriptComponent<NBTTagCompound> serializable = (ISerializableScriptComponent<NBTTagCompound>) component;
					serializable.deserialize(componentNbt.getCompoundTag("data"));
				}
				component.initPorts();
				componentIdMap.put(component.getID(), component);
				if(newUuids) {
					//Create new IDs
					component.setID(UUID.randomUUID());
				}
				importedComponents.add(component);
			} catch(Exception ex) {
				FMLLog.log(Level.ERROR, ex, "Could not create IScriptComponent with ID %s", type);
			}
		}

		//Load connections
		NBTTagList connectionsNbt = nbt.getTagList("connections", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < connectionsNbt.tagCount(); i++) {
			NBTTagCompound connectionNbt = connectionsNbt.getCompoundTagAt(i);
			UUID componentID = connectionNbt.getUniqueId("component");
			IScriptComponent component = componentIdMap.get(componentID);
			if(component != null) {
				for(OutputPort<?> port : component.getOutputs()) {
					if(connectionNbt.hasKey("output." + port.getID(), Constants.NBT.TAG_COMPOUND)) {
						NBTTagCompound portNbt = connectionNbt.getCompoundTag("output." + port.getID());
						UUID inputComponentID = portNbt.getUniqueId("component");
						IScriptComponent inputComponent = componentIdMap.get(inputComponentID);
						if(inputComponent != null) {
							int inputPortID = portNbt.getInteger("input");
							InputPort<?> input = null;
							for(InputPort<?> inputPort : inputComponent.getInputs()) {
								if(inputPort.getID() == inputPortID) {
									input = inputPort;
								}
							}
							if(input != null) {
								input.connect(port);
							}
						}
					}
				}
			}
		}

		return importedComponents;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return exportComponents(this.components);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		this.components.clear();
		this.components.addAll(importComponents(this, nbt, false));
	}
}
