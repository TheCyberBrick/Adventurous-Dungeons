package tcb.adventurousdungeons.api.script;

/**
 * The trigger input port is used by components
 * as a connection point for any triggers, i.e. output ports
 */
public class TriggerInputPort extends InputPort<Object> {
	public TriggerInputPort(ScriptComponent component, int id, String name) {
		super(component, id, name, Object.class, false, true);
	}
}
