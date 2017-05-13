package tcb.adventurousdungeons;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import tcb.adventurousdungeons.api.dungeon.component.serializers.ComponentDataSerializers;
import tcb.adventurousdungeons.client.handler.WorldRenderHandler;
import tcb.adventurousdungeons.common.handler.DungeonEventHandler;
import tcb.adventurousdungeons.common.handler.WorldEventHandler;
import tcb.adventurousdungeons.common.item.ItemAreaSelection;
import tcb.adventurousdungeons.common.item.ItemComponentSelection;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;
import tcb.adventurousdungeons.registries.DungeonComponentRegistry;
import tcb.adventurousdungeons.registries.ItemRegistry;
import tcb.adventurousdungeons.registries.MessageRegistry;
import tcb.adventurousdungeons.registries.ScriptComponentRegistry;

@Mod(modid = ModInfo.ID, version = ModInfo.VERSION)
@Mod.EventBusSubscriber
public class AdventurousDungeons {
	private static SimpleNetworkWrapper network;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);

		DungeonComponentRegistry.register();

		MessageRegistry.register();

		ComponentDataSerializers.register();
		
		ScriptComponentRegistry.register();
		ScriptComponentRegistry.registerGuiFactories();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		WorldStorageImpl.register();

		MinecraftForge.EVENT_BUS.register(WorldEventHandler.class);
		MinecraftForge.EVENT_BUS.register(WorldRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemAreaSelection.SelectionRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemComponentSelection.SelectionRenderHandler.class);
		MinecraftForge.EVENT_BUS.register(DungeonEventHandler.class);
	}

	public static SimpleNetworkWrapper getNetwork() {
		return network;
	}

	@SubscribeEvent
	public static void onRegisterItems(RegistryEvent.Register<Item> event) {
		ItemRegistry.register(event.getRegistry());
	}
}
