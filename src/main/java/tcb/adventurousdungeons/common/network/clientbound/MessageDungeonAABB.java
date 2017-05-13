package tcb.adventurousdungeons.common.network.clientbound;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.common.network.common.MessageLocalStorage;
import tcb.adventurousdungeons.common.storage.ADLocalStorage;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public class MessageDungeonAABB extends MessageLocalStorage {
	private AxisAlignedBB aabb;

	public MessageDungeonAABB() {}

	public MessageDungeonAABB(ADLocalStorage dungeon, AxisAlignedBB aabb) {
		super(dungeon);
		this.aabb = aabb;
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		super.deserialize(buf);

		try {
			this.aabb = MoreNBTUtils.readAABB(buf.readCompoundTag());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		super.serialize(buf);
		buf.writeCompoundTag(MoreNBTUtils.writeAABB(this.aabb));
	}

	@Override
	public IMessage process(MessageContext ctx) {
		super.process(ctx);

		if(ctx.side == Side.CLIENT && this.aabb != null) {
			this.handleClient();
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	private void handleClient() {
		if(this.getStorage() instanceof ADLocalStorage) {
			((ADLocalStorage)this.getStorage()).setBoundingBox(this.aabb);
		}
	}
}
