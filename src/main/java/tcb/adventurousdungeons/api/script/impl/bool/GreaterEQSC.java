package tcb.adventurousdungeons.api.script.impl.bool;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns whether the first value is greater or equal the second
 */
public class GreaterEQSC extends DungeonScriptComponent {
	private InputPort<Double> in_0;
	private InputPort<Double> in_1;
	private OutputPort<Boolean> out;

	public GreaterEQSC(Script script) {
		super(script);
	}

	public GreaterEQSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_0 = this.in("val 1", Double.class, true);
		this.in_1 = this.in("val 2", Double.class, true);
		this.out = this.out("out", Boolean.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_0) >= this.get(this.in_1));
	}
}
