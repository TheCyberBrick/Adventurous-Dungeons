package tcb.adventurousdungeons.api.script.impl;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.PortCastException;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.util.AABBUtil;

/**
 * This component sets the input area to the input block states
 */
public class SetBlocksSC extends DungeonScriptComponent {
	private InputPort<IBlockState> in_block;
	private InputPort<AxisAlignedBB> in_aabb;

	public SetBlocksSC(Script script) {
		super(script);
	}

	public SetBlocksSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_block = this.in("block", IBlockState.class, true);
		this.in_aabb = this.in("aabb", AxisAlignedBB.class, true);
	}

	@Override
	protected void run() throws PortCastException {
		AABBUtil aabb = new AABBUtil(this.getDungeonComponent().getDungeon().getWorldStorage().getWorld(), this.get(this.in_aabb).contract(0.001D));
		IBlockState state = this.get(this.in_block);
		aabb.processBlocks(pos -> {
			this.getDungeonComponent().getDungeon().getWorldStorage().getWorld().setBlockState(pos, state);
		}, true);
	}
}
