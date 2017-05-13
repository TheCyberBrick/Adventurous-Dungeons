package tcb.adventurousdungeons.api.dungeon.component;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.gui.GuiEditDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.storage.StorageID;

public interface IDungeonComponent {
	/**
	 * Called when the component is initialized
	 */
	public void init();

	/**
	 * Returns the data manager responsible for data synchronization
	 * @return
	 */
	public ComponentDataManager getDataManager();

	/**
	 * Called when the value of a data parameter changes
	 * @param key
	 */
	public void notifyDataManagerChange(DataParameter<?> key);
	
	/**
	 * Returns the component ID
	 * @return
	 */
	public StorageID getID();

	/**
	 * Returns the component's name, if none was specified
	 * {@link #getID()} is returned
	 * @return
	 */
	public String getName();

	/**
	 * Sets the component's name, may be null
	 * @param name
	 */
	public void setName(@Nullable String name);

	/**
	 * Returns the dungeon
	 * @return
	 */
	public IDungeon getDungeon();

	/**
	 * Returns the world
	 * @return
	 */
	public World getWorld();
	
	/**
	 * Writes the component data to NBT
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt);

	/**
	 * Reads the component data from NBT
	 */
	public void readFromNBT(NBTTagCompound nbt);

	/**
	 * Sets the component state to the specified NBT
	 * @param nbt
	 */
	public void setComponentState(NBTTagCompound nbt);

	/**
	 * Updates the component each tick
	 */
	public void update();

	/**
	 * Called when the dungeon is unloaded
	 */
	public void onUnloaded();

	/**
	 * Called when an event is fired from the dungeon
	 * @param event
	 */
	public void onEvent(DungeonEvent event);

	/**
	 * Returns whether the component should be removed
	 * @return
	 */
	public boolean isDead();

	/**
	 * Sets the component to be removed
	 */
	public void setDead();

	@SideOnly(Side.CLIENT)
	public default GuiEditDungeonComponent getComponentGui() {
		return new GuiEditDungeonComponent(this);
	}
}
