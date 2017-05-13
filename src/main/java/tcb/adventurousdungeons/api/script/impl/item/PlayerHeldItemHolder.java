package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class PlayerHeldItemHolder implements IItemHolder {
	private final EntityPlayer player;
	private final EnumHand hand;

	public PlayerHeldItemHolder(EntityPlayer player, EnumHand hand) {
		this.player = player;
		this.hand = hand;
	}

	@Override
	public ItemStack getStack() {
		return this.player.getHeldItem(this.hand).copy();
	}

	@Override
	public void setStack(ItemStack stack) {
		this.player.setHeldItem(this.hand, stack.copy());
	}
}
