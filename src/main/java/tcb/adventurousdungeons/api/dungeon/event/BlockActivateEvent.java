package tcb.adventurousdungeons.api.dungeon.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import tcb.adventurousdungeons.api.dungeon.IDungeon;

public class BlockActivateEvent extends DungeonEvent {
	private EntityPlayer player;
	private Vec3d pos;
	private BlockPos blockPos;
	private ItemStack item;
	private EnumHand hand;

	public BlockActivateEvent(IDungeon dungeon, PlayerInteractEvent.RightClickBlock event) {
		super(dungeon);

		this.player = event.getEntityPlayer();
		this.blockPos = event.getPos();
		this.pos = event.getHitVec();
		this.item = event.getItemStack();
		this.hand = event.getHand();
	}

	public EntityPlayer getPlayer() {
		return this.player;
	}

	public Vec3d getPos() {
		return this.pos;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public EnumHand getHand() {
		return this.hand;
	}
}
