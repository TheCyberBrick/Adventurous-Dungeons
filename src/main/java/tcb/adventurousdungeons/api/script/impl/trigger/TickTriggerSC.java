package tcb.adventurousdungeons.api.script.impl.trigger;

import tcb.adventurousdungeons.api.dungeon.event.DungeonUpdateEvent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonUpdateEvent.Phase;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

public class TickTriggerSC extends EventTriggerSC<DungeonUpdateEvent> {
	private OutputPort<Object> out_pre;
	private OutputPort<Object> out_post;

	public TickTriggerSC(Script script) {
		super(script);
	}

	public TickTriggerSC(Script script, String name) {
		super(script, name);
	}

	@Override
	public Class<DungeonUpdateEvent> getEventType() {
		return DungeonUpdateEvent.class;
	}

	@Override
	protected void createPorts() {
		this.out_pre = this.out("pre");
		this.out_post = this.out("post");
	}

	@Override
	protected boolean runTrigger(DungeonUpdateEvent event) throws ScriptException {
		if(event.getTickPhase() == Phase.START) {
			this.ignore(this.out_post);
		} else {
			this.ignore(this.out_pre);
		}
		return true;
	}
}
