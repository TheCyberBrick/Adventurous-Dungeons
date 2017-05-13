package tcb.adventurousdungeons.api.dungeon.event;

import net.minecraft.entity.Entity;
import tcb.adventurousdungeons.api.dungeon.component.impl.EntityAreaTriggerDC;

public class EntityAreaTriggerEvent extends ComponentEvent<EntityAreaTriggerDC> {
	private final Entity entity;
	private final boolean enter;

	public EntityAreaTriggerEvent(EntityAreaTriggerDC component, Entity entity, boolean enter) {
		super(component);
		this.entity = entity;
		this.enter = enter;
	}

	/**
	 * The entity that has entered or left the area
	 * @return
	 */
	public Entity getEntity() {
		return this.entity;
	}

	/**
	 * Whether the entity has entered the area
	 * @return
	 */
	public boolean isEntering() {
		return this.enter;
	}

	/**
	 * Whether the entity has left the area
	 * @return
	 */
	public boolean isLeaving() {
		return !this.enter;
	}
}
