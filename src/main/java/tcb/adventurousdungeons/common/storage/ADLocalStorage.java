package tcb.adventurousdungeons.common.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ComponentDataManager;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonUpdateEvent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonUpdateEvent.Phase;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.LocalRegion;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.clientbound.MessageAddDungeonComponent;
import tcb.adventurousdungeons.common.network.clientbound.MessageDungeonAABB;
import tcb.adventurousdungeons.common.network.clientbound.MessageDungeonComponentData;
import tcb.adventurousdungeons.common.network.clientbound.MessageRemoveDungeonComponent;
import tcb.adventurousdungeons.registries.DungeonComponentRegistry;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public class ADLocalStorage extends LocalStorageImpl implements IDungeon {
	private Map<StorageID, IDungeonComponent> components = new HashMap<>();
	private AxisAlignedBB boundingBox;

	private boolean sendAABB = false;

	public ADLocalStorage(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region);
	}

	/**
	 * Sets the dungeon bounding box
	 * @param boundingBox
	 */
	public void setBoundingBox(@Nonnull AxisAlignedBB boundingBox) {
		if(boundingBox == null) {
			throw new NullPointerException("Dungeon bounding box must not be null");
		}
		this.boundingBox = boundingBox;
		this.sendAABB = true;
		this.setDirty(true);
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public void addDungeonComponent(IDungeonComponent component) {
		this.components.put(component.getID(), component);
		if(!this.getWorldStorage().getWorld().isRemote) {
			this.sendDataToAllWatchers(new MessageAddDungeonComponent(this, component));
		}
		this.setDirty(true);
	}

	@Override
	public void removeDungeonComponent(IDungeonComponent component) {
		this.components.remove(component.getID());
		if(!this.getWorldStorage().getWorld().isRemote) {
			this.sendDataToAllWatchers(new MessageRemoveDungeonComponent(this, component));
		}
		this.setDirty(true);
	}

	@Override
	public IDungeonComponent getDungeonComponent(StorageID id) {
		return this.components.get(id);
	}

	@Override
	public Collection<IDungeonComponent> getDungeonComponents() {
		return Collections.unmodifiableCollection(this.components.values());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		this.readAABB(nbt);
		this.readDungeonComponents(nbt);
	}

	protected void readAABB(NBTTagCompound nbt) {
		this.boundingBox = null;
		if(nbt.hasKey("aabb", Constants.NBT.TAG_COMPOUND)) {
			this.boundingBox = MoreNBTUtils.readAABB(nbt.getCompoundTag("aabb"));
		}
	}

	protected void readDungeonComponents(NBTTagCompound nbt) {
		this.components.clear();
		if(nbt.hasKey("components", Constants.NBT.TAG_LIST)) {
			NBTTagList components = nbt.getTagList("components", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < components.tagCount(); i++) {
				NBTTagCompound componentNbt = components.getCompoundTagAt(i);
				IDungeonComponent component = this.readDungeonComponent(componentNbt);
				if(component != null) {
					this.components.put(component.getID(), component);
				}
			}
		}
	}

	@Nullable
	public IDungeonComponent readDungeonComponent(NBTTagCompound nbt) {
		ResourceLocation id = new ResourceLocation(nbt.getString("id"));
		try {
			IDungeonComponent component = DungeonComponentRegistry.INSTANCE.createComponent(id, this);
			component.readFromNBT(nbt.getCompoundTag("data"));
			return component;
		} catch(Exception ex) {
			FMLLog.log(Level.ERROR, ex, "Could not create IDungeonComponent with ID %s", id);
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);

		this.writeAABB(nbt);
		this.writeDungeonComponents(nbt);

		return nbt;
	}

	protected void writeAABB(NBTTagCompound nbt) {
		nbt.setTag("aabb", MoreNBTUtils.writeAABB(this.boundingBox));
	}

	protected void writeDungeonComponents(NBTTagCompound nbt) {
		NBTTagList components = new NBTTagList();
		for(IDungeonComponent component : this.components.values()) {
			NBTTagCompound componentNbt = this.writeDungeonComponent(component);
			if(componentNbt != null) {
				components.appendTag(componentNbt);
			}
		}
		nbt.setTag("components", components);
	}

	@Nullable
	public NBTTagCompound writeDungeonComponent(IDungeonComponent component) {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			ResourceLocation id = DungeonComponentRegistry.INSTANCE.getComponentID(component.getClass());
			if(id == null) {
				throw new RuntimeException(String.format("IDungeonComponent %s is not registered!", component.getClass()));
			}
			nbt.setString("id", id.toString());
			nbt.setTag("data", component.writeToNBT(new NBTTagCompound()));
			return nbt;
		} catch(Exception ex) {
			FMLLog.log(Level.ERROR, ex,
					"A IDungeonComponent type %s has thrown an exception trying to write state. It will not persist",
					component.getClass().getName());
		}
		return null;
	}

	@Override
	public void readFromPacketNBT(NBTTagCompound nbt) {
		super.readFromPacketNBT(nbt);

		this.readAABB(nbt);
		this.readDungeonComponents(nbt);
	}

	@Override
	public NBTTagCompound writeToPacketNBT(NBTTagCompound nbt) {
		super.writeToPacketNBT(nbt);

		this.writeAABB(nbt);
		this.writeDungeonComponents(nbt);

		return nbt;
	}

	@Override
	public void update() {
		DungeonUpdateEvent preUpdate = new DungeonUpdateEvent(this, Phase.START);
		this.fireEvent(preUpdate);
		MinecraftForge.EVENT_BUS.post(preUpdate);

		super.update();

		if(this.sendAABB) {
			this.sendAABB = false;
			this.sendDataToAllWatchers(new MessageDungeonAABB(this, this.getBoundingBox()));
		}

		//TODO Blehh
		List<IDungeonComponent> components = new ArrayList<>(this.components.values());
		for(IDungeonComponent component : components) {
			if(component.isDead()) {
				this.removeDungeonComponent(component);
			} else {
				component.update();

				if(!this.getWorldStorage().getWorld().isRemote) {
					ComponentDataManager dataManager = component.getDataManager();
					if(dataManager.isDirty()) {
						this.sendDataToAllWatchers(new MessageDungeonComponentData(component, false));
					}
				}
			}
		}

		DungeonUpdateEvent postUpdate = new DungeonUpdateEvent(this, Phase.END);
		this.fireEvent(postUpdate);
		MinecraftForge.EVENT_BUS.post(postUpdate);
	}

	@Override
	protected void onWatched(EntityPlayerMP player) {
		super.onWatched(player);

		for(IDungeonComponent component : this.components.values()) {
			this.sendDataToPlayer(new MessageDungeonComponentData(component, true), player);
		}
	}

	@Override
	public void onUnloaded() {
		super.onUnloaded();

		for(IDungeonComponent component : this.components.values()) {
			component.onUnloaded();
		}
	}

	@Override
	public DungeonEvent fireEvent(DungeonEvent event) {
		for(IDungeonComponent component : this.components.values()) {
			component.onEvent(event);
		}
		return event;
	}

	@Override
	public boolean canPlayerEdit(EntityPlayer player) {
		if(player instanceof EntityPlayerMP) {
			int permLevel = 2;
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			UserListOpsEntry opsEntry = (UserListOpsEntry)playerMP.mcServer.getPlayerList().getOppedPlayers().getEntry(playerMP.getGameProfile());
			return opsEntry != null ? opsEntry.getPermissionLevel() >= permLevel : playerMP.mcServer.getOpPermissionLevel() >= permLevel;
		}
		return false;
		//return player instanceof EntityPlayerMP ? ((EntityPlayerMP)player).mcServer.getPlayerList().getOppedPlayers().getPermissionLevel(player.getGameProfile()) >= 2 : false;
	}
}
