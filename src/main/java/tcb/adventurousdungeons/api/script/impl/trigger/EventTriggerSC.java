package tcb.adventurousdungeons.api.script.impl.trigger;

import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * The trigger component is run if its according event
 * is fired
 */
public abstract class EventTriggerSC<T extends DungeonEvent> extends DungeonScriptComponent {
	private T triggerEvent;

	public EventTriggerSC(Script script) {
		super(script);
	}

	public EventTriggerSC(Script script, String name) {
		super(script, name);
	}

	public abstract Class<T> getEventType();

	@Override
	protected boolean hasProgramFlowInput() {
		return false;
	}

	@Override
	protected boolean hasProgramFlowOutput() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEvent(DungeonEvent e) {
		if(!this.getDungeonComponent().getWorld().isRemote && this.getEventType().isAssignableFrom(e.getClass())) {
			this.triggerEvent = (T) e;
			try {
				//long ns = System.nanoTime();
				this.execute();
				//System.out.println((System.nanoTime() - ns) / 1000000.0F);
				this.getScript().resetScript();
			} catch (ScriptException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public final void run() throws ScriptException {
		if(this.triggerEvent != null) {
			T event = this.triggerEvent;
			this.triggerEvent = null;
			this.runTrigger(event);
		} else {
			for(OutputPort<?> out : this.getOutputs()) {
				this.ignore(out);
			}
		}
	}

	protected abstract void runTrigger(T event) throws ScriptException;
}
