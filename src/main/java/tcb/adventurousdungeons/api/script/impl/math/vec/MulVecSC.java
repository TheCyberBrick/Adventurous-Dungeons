package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component multiplies a vector by a value
 */
public class MulVecSC extends DungeonScriptComponent {
	private InputPort<Vec3d> in_vec;
	private InputPort<Double> in_mul;
	private OutputPort<Vec3d> out;

	public MulVecSC(Script script) {
		super(script);
	}

	public MulVecSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_vec = this.in("vec", Vec3d.class, true);
		this.in_mul = this.in("mul", Double.class, true);
		this.out = this.out("out", Vec3d.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_vec).scale(this.get(this.in_mul)));
	}
}
