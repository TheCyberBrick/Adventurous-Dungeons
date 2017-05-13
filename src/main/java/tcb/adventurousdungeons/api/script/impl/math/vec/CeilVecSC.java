package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component rounds up the values of a Vec3d
 */
public class CeilVecSC extends DungeonScriptComponent {
	private InputPort<Vec3d> in;
	private OutputPort<Vec3d> out;

	public CeilVecSC(Script script) {
		super(script);
	}

	public CeilVecSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Vec3d.class, true);
		this.out = this.out("out", Vec3d.class);
	}

	@Override
	protected void run() throws ScriptException {
		Vec3d in = this.get(this.in);
		this.put(this.out, new Vec3d(MathHelper.ceil(in.xCoord), MathHelper.ceil(in.yCoord), MathHelper.ceil(in.zCoord)));
	}
}
