package tcb.adventurousdungeons.api.script;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * The base component for scripts.
 * Any expected exceptions must extend {@link ScriptException}, any
 * other exceptions will be considered a bug and likely not be caught.
 */
public interface IScriptComponent {
	/**
	 * Returns the component ID
	 * @return
	 */
	public UUID getID();

	/**
	 * Sets the component ID
	 * @param id
	 * @return
	 */
	public void setID(UUID id);

	/**
	 * Initializes the input and output ports of the component
	 */
	public void initPorts();

	/**
	 * Returns the script
	 * @return
	 */
	public Script getScript();

	/**
	 * Sets the name of the component
	 * @param name
	 */
	public void setName(String name);

	/**
	 * Returns the name of the component
	 * @return
	 */
	public String getName();

	/**
	 * Returns the input ports
	 * @return
	 */
	public List<InputPort<?>> getInputs();

	/**
	 * Returns the input port with the specified name
	 * @param name
	 * @return
	 */
	public default InputPort<?> getInput(String name) {
		for(InputPort<?> port : this.getInputs()) {
			if(port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	/**
	 * Returns the output ports
	 * @return
	 */
	public List<OutputPort<?>> getOutputs();

	/**
	 * Returns the output port with the specified name
	 * @param name
	 * @return
	 */
	public default OutputPort<?> getOutput(String name) {
		for(OutputPort<?> port : this.getOutputs()) {
			if(port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	/**
	 * Returns all ports
	 * @return
	 */
	public List<Port<?>> getPorts();

	/**
	 * Executes this component
	 */
	public void execute() throws ScriptException;

	/**
	 * Returns an error message that this component has thrown.
	 * May be null if no exception occurred
	 * @return
	 */
	@Nullable
	public String getError();

	/**
	 * Sets an error message if this component throws a {@link ScriptException},
	 * or null if the error is reset
	 * @param ex
	 */
	public void setError(@Nullable String ex);
}
