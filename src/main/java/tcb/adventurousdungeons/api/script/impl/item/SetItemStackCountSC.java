package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component sets the count of an ItemStack
 */
public class SetItemStackCountSC extends DungeonScriptComponent {
	private InputPort<ItemStack> in_item;
	private InputPort<Integer> in_count;
	private OutputPort<ItemStack> out;

	public SetItemStackCountSC(Script script) {
		super(script);
	}

	public SetItemStackCountSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_item = this.in("item", ItemStack.class, true);
		this.in_count = this.in("count", Integer.class, true);
		this.out = this.out("out", ItemStack.class);
	}

	@Override
	protected void run() throws ScriptException {
		ItemStack stack = this.get(this.in_item);
		stack.setCount(this.get(this.in_count));
		this.put(this.out, stack);
	}
}
