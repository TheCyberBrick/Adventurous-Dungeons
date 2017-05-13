package tcb.adventurousdungeons.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.storage.IChunkStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class MessageSyncChunkStorage extends MessageBase {
	private NBTTagCompound nbt;
	private ChunkPos pos;

	public MessageSyncChunkStorage() {}

	public MessageSyncChunkStorage(IChunkStorage storage) {
		this.nbt = storage.writeToNBT(new NBTTagCompound());
		this.pos = storage.getChunk().getPos();
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.pos = new ChunkPos(buf.readInt(), buf.readInt());
			this.nbt = buf.readCompoundTag();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeInt(this.pos.chunkXPos);
		buf.writeInt(this.pos.chunkZPos);
		buf.writeCompoundTag(this.nbt);
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
		Chunk chunk = world.getChunkFromChunkCoords(this.pos.chunkXPos, this.pos.chunkZPos);
		if(chunk != null) {
			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			IChunkStorage chunkStorage = worldStorage.getChunkStorage(chunk);
			chunkStorage.readFromNBT(this.nbt);
		}
	}
}