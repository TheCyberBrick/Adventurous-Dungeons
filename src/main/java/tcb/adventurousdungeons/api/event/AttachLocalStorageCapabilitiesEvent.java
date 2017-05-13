package tcb.adventurousdungeons.api.event;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;

public class AttachLocalStorageCapabilitiesEvent extends AttachCapabilitiesEvent<ILocalStorage> {
	private final ILocalStorage storage;

	public AttachLocalStorageCapabilitiesEvent(ILocalStorage storage) {
		super(ILocalStorage.class, storage);
		this.storage = storage;
	}

	public ILocalStorage getStorage() {
		return this.storage;
	}
}
