package tcb.adventurousdungeons.api.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import tcb.adventurousdungeons.api.storage.IChunkStorage;

public class AttachChunkStorageCapabilitiesEvent extends AttachCapabilitiesEvent<IChunkStorage> {
	private final IChunkStorage storage;

	public AttachChunkStorageCapabilitiesEvent(IChunkStorage storage) {
		super(IChunkStorage.class, storage);
		this.storage = storage;
	}

	public IChunkStorage getStorage() {
		return this.storage;
	}
}
