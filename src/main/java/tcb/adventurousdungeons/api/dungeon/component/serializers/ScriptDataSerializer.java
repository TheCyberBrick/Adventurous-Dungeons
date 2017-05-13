package tcb.adventurousdungeons.api.dungeon.component.serializers;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import tcb.adventurousdungeons.api.script.Script;

public class ScriptDataSerializer implements DataSerializer<Script> {
	@Override
	public void write(PacketBuffer buf, Script value) {
		buf.writeCompoundTag(value.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public Script read(PacketBuffer buf) throws IOException {
		Script script = new Script();
		script.readFromNBT(buf.readCompoundTag());
		return script;
	}

	@Override
	public DataParameter<Script> createKey(int id) {
		return new DataParameter<>(id, this);
	}
}
