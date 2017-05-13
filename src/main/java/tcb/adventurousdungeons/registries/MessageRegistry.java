package tcb.adventurousdungeons.registries;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.common.network.clientbound.MessageAddDungeonComponent;
import tcb.adventurousdungeons.common.network.clientbound.MessageDungeonAABB;
import tcb.adventurousdungeons.common.network.clientbound.MessageDungeonComponentData;
import tcb.adventurousdungeons.common.network.clientbound.MessageEditDungeonComponent;
import tcb.adventurousdungeons.common.network.clientbound.MessageRemoveDungeonComponent;
import tcb.adventurousdungeons.common.network.clientbound.MessageRemoveLocalStorage;
import tcb.adventurousdungeons.common.network.clientbound.MessageSyncChunkStorage;
import tcb.adventurousdungeons.common.network.clientbound.MessageSyncLocalStorage;
import tcb.adventurousdungeons.common.network.common.MessageBase;
import tcb.adventurousdungeons.common.network.common.MessageEditDungeonScript;
import tcb.adventurousdungeons.common.network.serverbound.MessageCreateArea;
import tcb.adventurousdungeons.common.network.serverbound.MessageDeleteComponent;
import tcb.adventurousdungeons.common.network.serverbound.MessageSetComponentName;

public class MessageRegistry {
	private MessageRegistry() { }

	private static final ClientboundHandler CLIENT_BOUND_HANDLER = new ClientboundHandler();
	private static final ServerboundHandler SERVER_BOUND_HANDLER = new ServerboundHandler();

	private static int nextMessageId = 0;

	public static void register() {

		registerMessage(MessageSyncLocalStorage.class, Side.CLIENT);
		registerMessage(MessageSyncChunkStorage.class, Side.CLIENT);
		registerMessage(MessageRemoveLocalStorage.class, Side.CLIENT);
		registerMessage(MessageEditDungeonScript.class, Side.CLIENT);
		registerMessage(MessageEditDungeonComponent.class, Side.CLIENT);
		registerMessage(MessageDungeonComponentData.class, Side.CLIENT);
		registerMessage(MessageDungeonAABB.class, Side.CLIENT);
		registerMessage(MessageAddDungeonComponent.class, Side.CLIENT);
		registerMessage(MessageRemoveDungeonComponent.class, Side.CLIENT);

		registerMessage(MessageEditDungeonScript.class, Side.SERVER);
		registerMessage(MessageSetComponentName.class, Side.SERVER);
		registerMessage(MessageCreateArea.class, Side.SERVER);
		registerMessage(MessageDeleteComponent.class, Side.SERVER);

	}

	private static void registerMessage(Class<? extends MessageBase> messageType, Side toSide) {
		AdventurousDungeons.getNetwork().registerMessage(getHandler(messageType, toSide), messageType, MessageRegistry.nextMessageId++, toSide);
	}

	private static IMessageHandler<MessageBase, IMessage> getHandler(Class<? extends MessageBase> messageType, Side toSide) {
		if (toSide == Side.CLIENT) {
			return CLIENT_BOUND_HANDLER;
		}
		return SERVER_BOUND_HANDLER;
	}

	private static class ServerboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			try {
				server.callFromMainThread(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private static class ClientboundHandler implements IMessageHandler<MessageBase, IMessage> {
		@Override
		public IMessage onMessage(MessageBase message, MessageContext ctx) {
			Minecraft mc = FMLClientHandler.instance().getClient();
			try {
				mc.addScheduledTask(() -> message.process(ctx));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
