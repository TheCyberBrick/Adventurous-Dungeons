package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component converts a Vec3i into a Vec3d
 */
public class ToVec3dSC extends DungeonScriptComponent {
	private InputPort<Vec3i> in;
	private OutputPort<Vec3d> out;

	public ToVec3dSC(Script script) {
		super(script);
	}

	public ToVec3dSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Vec3i.class, true);
		this.out = this.out("out", Vec3d.class);
	}

	@Override
	protected void run() throws ScriptException {
		Vec3i in = this.get(this.in);
		this.put(this.out, new Vec3d(in.getX(), in.getY(), in.getZ()));
	}
}
