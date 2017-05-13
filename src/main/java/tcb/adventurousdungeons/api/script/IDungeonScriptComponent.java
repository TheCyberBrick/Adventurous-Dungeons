package tcb.adventurousdungeons.api.script;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;
import tcb.adventurousdungeons.api.dungeon.event.DungeonEvent;
import tcb.adventurousdungeons.api.script.gui.GuiScriptComponent;
import tcb.adventurousdungeons.client.gui.GuiEditScript;

public interface IDungeonScriptComponent extends ISerializableScriptComponent<NBTTagCompound> {
	public ScriptDC getDungeonComponent();

	public void setDungeonComponent(ScriptDC component);

	public default void onEvent(DungeonEvent event) {

	}

	@Override
	public default Class<NBTTagCompound> getSerializer() {
		return NBTTagCompound.class;
	}

	public void setGuiX(float x);

	public void setGuiY(float y);

	public float getGuiX();

	public float getGuiY();

	public boolean isAdditionalInfoOpen();

	public void setAdditionalInfoOpen(boolean open);

	@Nullable
	public List<Vec3d> getSplinePoints(String port);

	public void setSplinePoints(String port, @Nullable List<Vec3d> points);

	@SideOnly(Side.CLIENT)
	public default GuiScriptComponent getComponentGui(GuiEditScript gui) {
		return new GuiScriptComponent(gui, this);
	}
}
