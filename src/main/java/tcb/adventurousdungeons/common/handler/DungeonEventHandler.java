package tcb.adventurousdungeons.common.handler;

import java.util.List;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.event.BlockActivateEvent;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public final class DungeonEventHandler {
	@SubscribeEvent
	public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
		if(event.getHitVec() != null) {
			World world = event.getWorld();
			IWorldStorage storage = WorldStorageImpl.getCapability(world);
			BlockPos pos = event.getPos();

			List<IDungeon> dungeons = storage.getLocalStorageHandler().getLocalStorages(IDungeon.class, new AxisAlignedBB(pos), null);
			for(IDungeon dungeon : dungeons) {
				dungeon.fireEvent(new BlockActivateEvent(dungeon, event));
			}
		}
	}
}
