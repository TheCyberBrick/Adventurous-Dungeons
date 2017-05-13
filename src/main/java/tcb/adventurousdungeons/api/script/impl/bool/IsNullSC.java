package tcb.adventurousdungeons.api.script.impl.bool;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.PortCastException;
import tcb.adventurousdungeons.api.script.Script;

/**
 * This component returns whether the input value is null
 */
public class IsNullSC extends DungeonScriptComponent {
	private InputPort<Object> in;
	private OutputPort<Boolean> out;

	public IsNullSC(Script script) {
		super(script);
	}

	public IsNullSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Object.class, false);
		this.out = this.out("out", Boolean.class);
	}

	@Override
	protected void run() throws PortCastException {
		this.put(this.out, this.get(this.in) == null);
	}
}
