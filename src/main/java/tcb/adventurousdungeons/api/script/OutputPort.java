package tcb.adventurousdungeons.api.script;

/**
 * Output ports are used by components to
 * provide values to other components
 * @param <T>
 */
public class OutputPort<T> extends Port<T> {
	private final boolean splitIterable;

	public OutputPort(ScriptComponent component, int id, String name, Class<T> type, boolean splitIterable) {
		super(component, id, name, type, false);
		this.splitIterable = splitIterable;
	}

	@Override
	public boolean isCompatible(Port<?> connection) {
		return connection instanceof InputPort;
	}

	/**
	 * Returns whether an {@link Iterable} output is split up
	 * into its elements and each sent separately
	 * @return
	 */
	public boolean isMultiOutput() {
		return this.splitIterable && Iterable.class.isAssignableFrom(this.getDataType());
	}
}
