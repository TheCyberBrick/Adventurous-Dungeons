package tcb.adventurousdungeons.api.script.impl;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component creates an AABB from one or two Vec3d input values.
 * The second Vec3d input value is optional
 */
public class CreateABBSC extends DungeonScriptComponent {
	private InputPort<Vec3d> in_p1;
	private InputPort<Vec3d> in_p2;
	private OutputPort<AxisAlignedBB> out_aabb;

	public CreateABBSC(Script script) {
		super(script);
	}

	public CreateABBSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_p1 = this.in("p1", Vec3d.class, true);
		this.in_p2 = this.in("p2", Vec3d.class, false);
		this.out_aabb = this.out("aabb", AxisAlignedBB.class);
	}

	@Override
	protected void run() throws ScriptException {
		Vec3d p1 = this.get(this.in_p1);
		Vec3d p2 = this.get(this.in_p2);
		if(p2 == null) {
			p2 = p1;
		}
		this.put(this.out_aabb, new AxisAlignedBB(p1.xCoord, p1.yCoord, p1.zCoord, p2.xCoord, p2.yCoord, p2.zCoord));
	}
}
