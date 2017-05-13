package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

public class EntityItemItemHolder implements IItemHolder {
	private final EntityItem entity;

	public EntityItemItemHolder(EntityItem entity) {
		this.entity = entity;
	}

	@Override
	public ItemStack getStack() {
		return this.entity.getEntityItem().copy();
	}

	@Override
	public void setStack(ItemStack stack) {
		this.entity.setEntityItemStack(stack.copy());
	}
}
