package tcb.adventurousdungeons.api.script;

public class ScriptException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -945255556254192031L;

	private final IScriptComponent component;

	public ScriptException(IScriptComponent component, String msg) {
		super(msg);
		this.component = component;
		component.setError(msg);
	}

	public ScriptException(IScriptComponent component, String msg, Throwable ex) {
		super(msg, ex);
		this.component = component;
		component.setError(msg);
	}

	public ScriptException(IScriptComponent component, String msg, Throwable ex, boolean enableSuppression, boolean writableStackTrace) {
		super(msg, ex, enableSuppression, writableStackTrace);
		this.component = component;
		component.setError(msg);
	}

	public IScriptComponent getComponent() {
		return this.component;
	}
}
