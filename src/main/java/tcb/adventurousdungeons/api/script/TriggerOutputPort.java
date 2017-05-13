package tcb.adventurousdungeons.api.script;

/**
 * The trigger output port is used by components
 * as a connection point for any triggers, i.e. input ports
 */
public class TriggerOutputPort extends OutputPort<Object> {
	public TriggerOutputPort(ScriptComponent component, int id, String name) {
		super(component, id, name, Object.class, false);
	}
}
