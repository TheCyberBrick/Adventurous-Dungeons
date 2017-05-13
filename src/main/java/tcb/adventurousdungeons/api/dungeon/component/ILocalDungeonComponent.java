package tcb.adventurousdungeons.api.dungeon.component;

import net.minecraft.util.math.AxisAlignedBB;

public interface ILocalDungeonComponent extends IDungeonComponent {
	public void setBounds(AxisAlignedBB aabb);

	public AxisAlignedBB getBounds();
}
