package tcb.adventurousdungeons.api.script.impl.math;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component multiplies two integers together
 */
public class MulIntSC extends DungeonScriptComponent {
	private InputPort<Integer> in_val1;
	private InputPort<Integer> in_val2;
	private OutputPort<Integer> out;

	public MulIntSC(Script script) {
		super(script);
	}

	public MulIntSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_val1 = this.in("val 1", Integer.class, true);
		this.in_val2 = this.in("val 2", Integer.class, true);
		this.out = this.out("out", Integer.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_val1) * this.get(this.in_val2));
	}
}
