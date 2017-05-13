package tcb.adventurousdungeons.api.script.impl.bool;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns whether both input values are true
 */
public class BoolAndSC extends DungeonScriptComponent {
	private InputPort<Boolean> in_0;
	private InputPort<Boolean> in_1;
	private OutputPort<Boolean> out;

	public BoolAndSC(Script script) {
		super(script);
	}

	public BoolAndSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_0 = this.in("val 1", Boolean.class, true);
		this.in_1 = this.in("val 2", Boolean.class, true);
		this.out = this.out("out", Boolean.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_0) && this.get(this.in_1));
	}
}
