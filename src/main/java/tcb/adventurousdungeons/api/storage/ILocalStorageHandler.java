package tcb.adventurousdungeons.api.storage;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public interface ILocalStorageHandler {
	/**
	 * Returns the world storage
	 * @return
	 */
	public IWorldStorage getWorldStorage();

	/**
	 * Adds a local storage to the world
	 * @param storage
	 * @return
	 */
	public boolean addLocalStorage(ILocalStorage storage);

	/**
	 * Removes a local storage from the world
	 * @param storage
	 * @return
	 */
	public boolean removeLocalStorage(ILocalStorage storage);

	/**
	 * Returns the local storage with the specified ID, if currently loaded
	 * @param id
	 * @return
	 */
	@Nullable
	public ILocalStorage getLocalStorage(StorageID id);

	/**
	 * Returns a list of all local storages of the specified type that intersect with the specified AABB
	 * @param aabb
	 * @param type
	 * @return
	 */
	public <T extends ILocalStorage> List<T> getLocalStorages(Class<T> type, AxisAlignedBB aabb, @Nullable Predicate<T> filter);
	
	/**
	 * Deletes the file (or entry if in a region) of
	 * the specified local storage
	 * @param storage
	 */
	public void deleteLocalStorageFile(ILocalStorage storage);

	/**
	 * Saves the local storage to a file (or entry if in a region)
	 * @param storage
	 */
	public void saveLocalStorageFile(ILocalStorage storage);

	/**
	 * Loads the local storage of the specified reference from
	 * a file or the region cache if the local storage uses a region
	 * @param reference
	 * @return
	 */
	@Nullable
	public ILocalStorage loadLocalStorage(LocalStorageReference reference);

	/**
	 * Unloads a local storage and saves to a file if necessary
	 * @param storage
	 * @return True if the storage was successfully unloaded
	 */
	public boolean unloadLocalStorage(ILocalStorage storage);

	/**
	 * Returns an unmodifiable list of all currently loaded local storages
	 * @return
	 */
	public Collection<ILocalStorage> getLoadedStorages();

	/**
	 * Ticks all local storages that implement {@link ITickable}
	 */
	public void tick();

	/**
	 * Returns the directory where the data of all local storages are saved
	 * @return
	 */
	public File getLocalStorageDirectory();

	/**
	 * Creates a local storage instance from the specified NBT
	 * @param nbt
	 * @param region
	 * @param packet
	 * @return
	 */
	public ILocalStorage createLocalStorageFromNBT(NBTTagCompound nbt, LocalRegion region, boolean packet);

	/**
	 * Returns the data NBT of the specified full local storage NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound getLocalStorageDataNBT(NBTTagCompound nbt);
	
	/**
	 * Saves a local storage instance to NBT
	 * @param nbt
	 * @param storage
	 * @param packet
	 * @return
	 */
	public NBTTagCompound saveLocalStorageToNBT(NBTTagCompound nbt, ILocalStorage storage, boolean packet);

	/**
	 * Returns the local region cache
	 * @return
	 */
	public LocalRegionCache getLocalRegionCache();
}
