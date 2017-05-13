package tcb.adventurousdungeons.common.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.api.event.AttachChunkStorageCapabilitiesEvent;
import tcb.adventurousdungeons.api.storage.IChunkStorage;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.LocalStorageReference;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.clientbound.MessageSyncChunkStorage;

public abstract class ChunkStorageImpl implements IChunkStorage {
	protected final IWorldStorage worldStorage;
	protected final World world;
	protected final Chunk chunk;

	private Set<EntityPlayerMP> watchers = new HashSet<>();
	private boolean dirty = false;

	private CapabilityDispatcher capabilities;

	private final List<LocalStorageReference> localStorageReferences = new ArrayList<>();

	public ChunkStorageImpl(IWorldStorage worldStorage, Chunk chunk) {
		this.worldStorage = worldStorage;
		this.world = worldStorage.getWorld();
		this.chunk = chunk;

		//Gather capabilities
		AttachChunkStorageCapabilitiesEvent event = new AttachChunkStorageCapabilitiesEvent(this);
		MinecraftForge.EVENT_BUS.post(event);
		this.capabilities = event.getCapabilities().size() > 0 ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.capabilities == null ? false : this.capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return this.capabilities == null ? null : this.capabilities.getCapability(capability, facing);
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public Chunk getChunk() {
		return this.chunk;
	}

	@Override
	public void init() {

	}

	@Override
	public void onUnload() {
		for(LocalStorageReference ref : this.localStorageReferences) {
			ILocalStorage localStorage = this.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());
			if(localStorage != null) {
				localStorage.unloadReference(ref);
				if(localStorage.getLoadedReferences().isEmpty()) {
					this.getWorldStorage().getLocalStorageHandler().unloadLocalStorage(localStorage);
				}
			}
		}
	}

	@Override
	public void setDefaults() {

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(this.capabilities != null && nbt.hasKey("ForgeCaps")) {
			this.capabilities.deserializeNBT(nbt.getCompoundTag("ForgeCaps"));
		}

		this.localStorageReferences.clear();
		NBTTagList localReferenceList = nbt.getTagList("LocalStorageReferences", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < localReferenceList.tagCount(); i++) {
			this.localStorageReferences.add(LocalStorageReference.readFromNBT((NBTTagCompound)localReferenceList.get(i)));
		}

		Iterator<LocalStorageReference> refIT = this.localStorageReferences.iterator();
		while(refIT.hasNext()) {
			LocalStorageReference ref = refIT.next();
			ILocalStorage storage = this.worldStorage.getLocalStorageHandler().getLocalStorage(ref.getID());

			//Not cached, load from file
			if(!this.worldStorage.getWorld().isRemote && storage == null) {
				storage = this.worldStorage.getLocalStorageHandler().loadLocalStorage(ref);
			}

			//Load reference if properly linked
			if(storage != null && storage.getLinkedChunks().contains(this.chunk.getPos())) {
				storage.loadReference(ref);
			} else if(!this.worldStorage.getWorld().isRemote) {
				//Local storage doesn't exist or chunk shouldn't be linked to local storage, remove link
				refIT.remove();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if(this.capabilities != null) {
			NBTTagCompound caps = this.capabilities.serializeNBT();
			if(caps.getSize() > 0) {
				nbt.setTag("ForgeCaps", caps);
			}
		}

		if(!this.localStorageReferences.isEmpty()) {
			NBTTagList localReferenceList = new NBTTagList();
			for(LocalStorageReference ref : this.localStorageReferences) {
				localReferenceList.appendTag(ref.writeToNBT(new NBTTagCompound()));
			}
			nbt.setTag("LocalStorageReferences", localReferenceList);
		}

		return nbt;
	}

	@Override
	public boolean addWatcher(EntityPlayerMP player) {
		if(this.watchers.add(player)) {
			this.onWatched(player);
			return true;
		}
		return false;
	}

	/**
	 * Called when a new watcher is added
	 * @param player
	 */
	protected void onWatched(EntityPlayerMP player) {
		for(LocalStorageReference ref : this.localStorageReferences) {
			ILocalStorage localStorage = this.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());
			if(localStorage != null) {
				localStorage.addWatcher(this, player);
			}
		}

		AdventurousDungeons.getNetwork().sendTo(new MessageSyncChunkStorage(this), player);
	}

	@Override
	public boolean removeWatcher(EntityPlayerMP player) {
		if(this.watchers.remove(player)) {
			this.onUnwatched(player);
			return true;
		}
		return false;
	}

	/**
	 * Called when a player stops watching this chunk
	 * @param player
	 */
	protected void onUnwatched(EntityPlayerMP player) {
		for(LocalStorageReference ref : this.localStorageReferences) {
			ILocalStorage localStorage = this.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());
			if(localStorage != null) {
				localStorage.removeWatcher(this, player);
			}
		}
	}

	@Override
	public Set<EntityPlayerMP> getWatchers() {
		return Collections.unmodifiableSet(this.watchers);
	}

	@Override
	public void markDirty() {
		this.setDirty(true);
	}

	@Override
	public void setDirty(boolean dirty) {
		if(dirty) {
			this.chunk.setChunkModified();
		}
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public LocalStorageReference getReference(StorageID id) {
		for(LocalStorageReference ref : this.localStorageReferences) {
			if(id.equals(ref.getID()))
				return ref;
		}
		return null;
	}

	@Override
	public boolean unlinkLocalStorage(ILocalStorage storage) {
		StorageID id = storage.getID();
		List<LocalStorageReference> unlinkedReferences = new ArrayList<>();
		Iterator<LocalStorageReference> referenceIT = this.localStorageReferences.iterator();
		while(referenceIT.hasNext()) {
			LocalStorageReference ref = referenceIT.next();
			if(id.equals(ref.getID())) {
				unlinkedReferences.add(ref);
				referenceIT.remove();
			}
		}
		if(!unlinkedReferences.isEmpty()) {
			//Make sure the local storage is also unlinked from this chunk
			for(LocalStorageReference ref : unlinkedReferences) {
				if(storage.getLoadedReferences().contains(ref)) {
					storage.unlinkChunk(this.chunk);
				}
			}
			this.markDirty();
			return true;
		}
		return false;
	}

	@Override
	public boolean linkLocalStorage(ILocalStorage storage) {
		StorageID id = storage.getID();
		for(LocalStorageReference ref : this.localStorageReferences) {
			if(id.equals(ref.getID()))
				return false;
		}
		LocalStorageReference ref = new LocalStorageReference(this.chunk.getPos(), id, storage.getRegion());
		if(this.localStorageReferences.add(ref)) {
			this.markDirty();
			return true;
		}
		return false;
	}

	@Override
	public Collection<LocalStorageReference> getLocalStorageReferences() {
		return Collections.unmodifiableCollection(this.localStorageReferences);
	}
}
