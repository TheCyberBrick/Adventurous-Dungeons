package tcb.adventurousdungeons.api.script.impl;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component is used as a conditional statement.
 * The input boolean decides whether the value of in_true or in_false
 * is passed to the outputs
 */
public class IfElseSC extends DungeonScriptComponent {
	private InputPort<Boolean> cond;
	private InputPort<Object> in_true;
	private InputPort<Object> in_false;
	private OutputPort<Object> out_true;
	private OutputPort<Object> out_false;
	private OutputPort<Object> out;

	public IfElseSC(Script script) {
		super(script);
	}

	public IfElseSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.cond = this.in("cond", Boolean.class, true);
		this.in_true = this.in("in_true", Object.class, false, false);
		this.in_false = this.in("in_false", Object.class, false, false);
		this.out = this.out("out", Object.class);
		this.out_true = this.out("out_true", Object.class);
		this.out_false = this.out("out_false", Object.class);
	}

	@Override
	protected void run() throws ScriptException {
		if(this.get(this.cond)) {
			this.put(this.out_true, this.get(this.in_true));
			this.put(this.out, this.get(this.in_true));
			this.ignore(this.out_false);
		} else {
			this.put(this.out_false, this.get(this.in_false));
			this.put(this.out, this.get(this.in_false));
			this.ignore(this.out_true);
		}
	}
}
