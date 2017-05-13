package tcb.adventurousdungeons.api.dungeon.component.serializers;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public class AABBSerializer implements DataSerializer<AxisAlignedBB> {
	@Override
	public void write(PacketBuffer buf, AxisAlignedBB value) {
		buf.writeCompoundTag(MoreNBTUtils.writeAABB(value));
	}

	@Override
	public AxisAlignedBB read(PacketBuffer buf) throws IOException {
		return MoreNBTUtils.readAABB(buf.readCompoundTag());
	}

	@Override
	public DataParameter<AxisAlignedBB> createKey(int id) {
		return new DataParameter<>(id, this);
	}
}
