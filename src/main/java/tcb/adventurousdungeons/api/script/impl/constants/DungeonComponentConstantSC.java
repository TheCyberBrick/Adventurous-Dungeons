package tcb.adventurousdungeons.api.script.impl.constants;

import net.minecraft.nbt.NBTTagCompound;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;
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
}
