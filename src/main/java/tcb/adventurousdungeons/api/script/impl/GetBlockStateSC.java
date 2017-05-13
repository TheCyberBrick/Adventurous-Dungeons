package tcb.adventurousdungeons.api.script.impl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns the IBlockState at the input position
 */
public class GetBlockStateSC extends DungeonScriptComponent {
	private InputPort<Vec3i> in;
	private OutputPort<IBlockState> out;

	public GetBlockStateSC(Script script) {
		super(script);
	}

	public GetBlockStateSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", Vec3i.class, true);
		this.out = this.out("out", IBlockState.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, this.getDungeonComponent().getWorld().getBlockState(new BlockPos(this.get(this.in))));
	}
}
