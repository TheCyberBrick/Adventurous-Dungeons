package tcb.adventurousdungeons.common.network.clientbound;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.gui.GuiEditDungeonComponent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class MessageEditDungeonComponent extends MessageBase {
	private StorageID dungeonID;
	private StorageID componentID;

	public MessageEditDungeonComponent() {}

	public MessageEditDungeonComponent(IDungeonComponent component) {
		this.dungeonID = component.getDungeon().getID();
		this.componentID = component.getID();
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.dungeonID = StorageID.readFromNBT(buf.readCompoundTag());
			this.componentID = StorageID.readFromNBT(buf.readCompoundTag());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
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
		if(Minecraft.getMinecraft().currentScreen == null) {
			IWorldStorage worldStorage = WorldStorageImpl.getCapability(Minecraft.getMinecraft().world);
			ILocalStorage localStorage = worldStorage.getLocalStorageHandler().getLocalStorage(dungeonID);
			if(localStorage instanceof IDungeon) {
				IDungeon dungeon = (IDungeon) localStorage;
				IDungeonComponent dungeonComponent = dungeon.getDungeonComponent(componentID);
				if(dungeonComponent != null) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiEditDungeonComponent(dungeonComponent));
				}
			}
		}
	}
}