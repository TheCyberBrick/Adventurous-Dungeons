package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class PlayerItemHolder implements IItemHolder {
	private final InventoryPlayer inv;
	private final int slot;

	public PlayerItemHolder(InventoryPlayer inv, int slot) {
		this.inv = inv;
		this.slot = slot;
	}

	@Override
	public ItemStack getStack() {
		return this.inv.getStackInSlot(this.slot).copy();
	}

	@Override
	public void setStack(ItemStack stack) {
		this.inv.setInventorySlotContents(this.slot, stack.copy());
	}
}
