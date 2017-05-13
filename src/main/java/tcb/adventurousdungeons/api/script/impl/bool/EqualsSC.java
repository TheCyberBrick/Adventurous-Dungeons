package tcb.adventurousdungeons.api.script.impl.bool;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component compares two 
 * objects using {@link Object#equals(Object)}
 */
public class EqualsSC extends DungeonScriptComponent {
	private InputPort<Object> in_0;
	private InputPort<Object> in_1;
	private OutputPort<Boolean> output;

	public EqualsSC(Script script) {
		super(script);
	}
	
	public EqualsSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_0 = this.in("in_0", Object.class, true);
		this.in_1 = this.in("in_1", Object.class, true);
		this.output = this.out("out", Boolean.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.output, this.get(this.in_0).equals(this.get(this.in_1)));
	}
}
