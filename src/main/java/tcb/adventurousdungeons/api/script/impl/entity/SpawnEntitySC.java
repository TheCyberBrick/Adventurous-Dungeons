package tcb.adventurousdungeons.api.script.impl.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component spawns an entity in the world
 */
public class SpawnEntitySC extends DungeonScriptComponent {
	private InputPort<Entity> in_entity;
	private InputPort<Vec3d> in_pos;
	private InputPort<Vec3d> in_rot;
	private OutputPort<Entity> out;

	public SpawnEntitySC(Script script) {
		super(script);
	}

	public SpawnEntitySC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_entity = this.in("entity", Entity.class, true);
		this.in_pos = this.in("pos", Vec3d.class, true);
		this.in_rot = this.in("rot", Vec3d.class, false);
		this.out = this.out("out", Entity.class);
	}

	@Override
	protected void run() throws ScriptException {
		Entity entity = this.get(this.in_entity);
		Vec3d pos = this.get(this.in_pos);
		Vec3d rot = this.get(this.in_rot);
		entity.setLocationAndAngles(pos.xCoord, pos.yCoord, pos.zCoord, (float)(rot != null ? rot.yCoord : 0), (float)(rot != null ? rot.xCoord : 0));
		this.getDungeonComponent().getWorld().spawnEntity(entity);

		this.put(this.out, entity);
	}
}
