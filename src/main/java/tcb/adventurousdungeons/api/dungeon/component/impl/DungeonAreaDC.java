package tcb.adventurousdungeons.api.dungeon.component.impl;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ComponentDataManager;
import tcb.adventurousdungeons.api.dungeon.component.ILocalDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.serializers.ComponentDataSerializers;
import tcb.adventurousdungeons.util.AABBUtil;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public class DungeonAreaDC extends DungeonComponentImpl implements ILocalDungeonComponent {
	protected static final DataParameter<AxisAlignedBB> AABB = ComponentDataManager.createKey(DungeonAreaDC.class, ComponentDataSerializers.AABB);

	private AABBUtil helper;

	public DungeonAreaDC(IDungeon dungeon) {
		super(dungeon);
	}

	public DungeonAreaDC(IDungeon dungeon, AxisAlignedBB aabb) {
		this(dungeon);
		this.setBounds(aabb);
	}

	@Override
	public void init() {
		super.init();

		this.dataManager.register(AABB, new AxisAlignedBB(BlockPos.ORIGIN));
	}

	@Override
	public void setBounds(AxisAlignedBB aabb) {
		this.dataManager.set(AABB, aabb);
		this.helper = new AABBUtil(this.dungeon.getWorldStorage().getWorld(), aabb);
		this.getDungeon().markDirty();
	}

	@Override
	public AABBUtil getBounds() {
		return this.helper;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setTag("aabb", MoreNBTUtils.writeAABB(this.dataManager.get(AABB)));
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.setBounds(MoreNBTUtils.readAABB(nbt.getCompoundTag("aabb")));
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if(key.equals(AABB)) {
			this.helper = new AABBUtil(this.getWorld(), this.dataManager.get(AABB));
		}
	}
}
