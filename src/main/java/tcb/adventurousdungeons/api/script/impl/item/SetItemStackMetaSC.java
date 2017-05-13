package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component sets the meta of an ItemStack
 */
public class SetItemStackMetaSC extends DungeonScriptComponent {
	private InputPort<ItemStack> in_item;
	private InputPort<Integer> in_meta;
	private OutputPort<ItemStack> out;

	public SetItemStackMetaSC(Script script) {
		super(script);
	}

	public SetItemStackMetaSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_item = this.in("item", ItemStack.class, true);
		this.in_meta = this.in("meta", Integer.class, true);
		this.out = this.out("out", ItemStack.class);
	}

	@Override
	protected void run() throws ScriptException {
		ItemStack stack = this.get(this.in_item);
		stack.setItemDamage(this.get(this.in_meta));
		this.put(this.out, stack);
	}
}
