package tcb.adventurousdungeons.api.script;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public abstract class ScriptComponent implements IScriptComponent {
	/**
	 * If the value of a port is set to this value it will
	 * not run the connected component, or return null
	 * if a value is requested
	 */
	private static final Object IGNORE = new Object();

	private final List<Port<?>> allPorts = new ArrayList<>();
	private final List<InputPort<?>> inputs = new ArrayList<>();
	private final List<OutputPort<?>> outputs = new ArrayList<>();

	private int numPorts = 0;
	private Object[] values; //Contains the input and output values of the ports

	private String name;
	private final Script script;

	private Port<?> callerPort;

	private UUID uuid;

	private String error;

	public ScriptComponent(Script script) {
		this(script, null);
	}

	public ScriptComponent(Script script, String name) {
		this.name = name;
		this.script = script;

		this.uuid = UUID.randomUUID();
	}

	@Override
	public void setID(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public UUID getID() {
		return this.uuid;
	}

	@Override
	public final void initPorts() {
		this.allPorts.clear();
		this.inputs.clear();
		this.outputs.clear();
		this.numPorts = 0;

		this.createPorts();

		//Always have at least one input and output for program flow
		if(this.hasProgramFlowInput() && this.inputs.isEmpty()) {
			this.in("trigger");
		}
		if(this.hasProgramFlowOutput() && this.outputs.isEmpty()) {
			this.out("trigger");
		}

		this.allPorts.addAll(this.inputs);
		this.allPorts.addAll(this.outputs);
		this.values = new Object[this.numPorts];
	}

	protected abstract void createPorts();

	protected final Port<?> getCallerPort() {
		return this.callerPort;
	}

	protected boolean hasProgramFlowInput() {
		return true;
	}

	protected boolean hasProgramFlowOutput() {
		return true;
	}

	@Override
	public final Script getScript() {
		return this.script;
	}

	@Override
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public final String getName() {
		return this.name;
	}

	protected final TriggerInputPort in(String name) {
		TriggerInputPort port;
		this.inputs.add(port = new TriggerInputPort(this, this.numPorts++, name));
		return port;
	}

	protected final TriggerOutputPort out(String name) {
		TriggerOutputPort port;
		this.outputs.add(port = new TriggerOutputPort(this, this.numPorts++, name));
		return port;
	}

	protected final <T> InputPort<T> in(String name, Class<T> type, boolean isRequiredNonNull) {
		InputPort<T> port;
		this.inputs.add(port = new InputPort<>(this, this.numPorts++, name, type, isRequiredNonNull, true));
		return port;
	}

	protected final <T> InputPort<T> in(String name, Class<T> type, boolean isRequiredNonNull, boolean triggersComponent) {
		InputPort<T> port;
		this.inputs.add(port = new InputPort<>(this, this.numPorts++, name, type, isRequiredNonNull, triggersComponent));
		return port;
	}

	protected final <T> OutputPort<T> out(String name, Class<T> type) {
		OutputPort<T> port;
		this.outputs.add(port = new OutputPort<>(this, this.numPorts++, name, type, false));
		return port;
	}

	protected final <T extends Iterable<?>> OutputPort<T> out(String name, Class<T> type, boolean splitIterable) {
		OutputPort<T> port;
		this.outputs.add(port = new OutputPort<>(this, this.numPorts++, name, type, splitIterable));
		return port;
	}

	protected final <T> T get(InputPort<T> port) throws PortCastException {
		Object val = this.values[port.getID()];
		if(val != null && !port.getDataType().isAssignableFrom(val.getClass())) {
			throw new PortCastException(this, port, val.getClass(), port.getDataType(), String.format("Failed to cast input value with type '%s' of port '%s', component '%s' to type '%s'", val.getClass().getSimpleName(), port.getName(), port.getComponent().getName(), port.getDataType().getSimpleName()));
		}
		if(val == IGNORE) {
			return null;
		}
		@SuppressWarnings("unchecked")
		T value = (T) val;
		if(value != null) {
			return value;
		}
		return null;
	}

	protected final <T> void put(OutputPort<T> port, T output) {
		this.values[port.getID()] = output;
	}

	/**
	 * The specified output port will be ignore and will not trigger
	 * the connected component. If a value was requested from this port, null
	 * is returned
	 * @param port
	 */
	protected final void ignore(OutputPort<?> port) {
		this.values[port.getID()] = IGNORE;
	}

	private final <T> T retrieveOutputValue(InputPort<T> callee) throws PortCastException {
		if(callee.isConnected() && callee.getConnectedPort().getComponent() == this) {
			Object val = this.values[callee.getConnectedPort().getID()];
			if(val == IGNORE) {
				return null;
			}
			if(val != null && !callee.getDataType().isAssignableFrom(val.getClass()) && !((OutputPort<T>)callee.getConnectedPort()).isMultiOutput()) {
				throw new PortCastException(callee.getComponent(), callee, val.getClass(), callee.getDataType(), String.format("Failed to cast input value with type '%s' of port '%s', component '%s' to type '%s'", val.getClass().getSimpleName(), callee.getName(), callee.getComponent().getName(), callee.getDataType().getSimpleName()));
			}
			@SuppressWarnings("unchecked")
			T value = (T) val;
			if(value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final List<InputPort<?>> getInputs() {
		return Collections.unmodifiableList(this.inputs);
	}

	@Override
	public final List<OutputPort<?>> getOutputs() {
		return Collections.unmodifiableList(this.outputs);
	}

	@Override
	public List<Port<?>> getPorts() {
		return Collections.unmodifiableList(this.allPorts);
	}

	@Override
	public final void execute() throws ScriptException {
		this.execute(true, null, null, null);
	}

	private final void execute(boolean runOutputs, @Nullable Port<?> caller, @Nullable Object callerValue, @Nullable Set<ScriptComponent> callerStack) throws ScriptException {
		this.script.onComponentExecute(this);

		this.setError(null);

		if(caller != null) {
			if(caller.isConnected() && caller.getConnectedPort().getComponent() == this) {
				this.callerPort = caller.getConnectedPort();
			}
		}

		for(int i = 0; i < this.numPorts; i++) {
			this.values[i] = null;
		}
		if(this.callerPort != null) {
			this.values[this.callerPort.getID()] = callerValue;
		}

		//Components that were executed to generate input values
		List<ScriptComponent> inputComponents = new ArrayList<>();

		InputPort<?> multiInputPort = null;
		Iterator<?> multiInput = null;

		for(InputPort<?> inputPort : this.inputs) {
			if(this.callerPort != null && inputPort == this.callerPort) {
				continue;
			}
			if(inputPort.isRequiredNonNull() && !inputPort.isConnected()) {
				throw new ScriptException(this, String.format("Input port '%s' of component '%s' is required but not connected", inputPort.getName(), this.getName()));
			}
			if(inputPort.isConnected()) {
				/*if(this.getName().contains("Test")) {
					System.out.println("REQUESTED VAL FOR PORT " + inputPort.getName());
				}

				if(this.getName().contains("Tset")) {
					System.out.println("REQUESTED VAL 2 FOR PORT " + inputPort.getName());
				}*/

				ScriptComponent inputComponent = inputPort.getConnectedPort().getComponent();
				if(callerStack == null || !callerStack.contains(inputComponent)) {
					inputComponent.execute(false, inputPort, null, callerStack);
					if(callerStack != null) {
						//callerStack.add(inputComponent);
					}
				}
				Object value = inputComponent.retrieveOutputValue(inputPort);

				if(value == null && inputPort.isRequiredNonNull()) {
					throw new ScriptException(this, String.format("Input port '%s' of component '%s' received null from output port '%s' of component '%s'", inputPort.getName(), this.getName(), inputPort.getConnectedPort().getName(), inputPort.getConnectedPort().getComponent().getName()));
				}

				if(((OutputPort<?>)inputPort.getConnectedPort()).isMultiOutput() && value != null) {
					if(multiInput != null) {
						throw new ScriptException(this, String.format("Component '%s' has more than one multi input: '%s', '%s'", this.getName(), multiInputPort.getName(), inputPort.getName()));
					}
					multiInputPort = inputPort;
					multiInput = ((Iterable<?>) value).iterator();
				} else {
					this.values[inputPort.getID()] = value;
				}

				inputComponents.add(inputComponent);
				//inputComponent.values[inputPort.getConnectedPort().getID()] = null;
			}
		}
		
		while(multiInput == null || multiInput.hasNext()) {
			if(multiInput != null) {
				this.values[multiInputPort.getID()] = multiInput.next();
			}

			this.run();

			this.callerPort = null;

			if(runOutputs) {
				//Keep track of which components have already been executed by the output ports
				//Prevents multiple output ports connected to the same component from executing it multiple times
				Set<ScriptComponent> executedStack = callerStack != null ? callerStack : new HashSet<>();

				executedStack.add(this);

				for(OutputPort<?> outputPort : this.outputs) {
					Object value = this.values[outputPort.getID()];
					if(outputPort.isConnected() && value != IGNORE && ((InputPort<?>)outputPort.getConnectedPort()).doesTriggerComponent()) {
						if(value == null && ((InputPort<?>)outputPort.getConnectedPort()).isRequiredNonNull()) {
							throw new ScriptException(outputPort.getConnectedPort().getComponent(), String.format("Input port '%s' of component '%s' received null from output port '%s' of component '%s'", outputPort.getConnectedPort().getName(), outputPort.getConnectedPort().getComponent().getName(), outputPort.getName(), this.getName()));
						}

						ScriptComponent outputComponent = outputPort.getConnectedPort().getComponent();
						if(!executedStack.contains(outputComponent)) {
							if(outputPort.isMultiOutput() && value != null) {
								Iterable<?> iterable = (Iterable<?>) value;
								Set<ScriptComponent> multiOutputExecutedStack = new HashSet<>();
								for(Object obj : iterable) {
									//Store executed components in a seperate set so that they can be executed multiple times
									Set<ScriptComponent> separateStack = new HashSet<>(executedStack);
									outputComponent.execute(true, outputPort, obj, separateStack);
									multiOutputExecutedStack.addAll(separateStack);
								}
								//Add components that have been executed to stack
								executedStack.addAll(multiOutputExecutedStack);
							} else {
								outputComponent.execute(true, outputPort, value, executedStack);
							}
						}
					}
				}

				//System.out.println("CLEAR " + this.getName());
				//Clear all output values as they are no longer needed
				/*for(OutputPort<?> port : this.getOutputs()) {
					this.values[port.getID()] = null;
				}*/
			}

			//Clear the read values of input components
			/*for(ScriptComponent inputComponent : inputComponents) {
				for(OutputPort<?> port : inputComponent.getOutputs()) {
					if(port.isConnected() && port.getConnectedPort().getComponent() == this) {
						inputComponent.values[port.getID()] = null;
					}
				}
			}*/

			if(multiInput == null) {
				break;
			} else if(multiInput.hasNext()) {
				this.script.onComponentExecute(this);
			}
		}
	}

	protected abstract void run() throws ScriptException;

	@Override
	public String getError() {
		return this.error;
	}

	@Override
	public void setError(String ex) {
		this.error = ex;
	}
}
