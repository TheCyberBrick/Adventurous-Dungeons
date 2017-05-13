package tcb.adventurousdungeons.api.script.impl.math;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component converts an int to a double
 */
public class IntToDoubleSC extends DungeonScriptComponent {
	private InputPort<Integer> in;
	private OutputPort<Double> out;

	public IntToDoubleSC(Script script) {
		super(script);
	}

	public IntToDoubleSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Integer.class, true);
		this.out = this.out("out", Double.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, (double)this.get(this.in));
	}
}
