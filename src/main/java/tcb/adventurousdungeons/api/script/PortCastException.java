package tcb.adventurousdungeons.api.script;

public class PortCastException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4888787767246053920L;

	private final InputPort<?> port;
	private final Class<?> toCast, expected;

	public PortCastException(ScriptComponent component, InputPort<?> port, Class<?> toCast, Class<?> expected, String msg) {
		super(component, msg);
		this.port = port;
		this.toCast = toCast;
		this.expected = expected;
	}

	public PortCastException(ScriptComponent component, InputPort<?> port, Class<?> toCast, Class<?> expected, String msg, ClassCastException ex) {
		super(component, msg, ex);
		this.port = port;
		this.toCast = toCast;
		this.expected = expected;
	}

	public InputPort<?> getPort() {
		return this.port;
	}

	public Class<?> getClassToCast() {
		return this.toCast;
	}

	public Class<?> getExpectedClass() {
		return this.expected;
	}
}
