package tcb.adventurousdungeons.api.dungeon.component.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.event.EntityAreaTriggerEvent;

public class EntityAreaTriggerDC extends DungeonAreaDC {
	private final Set<Entity> prevEntities = new HashSet<>();
	private final Set<UUID> currentEntitiesInside = new HashSet<>();

	public EntityAreaTriggerDC(IDungeon dungeon) {
		super(dungeon);
	}

	public EntityAreaTriggerDC(IDungeon dungeon, AxisAlignedBB aabb) {
		super(dungeon, aabb);
	}

	@Override
	public void update() {
		if(this.getBounds() != null) {
			List<Entity> currentEntities = this.getBounds().getEntities();

			Set<Entity> entitiesEntered = new HashSet<>();
			entitiesEntered.addAll(currentEntities);
			entitiesEntered.removeAll(this.prevEntities);
			this.prevEntities.removeAll(currentEntities);

			for(Entity entity : entitiesEntered) {
				if(this.currentEntitiesInside.add(entity.getPersistentID())) {
					this.onEntityEnter(entity);
					this.dungeon.setDirty(true);
				}
			}
			for(Entity entity : this.prevEntities) {
				this.onEntityLeave(entity);
				this.currentEntitiesInside.remove(entity.getPersistentID());
				this.dungeon.setDirty(true);
			}

			this.prevEntities.clear();
			this.prevEntities.addAll(currentEntities);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		NBTTagList uuids = new NBTTagList();
		for(UUID uuid : this.currentEntitiesInside) {
			NBTTagCompound uuidNbt = new NBTTagCompound();
			uuidNbt.setUniqueId("id", uuid);
			uuids.appendTag(uuidNbt);
		}
		nbt.setTag("currentEntitiesInside", uuids);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.currentEntitiesInside.clear();
		NBTTagList uuids = nbt.getTagList("currentEntitiesInside", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < uuids.tagCount(); i++) {
			NBTTagCompound uuidNbt = uuids.getCompoundTagAt(i);
			this.currentEntitiesInside.add(uuidNbt.getUniqueId("id"));
		}
	}

	protected void onEntityEnter(Entity entity) {
		//System.out.println("Entity enter: " + entity);

		EntityAreaTriggerEvent event = new EntityAreaTriggerEvent(this, entity, true);
		this.dungeon.fireEvent(event);
		MinecraftForge.EVENT_BUS.post(event);
	}

	protected void onEntityLeave(Entity entity) {
		//System.out.println("Entity exit: " + entity);

		EntityAreaTriggerEvent event = new EntityAreaTriggerEvent(this, entity, false);
		this.dungeon.fireEvent(event);
		MinecraftForge.EVENT_BUS.post(event);
	}
}
