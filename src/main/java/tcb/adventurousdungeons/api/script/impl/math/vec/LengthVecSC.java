package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns the length of a Vec3d
 */
public class LengthVecSC extends DungeonScriptComponent {
	private InputPort<Vec3d> in;
	private OutputPort<Double> out;

	public LengthVecSC(Script script) {
		super(script);
	}

	public LengthVecSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Vec3d.class, true);
		this.out = this.out("out", Double.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in).lengthVector());
	}
}
