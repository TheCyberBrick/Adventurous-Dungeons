package tcb.adventurousdungeons.api.script.impl.math;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component rounds down a double
 */
public class FloorDoubleSC extends DungeonScriptComponent {
	private InputPort<Double> in;
	private OutputPort<Double> out;

	public FloorDoubleSC(Script script) {
		super(script);
	}

	public FloorDoubleSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Double.class, true);
		this.out = this.out("out", Double.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, Math.floor(this.get(this.in)));
	}
}
