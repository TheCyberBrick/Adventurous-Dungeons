package tcb.adventurousdungeons.common.network.clientbound;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.common.MessageLocalStorage;
import tcb.adventurousdungeons.common.storage.ADLocalStorage;

public class MessageRemoveDungeonComponent extends MessageLocalStorage {
	private StorageID componentId;

	public MessageRemoveDungeonComponent() {}

	public MessageRemoveDungeonComponent(ADLocalStorage storage, IDungeonComponent component) {
		super(storage);
		this.componentId = component.getID();
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);

		try {
			this.componentId = StorageID.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);

		buf.writeCompoundTag(this.componentId.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT && this.componentId != null) {
			this.handleClient();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleClient() {
		if(this.getStorage() instanceof ADLocalStorage) {
			ADLocalStorage storage = (ADLocalStorage) this.getStorage();
			IDungeonComponent component = storage.getDungeonComponent(this.componentId);
			if(component != null) {
				storage.removeDungeonComponent(component);
			}
		}
	}
}
