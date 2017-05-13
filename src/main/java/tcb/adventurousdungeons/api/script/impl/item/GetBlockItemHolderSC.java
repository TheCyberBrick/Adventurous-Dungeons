package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns an item holder for the specified inventory slot of a block
 */
public class GetBlockItemHolderSC extends DungeonScriptComponent {
	private InputPort<Vec3i> in_pos;
	private InputPort<Integer> in_slot;
	private InputPort<EnumFacing> in_side;
	private OutputPort<IItemHolder> out;

	public GetBlockItemHolderSC(Script script) {
		super(script);
	}

	public GetBlockItemHolderSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_pos = this.in("pos", Vec3i.class, true);
		this.in_slot = this.in("slot", Integer.class, true);
		this.in_side = this.in("slot", EnumFacing.class, false);
		this.out = this.out("holder", IItemHolder.class);
	}

	@Override
	protected void run() throws ScriptException {
		BlockPos pos = new BlockPos(this.get(this.in_pos));
		TileEntity te = this.getDungeonComponent().getWorld().getTileEntity(pos);
		if(te != null) {
			EnumFacing side = this.get(this.in_side);
			if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
				IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
				int slot = this.get(this.in_slot);
				if(slot < 0 || slot >= handler.getSlots()) {
					throw new ScriptException(this, String.format("Invalid TileEntity inventory slot %d at [%d, %d, %d]", slot, pos.getX(), pos.getY(), pos.getZ()));
				}
				this.put(this.out, new GenericItemHolder(handler, slot));
			} else {
				throw new ScriptException(this, String.format("TileEntity at position [%d, %d, %d] does not have a valid inventory (i.e. IItemHandler capability)", pos.getX(), pos.getY(), pos.getZ()));
			}
		} else {
			throw new ScriptException(this, String.format("Block at position [%d, %d, %d] is not a TileEntity", pos.getX(), pos.getY(), pos.getZ()));
		}
	}
}
