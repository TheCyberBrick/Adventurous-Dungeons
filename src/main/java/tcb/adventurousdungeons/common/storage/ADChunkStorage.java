package tcb.adventurousdungeons.common.storage;

import net.minecraft.world.chunk.Chunk;
import tcb.adventurousdungeons.api.storage.IWorldStorage;

public class ADChunkStorage extends ChunkStorageImpl {
	public ADChunkStorage(IWorldStorage worldStorage, Chunk chunk) {
		super(worldStorage, chunk);
	}
}
