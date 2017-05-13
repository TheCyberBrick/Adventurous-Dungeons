package tcb.adventurousdungeons.api.dungeon.component.impl;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ComponentDataManager;
import tcb.adventurousdungeons.util.CatmullRomSpline;
import tcb.adventurousdungeons.util.ReparameterizedSpline;

public class MoveEntitySplineDC extends DungeonComponentImpl {
	protected static final DataParameter<Integer> ENTITY_ID = ComponentDataManager.createKey(MoveEntitySplineDC.class, DataSerializers.VARINT);

	private UUID targetUUID;
	private boolean isPlayer;

	private CatmullRomSpline spline;
	private int duration, ticks;

	private ReparameterizedSpline pather;

	public MoveEntitySplineDC(IDungeon dungeon) {
		super(dungeon);
	}

	public MoveEntitySplineDC(IDungeon dungeon, CatmullRomSpline spline, int duration, Entity target) {
		super(dungeon);
		this.spline = spline;
		this.pather = new ReparameterizedSpline(spline);
		this.pather.init(128, 32);
		this.duration = duration;
		this.setTarget(target);
		this.isPlayer = target instanceof EntityPlayer;
	}

	@Override
	public void init() {
		super.init();
		this.dataManager.register(ENTITY_ID, -1);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("duration", this.duration);
		nbt.setInteger("ticks", this.ticks);
		nbt.setTag("spline", this.spline.writeToNBT(new NBTTagCompound()));
		nbt.setUniqueId("entityUuid", this.targetUUID);
		nbt.setBoolean("isPlayer", this.isPlayer);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.duration = nbt.getInteger("duration");
		this.ticks = nbt.getInteger("ticks");
		this.spline = CatmullRomSpline.readFromNBT(nbt.getCompoundTag("spline"));
		this.pather = new ReparameterizedSpline(this.spline);
		long ms = System.currentTimeMillis();
		this.pather.init(128, 32);
		System.out.println("ms: " + (System.currentTimeMillis() - ms));
		this.targetUUID = nbt.getUniqueId("entityUuid");
		this.isPlayer = nbt.getBoolean("isPlayer");
	}

	@Override
	public void update() {
		super.update();

		Entity target = this.getTarget();

		this.getDataManager().set(ENTITY_ID, target == null ? -1 : target.getEntityId());

		if(target != null && this.pather != null) {
			if(this.ticks == 0) {
				System.out.println("Len: " + this.pather.getLength());
				System.out.println("Start: " + target.getPositionVector());
				System.out.println("Time: " + System.currentTimeMillis());
			}

			Vec3d pos = this.pather.interpolate(this.ticks / (float)this.duration);

			target.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
			target.motionX = target.motionY = target.motionZ = 0.0D;
			target.fallDistance = 0.0F;

			if(this.ticks >= this.duration) {
				System.out.println("End: " + target.getPositionVector());
				System.out.println("Time: " + System.currentTimeMillis());
				this.setDead();
			} else {
				this.ticks++;
			}
		} else /*if(!this.isPlayer)*/ {
			this.setDead();
		}
	}

	public boolean isPlayer() {
		return this.isPlayer;
	}

	public void setTarget(@Nullable Entity entity) {
		this.targetUUID = entity == null ? null : entity.getUniqueID();
		this.getDataManager().set(ENTITY_ID, entity == null ? -1 : entity.getEntityId());
	}

	@Nullable
	public Entity getTarget() {
		World world = this.getDungeon().getWorldStorage().getWorld();
		if(world instanceof WorldServer) {
			if(this.targetUUID == null) {
				return null;
			}
			return ((WorldServer)world).getEntityFromUuid(this.targetUUID);
		} else {
			int id = this.getDataManager().get(ENTITY_ID);
			if(id < 0) {
				return null;
			}
			return world.getEntityByID(id);
		}
	}
}
