package tcb.adventurousdungeons.api.script.impl.bool;

import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns whether the two input AABBs intersect
 */
public class IntersectsAABBSC extends DungeonScriptComponent {
	private InputPort<AxisAlignedBB> in_0;
	private InputPort<AxisAlignedBB> in_1;
	private OutputPort<Boolean> out;

	public IntersectsAABBSC(Script script) {
		super(script);
	}

	public IntersectsAABBSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_0 = this.in("aabb_0", AxisAlignedBB.class, true);
		this.in_1 = this.in("aabb_1", AxisAlignedBB.class, true);
		this.out = this.out("out", Boolean.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_0).intersectsWith(this.get(this.in_1)));
	}
}
