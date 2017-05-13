package tcb.adventurousdungeons.api.dungeon.component.impl;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ComponentDataManager;
import tcb.adventurousdungeons.api.dungeon.component.serializers.ComponentDataSerializers;
import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.script.IDungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.Script;

public class ScriptDC extends DungeonComponentImpl {
	protected static final DataParameter<Script> SCRIPT = ComponentDataManager.createKey(ScriptDC.class, ComponentDataSerializers.SCRIPT);

	public ScriptDC(IDungeon dungeon) {
		super(dungeon);
	}

	@Override
	public void init() {
		super.init();

		this.dataManager.register(SCRIPT, new Script());
	}

	public NBTTagCompound getScriptNbtCopy() {
		return this.getScript().writeToNBT(new NBTTagCompound());
	}

	public void setScript(Script script) {
		this.dataManager.set(SCRIPT, script);
		for(IScriptComponent component : script.getComponents()) {
			if(component instanceof IDungeonScriptComponent) {
				((IDungeonScriptComponent)component).setDungeonComponent(this);
			}
		}
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if(key.equals(SCRIPT)) {
			for(IScriptComponent component : this.getScript().getComponents()) {
				if(component instanceof IDungeonScriptComponent) {
					((IDungeonScriptComponent)component).setDungeonComponent(this);
				}
			}
		}
	}

	private Script getScript() {
		return this.dataManager.get(SCRIPT);
	}

	@Override
	public void onEvent(DungeonEvent event) {
		Script script = this.getScript();
		for(IScriptComponent component : script.getComponents()) {
			if(component instanceof IDungeonScriptComponent) {
				((IDungeonScriptComponent)component).onEvent(event);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound scriptNbt = this.getScript().writeToNBT(new NBTTagCompound());
		nbt.setTag("script", scriptNbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("script", Constants.NBT.TAG_COMPOUND)) {
			Script script = new Script();
			script.readFromNBT(nbt.getCompoundTag("script"));
			this.setScript(script);
		}
	}
}
