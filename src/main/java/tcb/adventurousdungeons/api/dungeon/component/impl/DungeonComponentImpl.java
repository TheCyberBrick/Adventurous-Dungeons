package tcb.adventurousdungeons.api.dungeon.component.impl;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ComponentDataManager;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.api.storage.StorageUUID;

public class DungeonComponentImpl implements IDungeonComponent {
	protected static final DataParameter<String> NAME = ComponentDataManager.createKey(DungeonComponentImpl.class, DataSerializers.STRING);

	protected final IDungeon dungeon;
	protected final ComponentDataManager dataManager;
	private StorageID id;
	private boolean dead;

	public DungeonComponentImpl(IDungeon dungeon) {
		this.dungeon = dungeon;
		this.id = new StorageUUID(UUID.randomUUID());
		this.dataManager = new ComponentDataManager(this);
		this.init();
	}

	@Override
	public void init() {
		this.dataManager.register(NAME, "");
	}

	@Override
	public String getName() {
		String name = this.dataManager.get(NAME);
		return name.length() > 0 ? name : this.id.getStringID();
	}

	@Override
	public void setName(String name) {
		this.dataManager.set(NAME, name == null ? "" : name);
	}

	@Override
	public StorageID getID() {
		return this.id;
	}

	@Override
	public IDungeon getDungeon() {
		return this.dungeon;
	}

	@Override
	public World getWorld() {
		return this.dungeon.getWorldStorage().getWorld();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		this.id.writeToNBT(nbt);
		if(this.dataManager.get(NAME) != null) {
			nbt.setString("name", this.dataManager.get(NAME));
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.id = StorageID.readFromNBT(nbt);
		if(nbt.hasKey("name", Constants.NBT.TAG_STRING)) {
			this.dataManager.set(NAME, nbt.getString("name"));
		} else {
			this.dataManager.set(NAME, "");
		}
	}

	@Override
	public void setComponentState(NBTTagCompound nbt) {

	}

	@Override
	public void update() {

	}

	@Override
	public void onUnloaded() {

	}

	@Override
	public void onEvent(DungeonEvent event) {

	}

	@Override
	public boolean isDead() {
		return this.dead;
	}

	@Override
	public void setDead() {
		this.dead = true;
	}

	@Override
	public ComponentDataManager getDataManager() {
		return this.dataManager;
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {

	}
}
