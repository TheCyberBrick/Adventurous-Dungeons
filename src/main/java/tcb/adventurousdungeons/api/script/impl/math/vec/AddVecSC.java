package tcb.adventurousdungeons.api.script.impl.math.vec;

import net.minecraft.util.math.Vec3d;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component adds two vectors together
 */
public class AddVecSC extends DungeonScriptComponent {
	private InputPort<Vec3d> in_p1;
	private InputPort<Vec3d> in_p2;
	private OutputPort<Vec3d> out;

	public AddVecSC(Script script) {
		super(script);
	}

	public AddVecSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_p1 = this.in("p1", Vec3d.class, true);
		this.in_p2 = this.in("p2", Vec3d.class, true);
		this.out = this.out("out", Vec3d.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.get(this.in_p1).add(this.get(this.in_p2)));
	}
}
