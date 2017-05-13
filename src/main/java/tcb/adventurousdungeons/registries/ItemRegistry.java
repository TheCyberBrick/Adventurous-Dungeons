package tcb.adventurousdungeons.registries;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import tcb.adventurousdungeons.common.item.ItemAreaSelection;
import tcb.adventurousdungeons.common.item.ItemComponentSelection;
import tcb.adventurousdungeons.common.item.ItemScriptEditor;
import tcb.adventurousdungeons.common.item.ItemSpline;
import tcb.adventurousdungeons.common.item.ItemTest;

public class ItemRegistry {
	private ItemRegistry() { }

	public static void register(IForgeRegistry<Item> registry) {
		registry.register(new ItemTest());
		registry.register(new ItemAreaSelection());
		registry.register(new ItemComponentSelection());
		registry.register(new ItemScriptEditor());
		registry.register(new ItemSpline());
	}
}
