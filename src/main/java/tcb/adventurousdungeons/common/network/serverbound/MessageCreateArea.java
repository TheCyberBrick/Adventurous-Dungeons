package tcb.adventurousdungeons.common.network.serverbound;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.impl.DungeonAreaDC;
import tcb.adventurousdungeons.api.dungeon.component.impl.EntityAreaTriggerDC;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public class MessageCreateArea extends MessageBase {
	private StorageID dungeonID;
	private AxisAlignedBB aabb;
	private String name;
	private int type;

	public MessageCreateArea() {}

	public MessageCreateArea(IDungeon dungeon, AxisAlignedBB aabb, String name, int type) {
		this.dungeonID = dungeon.getID();
		this.aabb = aabb;
		this.name = name;
		this.type = type;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.dungeonID = StorageID.readFromNBT(buf.readCompoundTag());
			this.aabb = MoreNBTUtils.readAABB(buf.readCompoundTag());
			this.name = buf.readString(128);
			this.type = buf.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.dungeonID.writeToNBT(new NBTTagCompound()));
		buf.writeCompoundTag(MoreNBTUtils.writeAABB(this.aabb));
		buf.writeString(this.name);
		buf.writeInt(this.type);
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(ctx.side == Side.SERVER && this.name != null && this.dungeonID != null && this.aabb != null) {
			if(this.name.length() >= 3 && this.name.length() <= 64 && this.aabb != null) {
				IDungeon dungeon = null;
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(ctx.getServerHandler().playerEntity.world);
				Collection<ILocalStorage> localStorages = worldStorage.getLocalStorageHandler().getLoadedStorages();
				for(ILocalStorage localStorage : localStorages) {
					if(localStorage instanceof IDungeon) {
						if(aabb.intersectsWith(((IDungeon) localStorage).getBoundingBox())) {
							dungeon = (IDungeon) localStorage;
							break;
						}
					}
				}
				if(dungeon != null && dungeon.canPlayerEdit(ctx.getServerHandler().playerEntity)) {
					switch(type) {
					case 0: {
						IDungeonComponent component = new DungeonAreaDC(dungeon, this.aabb);
						component.setName(this.name);
						dungeon.addDungeonComponent(component);
						break;
					}
					case 1: {
						IDungeonComponent component = new EntityAreaTriggerDC(dungeon, this.aabb);
						component.setName(this.name);
						dungeon.addDungeonComponent(component);
						break;
					}
					}
				}
			}
		}
		return null;
	}
}