package tcb.adventurousdungeons.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.ILocalStorageHandler;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class MessageRemoveLocalStorage extends MessageBase {
	private StorageID id;

	public MessageRemoveLocalStorage() {}

	public MessageRemoveLocalStorage(StorageID id) {
		this.id = id;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.id = StorageID.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.id.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handle();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handle() {
		World world = Minecraft.getMinecraft().world;
		IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
		ILocalStorageHandler localStorageHandler = worldStorage.getLocalStorageHandler();
		ILocalStorage loadedStorage = localStorageHandler.getLocalStorage(this.id);
		if(loadedStorage != null) {
			localStorageHandler.removeLocalStorage(loadedStorage);
		}
	}
}