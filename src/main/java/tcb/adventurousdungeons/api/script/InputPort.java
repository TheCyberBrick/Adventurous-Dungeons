package tcb.adventurousdungeons.api.script;

/**
 * Input ports are used by components to
 * receive input values
 * @param <T>
 */
public class InputPort<T> extends Port<T> {
	private final boolean isRequired;

	private final boolean triggersComponent;

	public InputPort(ScriptComponent component, int id, String name, Class<T> type, boolean isRequired, boolean triggersComponent) {
		super(component, id, name, type, true);
		this.isRequired = isRequired;
		this.triggersComponent = triggersComponent;
	}

	/**
	 * Returns whether this input requires a value
	 * @return
	 */
	public final boolean isRequiredNonNull() {
		return this.isRequired;
	}

	/**
	 * Returns whether this input triggers the component if a value is sent
	 * @return
	 */
	public final boolean doesTriggerComponent() {
		return this.triggersComponent;
	}

	@Override
	public boolean isCompatible(Port<?> connection) {
		return connection instanceof OutputPort;
	}
}
