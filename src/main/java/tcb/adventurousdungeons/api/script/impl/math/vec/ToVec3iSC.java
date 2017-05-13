package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component rounds a Vec3d to a Vec3i
 */
public class ToVec3iSC extends DungeonScriptComponent {
	private InputPort<Vec3d> in;
	private OutputPort<Vec3i> out;

	public ToVec3iSC(Script script) {
		super(script);
	}

	public ToVec3iSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Vec3d.class, true);
		this.out = this.out("out", Vec3i.class);
	}

	@Override
	protected void run() throws ScriptException {
		Vec3d in = this.get(this.in);
		this.put(this.out, new Vec3i(Math.round(in.xCoord), Math.round(in.yCoord), Math.round(in.zCoord)));
	}
}
