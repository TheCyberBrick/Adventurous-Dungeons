package tcb.adventurousdungeons.common.network.serverbound;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class MessageSetComponentName extends MessageBase {
	private StorageID dungeonID;
	private StorageID componentID;
	private String name;

	public MessageSetComponentName() {}

	public MessageSetComponentName(IDungeonComponent component, String name) {
		this.dungeonID = component.getDungeon().getID();
		this.componentID = component.getID();
		this.name = name;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.dungeonID = StorageID.readFromNBT(buf.readCompoundTag());
			this.componentID = StorageID.readFromNBT(buf.readCompoundTag());
			this.name = buf.readString(128);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.dungeonID.writeToNBT(new NBTTagCompound()));
		buf.writeCompoundTag(this.componentID.writeToNBT(new NBTTagCompound()));
		buf.writeString(this.name);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.SERVER && this.dungeonID != null && this.componentID != null && this.name != null) {
			if(this.name.length() >= 3 && this.name.length() <= 64) {
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(ctx.getServerHandler().playerEntity.world);
				ILocalStorage localStorage = worldStorage.getLocalStorageHandler().getLocalStorage(this.dungeonID);
				if(localStorage instanceof IDungeon) {
					IDungeon dungeon = (IDungeon) localStorage;
					if(dungeon.canPlayerEdit(ctx.getServerHandler().playerEntity)) {
						IDungeonComponent dungeonComponent = dungeon.getDungeonComponent(this.componentID);
						if(dungeonComponent != null) {
							dungeonComponent.setName(this.name);
							dungeon.markDirty();
						}
					}
				}
			}
		}
		return null;
	}
}