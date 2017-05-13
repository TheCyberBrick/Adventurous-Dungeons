package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns ItemStack of an item holder
 */
public class GetItemStackSC extends DungeonScriptComponent {
	private InputPort<IItemHolder> in;
	private OutputPort<ItemStack> out;

	public GetItemStackSC(Script script) {
		super(script);
	}

	public GetItemStackSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", IItemHolder.class, true);
		this.out = this.out("out", ItemStack.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in).getStack());
	}
}
