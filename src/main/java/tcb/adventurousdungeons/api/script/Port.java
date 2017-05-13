package tcb.adventurousdungeons.api.script;

import javax.annotation.Nullable;

public abstract class Port<T> {
	private final Class<T> type;
	private final String name;
	private final int id;
	private final boolean input;

	private Port<T> connection;
	private ScriptComponent component;

	public Port(ScriptComponent component, int id, String name, Class<T> type, boolean input) {
		this.component = component;
		this.id = id;
		this.name = name;
		this.type = type;
		this.input = input;
	}

	/**
	 * Returns whether this port is an {@link InputPort}
	 * @return
	 */
	public boolean isInput() {
		return this.input;
	}
	
	/**
	 * Returns whether this port is an {@link OutputPort}
	 * @return
	 */
	public final boolean isOutput() {
		return !this.input;
	}
	
	/**
	 * Returns the ID of the port
	 * @return
	 */
	public final int getID() {
		return this.id;
	}

	/**
	 * The component that uses this port
	 * @return
	 */
	public final ScriptComponent getComponent() {
		return this.component;
	}

	/**
	 * The name of this port
	 * @return
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * The data type of this port.
	 * {@link Void} accepts any input
	 * @return
	 */
	public final Class<T> getDataType() {
		return this.type;
	}

	/**
	 * Returns whether this port is compatible with the specified port
	 * @param connection
	 * @return
	 */
	public abstract boolean isCompatible(Port<?> connection);

	/**
	 * Connects this port with another port, or disconnects
	 * the port if null
	 * @param connection
	 * @return Whether the port was successfully connected or disconnected
	 */
	@SuppressWarnings("unchecked")
	public final boolean connect(@Nullable Port<?> connection) {
		if(connection == null) {
			Port<?> other = this.connection;
			this.connection = null;
			if(other != null) {
				other.connect(null);
			}
			return true;
		}
		if(!connection.isCompatible(this) || !this.isCompatible(connection) || this.component == connection.component) {
			return false;
		}
		Port<T> prev = this.connection;
		this.connection = (Port<T>) connection;
		if(connection.getConnectedPort() != this) {
			if(!connection.connect(this)) {
				this.connection = prev;
				return false;
			}
		}
		if(prev != null && prev != this.connection) {
			prev.connection = null;
		}
		return true;
	}

	/**
	 * Returns whether this port is connected to another port
	 * @return
	 */
	public final boolean isConnected() {
		return this.connection != null;
	}

	/**
	 * Returns the connection port
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final <C extends Port<T>> C getConnectedPort() {
		return (C) this.connection;
	}
}
