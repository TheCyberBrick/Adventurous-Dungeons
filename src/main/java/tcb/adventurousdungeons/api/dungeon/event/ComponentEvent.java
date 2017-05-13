package tcb.adventurousdungeons.api.dungeon.event;

import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;

public class ComponentEvent<T extends IDungeonComponent> extends DungeonEvent {
	private final T component;

	public ComponentEvent(T component) {
		super(component.getDungeon());
		this.component = component;
	}

	/**
	 * The dungeon component that has caused this event
	 * @return
	 */
	public T getComponent() {
		return this.component;
	}
}
