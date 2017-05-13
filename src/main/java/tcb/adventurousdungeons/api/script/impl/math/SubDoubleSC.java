package tcb.adventurousdungeons.api.script.impl.math;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component subtracts the second double from the first
 */
public class SubDoubleSC extends DungeonScriptComponent {
	private InputPort<Double> in_val1;
	private InputPort<Double> in_val2;
	private OutputPort<Double> out;

	public SubDoubleSC(Script script) {
		super(script);
	}

	public SubDoubleSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_val1 = this.in("val 1", Double.class, true);
		this.in_val2 = this.in("val 2", Double.class, true);
		this.out = this.out("out", Double.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_val1) - this.get(this.in_val2));
	}
}
