package tcb.adventurousdungeons.common.network.clientbound;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.ComponentDataManager;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class MessageDungeonComponentData extends MessageBase {
	private StorageID dungeonID;
	private StorageID componentID;
	private List<ComponentDataManager.DataEntry<?>> dataManagerEntries;
	private ByteBuf data;

	public MessageDungeonComponentData() {}

	public MessageDungeonComponentData(IDungeonComponent component, boolean sendAll) {
		this.dungeonID = component.getDungeon().getID();
		this.componentID = component.getID();
		if(sendAll) {
			this.dataManagerEntries = component.getDataManager().getAll();
			component.getDataManager().setClean();
		} else {
			this.dataManagerEntries = component.getDataManager().getDirty();
		}
		this.data = Unpooled.buffer();
		try {
			ComponentDataManager.writeEntries(this.dataManagerEntries, new PacketBuffer(this.data));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			int len = buf.readInt();
			this.dataManagerEntries = ComponentDataManager.readEntries(new PacketBuffer(buf.readBytes(len)));
			this.dungeonID = StorageID.readFromNBT(buf.readCompoundTag());
			this.componentID = StorageID.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		this.data.readerIndex(0);
		buf.writeInt(this.data.readableBytes());
		buf.writeBytes(this.data);
		buf.writeCompoundTag(this.dungeonID.writeToNBT(new NBTTagCompound()));
		buf.writeCompoundTag(this.componentID.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			this.handleClientbound();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleClientbound() {
		IWorldStorage worldStorage = WorldStorageImpl.getCapability(Minecraft.getMinecraft().world);
		ILocalStorage localStorage = worldStorage.getLocalStorageHandler().getLocalStorage(this.dungeonID);
		if(localStorage instanceof IDungeon) {
			IDungeon dungeon = (IDungeon) localStorage;
			IDungeonComponent dungeonComponent = dungeon.getDungeonComponent(this.componentID);
			if(dungeonComponent != null) {
				dungeonComponent.getDataManager().setEntryValues(this.dataManagerEntries);
			}
		}
	}
}