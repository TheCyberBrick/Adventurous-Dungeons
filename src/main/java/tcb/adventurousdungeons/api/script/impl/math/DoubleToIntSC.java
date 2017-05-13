package tcb.adventurousdungeons.api.script.impl.math;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component rounds a double to an int
 */
public class DoubleToIntSC extends DungeonScriptComponent {
	private InputPort<Double> in;
	private OutputPort<Integer> out;

	public DoubleToIntSC(Script script) {
		super(script);
	}

	public DoubleToIntSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Double.class, true);
		this.out = this.out("out", Integer.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, (int)Math.round(this.get(this.in)));
	}
}
