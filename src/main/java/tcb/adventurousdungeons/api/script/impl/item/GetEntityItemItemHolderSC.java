package tcb.adventurousdungeons.api.script.impl.item;

import net.minecraft.entity.item.EntityItem;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns an item holder for the specified EntityItem
 */
public class GetEntityItemItemHolderSC extends DungeonScriptComponent {
	private InputPort<EntityItem> in_entity;
	private OutputPort<IItemHolder> out;

	public GetEntityItemItemHolderSC(Script script) {
		super(script);
	}

	public GetEntityItemItemHolderSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in_entity = this.in("entity", EntityItem.class, true);
		this.out = this.out("holder", IItemHolder.class);
	}

	@Override
	protected void run() throws ScriptException {
		this.put(this.out, new EntityItemItemHolder(this.get(this.in_entity)));
	}
}
