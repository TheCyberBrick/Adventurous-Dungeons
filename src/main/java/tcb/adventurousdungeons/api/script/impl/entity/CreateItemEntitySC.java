package tcb.adventurousdungeons.api.script.impl.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component creates an item entity
 */
public class CreateItemEntitySC extends DungeonScriptComponent {
	private InputPort<ItemStack> in_item;
	private InputPort<Integer> in_pickupDelay;
	private OutputPort<EntityItem> out;

	public CreateItemEntitySC(Script script) {
		super(script);
	}

	public CreateItemEntitySC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_item = this.in("item", ItemStack.class, true);
		this.in_pickupDelay = this.in("pickup delay", Integer.class, false);
		this.out = this.out("entity", EntityItem.class);
	}

	@Override
	protected void run() throws ScriptException {
		IDungeon dungeon = this.getDungeonComponent().getDungeon();
		AxisAlignedBB aabb = dungeon.getBoundingBox();
		EntityItem entity = new EntityItem(dungeon.getWorldStorage().getWorld(), aabb.minX, aabb.minY, aabb.minZ, this.get(this.in_item));
		Integer delay = this.get(this.in_pickupDelay);
		if(delay != null) {
			entity.setPickupDelay(delay);
		}
		this.put(this.out, entity);
	}
}
