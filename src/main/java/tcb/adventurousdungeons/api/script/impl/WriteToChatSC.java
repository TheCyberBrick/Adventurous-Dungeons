package tcb.adventurousdungeons.api.script.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * Writes a string to the chat of the specified target
 */
public class WriteToChatSC extends DungeonScriptComponent {
	private InputPort<Object> msg;
	private InputPort<Entity> target;
	private OutputPort<String> out_msg;
	private OutputPort<Entity> out_target;

	public WriteToChatSC(Script script) {
		super(script);
	}

	public WriteToChatSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.msg = this.in("msg", Object.class, true);
		this.target = this.in("target", Entity.class, true);
		this.out_msg = this.out("out_msg", String.class);	
		this.out_target = this.out("out_target", Entity.class);
	}

	@Override
	protected void run() throws ScriptException {
		String str = this.get(this.msg).toString();
		Entity target = this.get(this.target);

		if(target instanceof EntityPlayerMP) {
			((EntityPlayerMP)target).sendMessage(new TextComponentString(str));
		} else if(this.getDungeonComponent().getDungeon().getWorldStorage().getWorld().isRemote) {
			this.checkAddClientChatMessage(target, new TextComponentString(str));
		}

		this.put(this.out_msg, str);
		this.put(this.out_target, target);
	}

	@SideOnly(Side.CLIENT)
	public void checkAddClientChatMessage(Entity target, ITextComponent component) {
		if(target == Minecraft.getMinecraft().player) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
		}
	}
}
