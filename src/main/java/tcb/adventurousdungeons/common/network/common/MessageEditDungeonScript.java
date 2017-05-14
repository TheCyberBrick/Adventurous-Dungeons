package tcb.adventurousdungeons.common.network.common;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;
import tcb.adventurousdungeons.api.script.IDungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.client.gui.GuiEditScript;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class MessageEditDungeonScript extends MessageBase {
	private StorageID dungeonID;
	private StorageID scriptID;
	private NBTTagCompound scriptNbt;

	public MessageEditDungeonScript() {}

	private MessageEditDungeonScript(StorageID dungeonID, StorageID scriptID, NBTTagCompound scriptNbt) {
		this.dungeonID = dungeonID;
		this.scriptID = scriptID;
		this.scriptNbt = scriptNbt;
	}

	private MessageEditDungeonScript(StorageID dungeonID, StorageID scriptID) {
		this.dungeonID = dungeonID;
		this.scriptID = scriptID;
	}

	public static MessageEditDungeonScript createClientbound(ScriptDC script) {
		return new MessageEditDungeonScript(script.getDungeon().getID(), script.getID());
	}

	public static MessageEditDungeonScript createServerbound(StorageID dungeonID, StorageID scriptID, Script script) {
		return new MessageEditDungeonScript(dungeonID, scriptID, script.writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		try {
			this.dungeonID = StorageID.readFromNBT(buf.readCompoundTag());
			this.scriptID = StorageID.readFromNBT(buf.readCompoundTag());
			if(!buf.readBoolean()) {
				this.scriptNbt = buf.readCompoundTag();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeCompoundTag(this.dungeonID.writeToNBT(new NBTTagCompound()));
		buf.writeCompoundTag(this.scriptID.writeToNBT(new NBTTagCompound()));
		buf.writeBoolean(this.scriptNbt == null);
		if(this.scriptNbt != null) {
			buf.writeCompoundTag(this.scriptNbt);
		}
	}

	@Override
	public IMessage process(MessageContext ctx) {
		if(this.dungeonID != null && this.scriptID != null) {
			if(ctx.side == Side.CLIENT) {
				this.handleClientbound();
			} else {
				this.handleServerbound(ctx.getServerHandler());
			}
		}
		return null;
	}

	private void handleServerbound(NetHandlerPlayServer handler) {
		if(this.dungeonID != null && this.scriptNbt != null && this.scriptID != null) {
			World world = handler.playerEntity.world;
			IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
			ILocalStorage localStorage = worldStorage.getLocalStorageHandler().getLocalStorage(this.dungeonID);
			if(localStorage instanceof IDungeon) {
				if(((IDungeon)localStorage).canPlayerEdit(handler.playerEntity)) {
					IDungeonComponent component = ((IDungeon)localStorage).getDungeonComponent(this.scriptID);
					if(component instanceof ScriptDC) {
						Script script = new Script();
						script.readFromNBT(this.scriptNbt);
						((ScriptDC)component).setScript(script);
						localStorage.markDirty();
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void handleClientbound() {
		if(Minecraft.getMinecraft().currentScreen == null) {
			if(this.dungeonID != null && this.scriptID != null) {
				World world = Minecraft.getMinecraft().world;
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
				ILocalStorage localStorage = worldStorage.getLocalStorageHandler().getLocalStorage(this.dungeonID);
				if(localStorage instanceof IDungeon) {
					IDungeon dungeon = (IDungeon) localStorage;
					IDungeonComponent component = dungeon.getDungeonComponent(this.scriptID);
					if(component instanceof ScriptDC) {
						Script script = new Script();
						script.readFromNBT(((ScriptDC)component).getScriptNbtCopy());
						for(IScriptComponent scriptComponent : script.getComponents()) {
							if(scriptComponent instanceof IDungeonScriptComponent) {
								((IDungeonScriptComponent)scriptComponent).setDungeonComponent((ScriptDC) component);
							}
						}
						Minecraft.getMinecraft().displayGuiScreen(new GuiEditScript(this.dungeonID, this.scriptID, script));
					}
				}
			}
		}
	}
}