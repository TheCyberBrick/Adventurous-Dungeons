package tcb.adventurousdungeons.api.script.impl.bool;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component inverts the input boolean
 */
public class BoolNotSC extends DungeonScriptComponent {
	private InputPort<Boolean> in;
	private OutputPort<Boolean> out;

	public BoolNotSC(Script script) {
		super(script);
	}

	public BoolNotSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Boolean.class, true);
		this.out = this.out("out", Boolean.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, !this.get(this.in));
	}
}
