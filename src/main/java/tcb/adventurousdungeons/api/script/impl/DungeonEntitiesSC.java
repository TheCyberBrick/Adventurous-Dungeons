package tcb.adventurousdungeons.api.script.impl;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;

/**
 * This component returns a list of all entities
 * in the dungeon
 */
public class DungeonEntitiesSC extends DungeonScriptComponent {
	@SuppressWarnings("rawtypes")
	private OutputPort<List> out;

	public DungeonEntitiesSC(Script script) {
		super(script);
	}

	public DungeonEntitiesSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", List.class);		
	}

	@Override
	protected void run() {
		AxisAlignedBB aabb = this.getDungeonComponent().getDungeon().getBoundingBox();
		List<Entity> entities = this.getDungeonComponent().getDungeon().getWorldStorage().getWorld().getEntitiesWithinAABB(Entity.class, aabb);
		this.put(this.out, entities);
	}
}
