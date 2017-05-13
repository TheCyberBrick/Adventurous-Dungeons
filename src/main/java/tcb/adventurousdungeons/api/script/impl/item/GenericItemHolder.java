package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class GenericItemHolder implements IItemHolder {
	private final IItemHandler handler;
	private final int slot;

	public GenericItemHolder(IItemHandler handler, int slot) {
		this.handler = handler;
		this.slot = slot;
	}

	@Override
	public ItemStack getStack() {
		return this.handler.getStackInSlot(this.slot).copy();
	}

	@Override
	public void setStack(ItemStack stack) {
		if(this.handler instanceof IItemHandlerModifiable) {
			((IItemHandlerModifiable)this.handler).setStackInSlot(this.slot, stack.copy());
		} else {
			this.handler.extractItem(this.slot, Integer.MAX_VALUE, false);
			this.handler.insertItem(this.slot, stack.copy(), false);
		}
	}
}
