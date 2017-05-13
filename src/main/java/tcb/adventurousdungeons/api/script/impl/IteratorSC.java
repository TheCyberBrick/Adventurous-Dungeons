package tcb.adventurousdungeons.api.script.impl;

import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component splits an {@link Iterable} object
 * into its elements and runs the output component
 * for each element
 */
public class IteratorSC extends DungeonScriptComponent {
	@SuppressWarnings("rawtypes")
	private InputPort<Iterable> in;

	@SuppressWarnings("rawtypes")
	private OutputPort<Iterable> out;

	public IteratorSC(Script script) {
		super(script);
	}
	
	public IteratorSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Iterable.class, true);
		this.out = this.out("out", Iterable.class, true);		
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in));
	}
}
