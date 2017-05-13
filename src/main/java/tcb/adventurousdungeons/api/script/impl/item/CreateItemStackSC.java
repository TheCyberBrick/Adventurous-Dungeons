package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component creates a new ItemStack
 */
public class CreateItemStackSC extends DungeonScriptComponent {
	private InputPort<Item> in_item;
	private InputPort<Integer> in_count;
	private InputPort<Integer> in_meta;
	private OutputPort<ItemStack> out;

	public CreateItemStackSC(Script script) {
		super(script);
	}

	public CreateItemStackSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_item = this.in("item", Item.class, true);
		this.in_count = this.in("count", Integer.class, false);
		this.in_meta = this.in("meta", Integer.class, false);
		this.out = this.out("out", ItemStack.class);
	}

	@Override
	protected void run() throws ScriptException {
		Integer count = this.get(this.in_count);
		Integer meta = this.get(this.in_meta);
		this.put(this.out, new ItemStack(this.get(this.in_item), count != null ? count : 0, meta != null ? meta : 0));
	}
}
