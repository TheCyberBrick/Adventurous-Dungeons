package tcb.adventurousdungeons.api.dungeon.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import tcb.adventurousdungeons.api.dungeon.IDungeon;

public class DungeonEvent extends Event {
	private final IDungeon dungeon;

	public DungeonEvent(IDungeon dungeon) {
		this.dungeon = dungeon;
	}

	/**
	 * The dungeon this event belongs to
	 * @return
	 */
	public IDungeon getDungeon() {
		return this.dungeon;
	}
}
