package tcb.adventurousdungeons.api.script.impl.constants;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;
import tcb.adventurousdungeons.api.script.gui.GuiCreateScriptComponent;
import tcb.adventurousdungeons.api.storage.StorageID;

/**
 * This component returns the specified dungeon component
 */
public class DungeonComponentConstantSC extends DungeonScriptComponent {
	private OutputPort<IDungeonComponent> out;

	private StorageID componentID;

	public DungeonComponentConstantSC(Script script) {
		super(script);
	}

	public DungeonComponentConstantSC(Script script, String name, IDungeonComponent component) {
		super(script, name);
		this.componentID = component.getID();
	}

	public StorageID getComponentID() {
		return this.componentID;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", IDungeonComponent.class);		
	}

	@Override
	protected void run() throws ScriptException {
		IDungeonComponent component = this.getDungeonComponent().getDungeon().getDungeonComponent(this.componentID);
		if(component == null) {
			throw new ScriptException(this, String.format("Dungeon component with ID %s does not exist", this.componentID.toString()));
		}
		this.put(this.out, component);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		this.componentID.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.componentID = StorageID.readFromNBT(nbt);
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<DungeonComponentConstantSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable DungeonComponentConstantSC component, float x, float y) {
			return new FactoryGui(parent, DungeonComponentConstantSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<DungeonComponentConstantSC> {
		public FactoryGui(GuiScreen parent, Class<DungeonComponentConstantSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable DungeonComponentConstantSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		protected DungeonComponentConstantSC create(Script script, String name) {
			IDungeonComponent component = this.getInputComponent().getDungeonComponent().getDungeon().getDungeonComponent(this.getInputComponent().getComponentID());
			if(component != null) {
				return new DungeonComponentConstantSC(script, name, component);
			}
			return null;
		}
	}
}
