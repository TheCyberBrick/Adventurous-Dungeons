package tcb.adventurousdungeons.common.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public final class WorldEventHandler {
	public static final String CHUNK_NBT_TAG = ModInfo.ID + ":chunk_data";

	private static final Map<Chunk, IWorldStorage> UNLOAD_QUEUE = new HashMap<>();

	@SubscribeEvent
	public static void onChunkLoad(ChunkEvent.Load event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			cap.loadChunk(event.getChunk());
		}
	}

	@SubscribeEvent
	public static void onChunkRead(ChunkDataEvent.Load event) {
		if(event.getData().hasKey(CHUNK_NBT_TAG, Constants.NBT.TAG_COMPOUND)) {
			IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
			if(cap != null) {
				cap.readAndLoadChunk(event.getChunk(), event.getData().getCompoundTag(CHUNK_NBT_TAG));
			}
		}
	}

	@SubscribeEvent
	public static void onChunkUnload(ChunkEvent.Unload event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			if(event.getWorld().isRemote) {
				//Unload immediately on client side
				cap.unloadChunk(event.getChunk());
			} else {
				UNLOAD_QUEUE.put(event.getChunk(), cap);
			}
		}
	}

	@SubscribeEvent
	public static void onChunkSave(ChunkDataEvent.Save event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getWorld());
		if(cap != null) {
			NBTTagCompound nbt = cap.saveChunk(event.getChunk());
			if(nbt != null) {
				event.getData().setTag(CHUNK_NBT_TAG, nbt);
			}
			if(!event.getChunk().isLoaded()) {
				//Unload immediately after saving
				cap.unloadChunk(event.getChunk());
				UNLOAD_QUEUE.remove(event.getChunk());
			}
		}
	}

	@SubscribeEvent
	public static void onWatchChunk(ChunkWatchEvent.Watch event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getPlayer().getEntityWorld());
		if(cap != null) {
			cap.watchChunk(event.getChunk(), event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onUnwatchChunk(ChunkWatchEvent.UnWatch event) {
		IWorldStorage cap = WorldStorageImpl.getCapability(event.getPlayer().getEntityWorld());
		if(cap != null) {
			cap.unwatchChunk(event.getChunk(), event.getPlayer());
		}
	}

	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event) {
		IWorldStorage worldStorage = WorldStorageImpl.getCapability(event.getWorld());
		
		//Save loaded storages
		List<ILocalStorage> localStorages = new ArrayList<>();
		localStorages.addAll(worldStorage.getLocalStorageHandler().getLoadedStorages());
		for(ILocalStorage localStorage : localStorages) {
			//Only save if dirty
			if(localStorage.isDirty()) {
				worldStorage.getLocalStorageHandler().saveLocalStorageFile(localStorage);
				localStorage.setDirty(false);
			}
		}
		
		//Save regional cache
		worldStorage.getLocalStorageHandler().getLocalRegionCache().saveAllRegions();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event) {
		if(event.phase == Phase.END) {
			Iterator<Entry<Chunk, IWorldStorage>> entryIT = UNLOAD_QUEUE.entrySet().iterator();
			while(entryIT.hasNext()) {
				Entry<Chunk, IWorldStorage> entry = entryIT.next();
				Chunk chunk = entry.getKey();
				if(!chunk.isLoaded()) {
					entry.getValue().unloadChunk(chunk);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(event.phase == Phase.END && !event.world.isRemote) {
			tickWorld(event.world);
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			World world = Minecraft.getMinecraft().world;
			if(world != null && !Minecraft.getMinecraft().isGamePaused()) {
				tickWorld(world);
			}
		}
	}

	private static void tickWorld(World world) {
		IWorldStorage cap = WorldStorageImpl.getCapability(world);
		if(cap != null) {
			cap.tick();
		}
	}
}
