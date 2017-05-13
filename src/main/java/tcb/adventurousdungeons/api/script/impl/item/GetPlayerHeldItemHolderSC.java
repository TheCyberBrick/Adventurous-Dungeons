package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns an item holder of the held item of a player
 */
public class GetPlayerHeldItemHolderSC extends DungeonScriptComponent {
	private InputPort<EntityPlayer> in_player;
	private InputPort<Boolean> in_offhand;
	private OutputPort<IItemHolder> out;

	public GetPlayerHeldItemHolderSC(Script script) {
		super(script);
	}

	public GetPlayerHeldItemHolderSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_player = this.in("player", EntityPlayer.class, true);
		this.in_offhand = this.in("offhand", Boolean.class, false);
		this.out = this.out("holder", IItemHolder.class);
	}

	@Override
	protected void run() throws ScriptException {
		Boolean offhand = this.get(this.in_offhand);
		EnumHand hand = EnumHand.MAIN_HAND;
		if(offhand != null && offhand) {
			hand = EnumHand.OFF_HAND;
		}
		this.put(this.out, new PlayerHeldItemHolder(this.get(this.in_player), hand));
	}
}
