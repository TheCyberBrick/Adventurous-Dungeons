package tcb.adventurousdungeons.api.script.impl.trigger;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.event.EntityAreaTriggerEvent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

public class EntityAreaTriggerSC extends EventTriggerSC<EntityAreaTriggerEvent> {
	private OutputPort<Entity> out_entity;
	private OutputPort<AxisAlignedBB> out_aabb;
	private OutputPort<Boolean> out_enter;

	private InputPort<?> in;

	public EntityAreaTriggerSC(Script script) {
		super(script);
	}

	public EntityAreaTriggerSC(Script script, String name) {
		super(script, name);
	}

	@Override
	public Class<EntityAreaTriggerEvent> getEventType() {
		return EntityAreaTriggerEvent.class;
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", IDungeonComponent.class, true);
		this.out_entity = this.out("entity", Entity.class);
		this.out_aabb = this.out("aabb", AxisAlignedBB.class);
		this.out_enter = this.out("enter", Boolean.class);
	}

	@Override
	protected void runTrigger(EntityAreaTriggerEvent event) throws ScriptException {
		if(event.getComponent() == this.get(this.in)) {
			this.put(this.out_entity, event.getEntity());
			this.put(this.out_aabb, event.getComponent().getBounds());
			this.put(this.out_enter, event.isEntering());
		}
	}
}
