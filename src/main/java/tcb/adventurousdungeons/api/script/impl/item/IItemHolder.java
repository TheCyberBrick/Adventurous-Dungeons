package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.item.ItemStack;

public interface IItemHolder {
	/**
	 * Returns a <b>copy</b> of the item stack
	 * @return
	 */
	public ItemStack getStack();
	
	/**
	 * Sets the stack in the slot to a <b>copy</b> of the specified stack
	 * @param stack
	 */
	public void setStack(ItemStack stack);
}
