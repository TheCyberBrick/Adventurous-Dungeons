package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component sets the ItemStack of an item holder
 */
public class SetItemStackSC extends DungeonScriptComponent {
	private InputPort<IItemHolder> in_holder;
	private InputPort<ItemStack> in_item;
	private OutputPort<IItemHolder> out_holder;
	private OutputPort<ItemStack> out_item;

	public SetItemStackSC(Script script) {
		super(script);
	}

	public SetItemStackSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_holder = this.in("holder", IItemHolder.class, true);
		this.in_item = this.in("item", ItemStack.class, true);
		this.out_holder = this.out("holder", IItemHolder.class);
		this.out_item = this.out("item", ItemStack.class);
	}

	@Override
	protected void run() throws ScriptException {
		ItemStack item = this.get(this.in_item);
		IItemHolder holder = this.get(this.in_holder);
		this.put(this.out_item, item);
		this.put(this.out_holder, holder);
		holder.setStack(item);
	}
}
