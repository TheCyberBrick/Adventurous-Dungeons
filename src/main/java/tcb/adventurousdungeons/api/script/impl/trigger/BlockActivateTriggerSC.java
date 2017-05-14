package tcb.adventurousdungeons.api.script.impl.trigger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import tcb.adventurousdungeons.api.dungeon.event.BlockActivateEvent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

public class BlockActivateTriggerSC extends EventTriggerSC<BlockActivateEvent> {
	private OutputPort<EntityPlayer> out_player;
	private OutputPort<Vec3d> out_pos;
	private OutputPort<Vec3i> out_block_pos;
	private OutputPort<ItemStack> out_item;
	private OutputPort<Boolean> out_offhand;

	public BlockActivateTriggerSC(Script script) {
		super(script);
	}

	public BlockActivateTriggerSC(Script script, String name) {
		super(script, name);
	}

	@Override
	public Class<BlockActivateEvent> getEventType() {
		return BlockActivateEvent.class;
	}

	@Override
	protected void createPorts() {
		this.out_player = this.out("player", EntityPlayer.class);
		this.out_pos = this.out("pos", Vec3d.class);
		this.out_block_pos = this.out("block_pos", Vec3i.class);
		this.out_item = this.out("item", ItemStack.class);
		this.out_offhand = this.out("offhand", Boolean.class);
	}

	@Override
	protected boolean runTrigger(BlockActivateEvent event) throws ScriptException {
		this.put(this.out_player, event.getPlayer());
		this.put(this.out_pos, event.getPos());
		this.put(this.out_block_pos, event.getBlockPos());
		this.put(this.out_item, event.getItem());
		this.put(this.out_offhand, event.getHand() == EnumHand.OFF_HAND);
		return true;
	}
}
