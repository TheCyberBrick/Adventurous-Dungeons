package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns the meta of an ItemStack
 */
public class GetItemStackMetaSC extends DungeonScriptComponent {
	private InputPort<ItemStack> in;
	private OutputPort<Integer> out;

	public GetItemStackMetaSC(Script script) {
		super(script);
	}

	public GetItemStackMetaSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", ItemStack.class, true);
		this.out = this.out("out", Integer.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in).getItemDamage());
	}
}
