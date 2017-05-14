package tcb.adventurousdungeons.api.script;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IScriptComponentCreationGuiFactory<T extends IScriptComponent> {
	/**
	 * Returns a GUI that is used to create a new instance of a component or edit an already existing component.
	 * Components must be added using the 'add' {@link Consumer}
	 * @param parent The parent GuiScreen
	 * @param add The consumer to be used to add the new script component
	 * @param script The script. <b>Do not add components here!</b>
	 * @param component The input component. May be null if a new component is created. Do not modify this component, any changes made to it will be lost
	 * @param x The GUI x coordinate where the component is added
	 * @param y The GUI y coordinate where the component is added
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable T component, float x, float y);

	/**
	 * Returns whether the GUI is for editing an already existing component only
	 * @return
	 */
	public default boolean isEditOnly() {
		return false;
	}
}
