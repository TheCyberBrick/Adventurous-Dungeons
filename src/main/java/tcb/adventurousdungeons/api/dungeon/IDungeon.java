package tcb.adventurousdungeons.api.dungeon;

import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.StorageID;

public interface IDungeon extends ILocalStorage, ITickable {
	/**
	 * Returns whether the specified player has the right to edit
	 * the dungeon properties
	 * @param player
	 * @return
	 */
	public boolean canPlayerEdit(EntityPlayer player);
	
	/**
	 * Adds a dungeon component
	 * @param component
	 */
	public void addDungeonComponent(IDungeonComponent component);

	/**
	 * Removes a dungeon component
	 * @param component
	 */
	public void removeDungeonComponent(IDungeonComponent component);

	/**
	 * Returns the specified dungeon component
	 * @param id
	 */
	public IDungeonComponent getDungeonComponent(StorageID id);
	
	/**
	 * Returns an unmodifiable list of dungeon components
	 * @return
	 */
	public Collection<IDungeonComponent> getDungeonComponents();

	/**
	 * Updates the dungeon each tick
	 */
	public void update();

	/**
	 * Propagates an event to all dungeon components
	 * @param event
	 * @return
	 */
	public DungeonEvent fireEvent(DungeonEvent event);

	/**
	 * Links all chunks that intersect with the bounding box
	 * and unlinks any chunks that do not intersect
	 */
	public default void linkChunks() {
		this.unlinkAllChunks();

		AxisAlignedBB boundingBox = this.getBoundingBox();
		int sx = MathHelper.floor(boundingBox.minX / 16);
		int sz = MathHelper.floor(boundingBox.minZ / 16);
		int ex = MathHelper.floor(boundingBox.maxX / 16);
		int ez = MathHelper.floor(boundingBox.maxZ / 16);
		for(int cx = sx; cx <= ex; cx++) {
			for(int cz = sz; cz <= ez; cz++) {
				Chunk chunk = this.getWorldStorage().getWorld().getChunkFromChunkCoords(cx, cz);
				this.linkChunk(chunk);
			}
		}
	}
}
