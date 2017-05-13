package tcb.adventurousdungeons.api.script.impl.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns the AABB of an entity
 */
public class GetEntityBoundsSC extends DungeonScriptComponent {
	private InputPort<Entity> in;
	private OutputPort<AxisAlignedBB> out;

	public GetEntityBoundsSC(Script script) {
		super(script);
	}

	public GetEntityBoundsSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Entity.class, true);
		this.out = this.out("out", AxisAlignedBB.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in).getEntityBoundingBox());
	}
}
