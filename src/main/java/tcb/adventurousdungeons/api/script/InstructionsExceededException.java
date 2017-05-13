package tcb.adventurousdungeons.api.script;

public class InstructionsExceededException extends ScriptException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2309668090712852436L;

	public InstructionsExceededException(IScriptComponent component, String msg) {
		super(component, msg, null, false, false);
	}
}
