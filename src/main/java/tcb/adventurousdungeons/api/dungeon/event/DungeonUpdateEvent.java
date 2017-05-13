package tcb.adventurousdungeons.api.dungeon.event;

import tcb.adventurousdungeons.api.dungeon.IDungeon;

public class DungeonUpdateEvent extends DungeonEvent {
	public enum Phase {
		START, END;
	}

	private final Phase phase;

	public DungeonUpdateEvent(IDungeon dungeon, Phase phase) {
		super(dungeon);
		this.phase = phase;
	}

	public Phase getTickPhase() {
		return this.phase;
	}
}
