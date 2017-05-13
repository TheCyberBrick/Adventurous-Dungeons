package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns an item holder for the specified inventory slot of a player
 */
public class GetPlayerItemHolderSC extends DungeonScriptComponent {
	private InputPort<EntityPlayer> in_player;
	private InputPort<Integer> in_slot;
	private OutputPort<IItemHolder> out;

	public GetPlayerItemHolderSC(Script script) {
		super(script);
	}

	public GetPlayerItemHolderSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_player = this.in("player", EntityPlayer.class, true);
		this.in_slot = this.in("slot", Integer.class, true);
		this.out = this.out("holder", IItemHolder.class);
	}

	@Override
	protected void run() throws ScriptException {
		InventoryPlayer inv = this.get(this.in_player).inventory;
		int slot = this.get(this.in_slot);
		if(slot < 0 || slot >= inv.getSizeInventory()) {
			throw new ScriptException(this, String.format("Invalid player inventory slot %d", slot));
		}
		this.put(this.out, new PlayerItemHolder(inv, slot));
	}
}
