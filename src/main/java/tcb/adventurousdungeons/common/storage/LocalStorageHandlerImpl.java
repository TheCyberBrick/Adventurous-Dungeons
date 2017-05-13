package tcb.adventurousdungeons.common.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import tcb.adventurousdungeons.api.storage.IChunkStorage;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.ILocalStorageHandler;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.LocalRegion;
import tcb.adventurousdungeons.api.storage.LocalRegionCache;
import tcb.adventurousdungeons.api.storage.LocalRegionData;
import tcb.adventurousdungeons.api.storage.LocalStorageReference;
import tcb.adventurousdungeons.api.storage.StorageID;

public class LocalStorageHandlerImpl implements ILocalStorageHandler {
	private final IWorldStorage worldStorage;
	private final World world;
	private final File localStorageDir;

	private final Map<StorageID, ILocalStorage> localStorage = new HashMap<StorageID, ILocalStorage>();
	private final List<ILocalStorage> tickableLocalStorage = new ArrayList<>();

	private final LocalRegionCache regionCache;

	public LocalStorageHandlerImpl(IWorldStorage worldStorage) {
		this.worldStorage = worldStorage;
		this.world = worldStorage.getWorld();
		String dimFolder = this.world.provider.getSaveFolder();
		this.localStorageDir = new File(this.world.getSaveHandler().getWorldDirectory(), (dimFolder != null && dimFolder.length() > 0 ? dimFolder + File.separator : "") + "data" + File.separator + "local_storage" + File.separator);
		this.regionCache = new LocalRegionCache(new File(this.localStorageDir, "region"));
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public boolean addLocalStorage(ILocalStorage storage) {
		if(!this.localStorage.containsKey(storage.getID()) && !storage.getLinkedChunks().isEmpty()) {
			this.localStorage.put(storage.getID(), storage);

			if(storage instanceof ITickable) {
				this.tickableLocalStorage.add(storage);
			}

			//Add already loaded references and watchers
			for(ChunkPos referenceChunk : storage.getLinkedChunks()) {
				Chunk chunk = this.world.getChunkProvider().getLoadedChunk(referenceChunk.chunkXPos, referenceChunk.chunkZPos);
				if(chunk != null) {
					IChunkStorage chunkStorage = this.worldStorage.getChunkStorage(chunk);
					if(chunkStorage != null) {
						LocalStorageReference reference = chunkStorage.getReference(storage.getID());
						if(reference != null && !storage.getLoadedReferences().contains(reference)) {
							//Add reference
							storage.loadReference(reference);

							//Add watchers
							for(EntityPlayerMP watcher : chunkStorage.getWatchers()) {
								storage.addWatcher(chunkStorage, watcher);
							}
						}
					}
				}
			}

			storage.onLoaded();

			return true;
		}
		return false;
	}

	@Override
	public boolean removeLocalStorage(ILocalStorage storage) {
		if(this.localStorage.containsKey(storage.getID())) {
			if(!this.world.isRemote) {
				storage.unlinkAllChunks();
			}

			this.localStorage.remove(storage.getID());

			Iterator<ILocalStorage> it = this.tickableLocalStorage.iterator();
			ILocalStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getID().equals(tickableStorage.getID())) {
					it.remove();
				}
			}

			if(!this.world.isRemote) {
				this.deleteLocalStorageFile(storage);
			}

			storage.onUnloaded();

			storage.onRemoved();

			return true;
		}
		return false;
	}

	@Override
	public ILocalStorage getLocalStorage(StorageID id) {
		return this.localStorage.get(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, AxisAlignedBB aabb, @Nullable Predicate<T> filter) {
		List<T> storages = new ArrayList<>();
		int sx = MathHelper.floor(aabb.minX / 16);
		int sz = MathHelper.floor(aabb.minZ / 16);
		int ex = MathHelper.floor(aabb.maxX / 16);
		int ez = MathHelper.floor(aabb.maxZ / 16);
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = this.world.getChunkFromChunkCoords(cx, cz);
				IChunkStorage chunkStorage = this.getWorldStorage().getChunkStorage(chunk);
				for(LocalStorageReference ref : chunkStorage.getLocalStorageReferences()) {
					ILocalStorage localStorage = this.getLocalStorage(ref.getID());
					if(localStorage != null && localStorage.getBoundingBox() != null && type.isAssignableFrom(localStorage.getClass()) && localStorage.getBoundingBox().intersectsWith(aabb)
							&& (filter == null || filter.apply((T) localStorage))) {
						storages.add((T) localStorage);
					}
				}
			}
		}
		return storages;
	}

	@Override
	public void deleteLocalStorageFile(ILocalStorage storage) {
		if(storage.getRegion() == null) {
			File file = new File(this.getLocalStorageDirectory(), storage.getID().getStringID() + ".dat");
			if(file.exists()) {
				file.delete();
			}
		} else {
			LocalRegionData regionData = this.regionCache.getOrCreateRegion(storage.getRegion());
			if(regionData != null) {
				regionData.deleteLocalStorage(this.regionCache.getDir(), storage.getID());
			}
		}
	}

	@Override
	public void saveLocalStorageFile(ILocalStorage storage) {
		NBTTagCompound nbt = this.saveLocalStorageToNBT(new NBTTagCompound(), storage, false);
		if(storage.getRegion() == null) {
			try {
				File savePath = this.getLocalStorageDirectory();
				savePath.mkdirs();
				File file = new File(savePath, storage.getID().getStringID() + ".dat");
				CompressedStreamTools.safeWrite(nbt, file);
			} catch(Exception ex) {
				throw new RuntimeException(ex);
			}
		} else {
			LocalRegionData region = this.regionCache.getOrCreateRegion(storage.getRegion());
			region.setLocalStorageNBT(storage.getID(), nbt);
		}
	}

	@Override
	public ILocalStorage loadLocalStorage(LocalStorageReference reference) {
		ILocalStorage storage = this.createLocalStorageFromFile(reference);
		if(storage != null) {
			this.addLocalStorage(storage);
			storage.onLoaded();

			if(storage.getRegion() != null) {
				LocalRegionData data = this.regionCache.getOrCreateRegion(reference.getRegion());
				data.incrRefCounter();
			}

			return storage;
		}
		return null;
	}

	/**
	 * Creates an instance of the local storage specified by the reference
	 * @param reference
	 * @return
	 */
	@Nullable
	private ILocalStorage createLocalStorageFromFile(LocalStorageReference reference) {
		if(!reference.hasRegion()) {
			File file = new File(this.getLocalStorageDirectory(), reference.getID().getStringID() + ".dat");
			if(file.exists()) {
				try {
					NBTTagCompound nbt = CompressedStreamTools.read(file);
					return this.createLocalStorageFromNBT(nbt, null, false);
				} catch(Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		} else {
			LocalRegionData region = this.regionCache.getOrCreateRegion(reference.getRegion());
			NBTTagCompound nbt = region.getLocalStorageNBT(reference.getID());
			if(nbt != null) {
				return this.createLocalStorageFromNBT(nbt, reference.getRegion(), false);
			}
		}
		return null;
	}

	@Override
	public boolean unloadLocalStorage(ILocalStorage storage) {
		if(this.localStorage.containsKey(storage.getID())) {
			//Only save if dirty
			if(!this.world.isRemote && storage.isDirty()) {
				this.saveLocalStorageFile(storage);
				storage.setDirty(false);
			}

			this.localStorage.remove(storage.getID());

			Iterator<ILocalStorage> it = this.tickableLocalStorage.iterator();
			ILocalStorage tickableStorage = null;
			while(it.hasNext()) {
				tickableStorage = it.next();
				if(storage.getID().equals(tickableStorage.getID())) {
					it.remove();
				}
			}

			storage.onUnloaded();

			if(!this.world.isRemote && storage.getRegion() != null) {
				LocalRegionData data = this.regionCache.getOrCreateRegion(storage.getRegion());
				data.decrRefCounter();

				if(!data.hasReferences()) {
					if(data.isDirty()) {
						data.saveRegion(this.regionCache.getDir());
					}
					this.regionCache.removeRegion(storage.getRegion());
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public Collection<ILocalStorage> getLoadedStorages() {
		return Collections.unmodifiableCollection(this.localStorage.values());
	}

	@Override
	public File getLocalStorageDirectory() {
		return this.localStorageDir;
	}

	@Override
	public void tick() {
		for(int i = 0; i < this.tickableLocalStorage.size(); i++) {
			ILocalStorage localStorage = this.tickableLocalStorage.get(i);
			((ITickable)localStorage).update();
		}
	}

	@Override
	public ILocalStorage createLocalStorageFromNBT(NBTTagCompound nbt, @Nullable LocalRegion region, boolean packet) {
		try {
			ILocalStorage storage = new ADLocalStorage(this.worldStorage, StorageID.readFromNBT(nbt), region);
			if(packet) {
				storage.readFromPacketNBT(nbt.getCompoundTag("data"));
			} else {
				storage.readFromNBT(nbt.getCompoundTag("data"));
			}
			return storage;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public NBTTagCompound getLocalStorageDataNBT(NBTTagCompound nbt) {
		return nbt.getCompoundTag("data");
	}

	@Override
	public NBTTagCompound saveLocalStorageToNBT(NBTTagCompound nbt, ILocalStorage storage, boolean packet) {
		storage.getID().writeToNBT(nbt);
		if(packet) {
			nbt.setTag("data", storage.writeToPacketNBT(new NBTTagCompound()));
		} else {
			nbt.setTag("data", storage.writeToNBT(new NBTTagCompound()));
		}
		return nbt;
	}

	@Override
	public LocalRegionCache getLocalRegionCache() {
		return this.regionCache;
	}
}
