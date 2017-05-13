package tcb.adventurousdungeons.api.dungeon.component.serializers;

import net.minecraft.network.datasync.DataSerializers;

public final class ComponentDataSerializers {
	public static final ScriptDataSerializer SCRIPT = new ScriptDataSerializer();
	public static final AABBSerializer AABB = new AABBSerializer();

	public static void register() {
		DataSerializers.registerSerializer(SCRIPT);
		DataSerializers.registerSerializer(AABB);
	}
}
