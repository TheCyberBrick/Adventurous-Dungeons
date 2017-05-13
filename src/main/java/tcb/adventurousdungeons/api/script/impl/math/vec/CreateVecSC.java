package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component creates a 3D vector from 3 values
 */
public class CreateVecSC extends DungeonScriptComponent {
	private InputPort<Double> in_x;
	private InputPort<Double> in_y;
	private InputPort<Double> in_z;
	private OutputPort<Vec3d> out;

	public CreateVecSC(Script script) {
		super(script);
	}

	public CreateVecSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_x = this.in("x", Double.class, false);
		this.in_y = this.in("y", Double.class, false);
		this.in_z = this.in("z", Double.class, false);
		this.out = this.out("out", Vec3d.class);
	}

	@Override
	protected void run() throws ScriptException {
		Double x = this.get(this.in_x);
		Double y = this.get(this.in_y);
		Double z = this.get(this.in_z);
		this.put(this.out, new Vec3d(x != null ? x : 0, y != null ? y : 0, z != null ? z : 0));
	}
}
