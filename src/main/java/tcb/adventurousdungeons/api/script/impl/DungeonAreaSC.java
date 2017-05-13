package tcb.adventurousdungeons.api.script.impl;

import net.minecraft.util.math.AxisAlignedBB;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.ILocalDungeonComponent;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

/**
 * This component returns the specified dungeon area
 */
public class DungeonAreaSC extends DungeonScriptComponent {
	private OutputPort<AxisAlignedBB> out;
	private InputPort<IDungeonComponent> in;

	public DungeonAreaSC(Script script) {
		super(script);
	}

	public DungeonAreaSC(Script script, String name) {
		super(script, name);
	}

	@Override
	protected void createPorts() {
		this.in = this.in("in", IDungeonComponent.class, true);
		this.out = this.out("out", AxisAlignedBB.class);		
	}

	@Override
	protected void run() throws ScriptException {
		IDungeonComponent component = this.get(this.in);
		if(component instanceof ILocalDungeonComponent) {
			this.put(this.out, ((ILocalDungeonComponent)component).getBounds());
		} else {
			throw new ScriptException(this, "Dungeon component does not have a bounding box");
		}
	}
}
