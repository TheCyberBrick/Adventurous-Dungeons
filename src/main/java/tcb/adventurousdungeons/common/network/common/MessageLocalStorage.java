package tcb.adventurousdungeons.common.network.common;

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
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public abstract class MessageLocalStorage extends MessageBase {
	private StorageID storageID;
	private ILocalStorage storage;

	public MessageLocalStorage() {}

	public MessageLocalStorage(ILocalStorage storage) {
		this.storageID = storage.getID();
		this.storage = storage;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.storageID = StorageID.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.storageID.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handleClient();
		} else {
			World world = ctx.getServerHandler().playerEntity.world;
			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			this.storage = worldStorage.getLocalStorageHandler().getLocalStorage(this.storageID);
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleClient() {
		World world = Minecraft.getMinecraft().world;
		IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
		this.storage = worldStorage.getLocalStorageHandler().getLocalStorage(this.storageID);
	}

	public ILocalStorage getStorage() {
		return this.storage;
	}
}