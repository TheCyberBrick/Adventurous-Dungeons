package tcb.adventurousdungeons.api.script.gui;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.Script;

public class GuiCreateScriptComponentSimple<T extends IScriptComponent> extends GuiCreateScriptComponent<T> {
	public static interface ISimpleScriptComponentFactory<T extends IScriptComponent> {
		public T create(Script script, String name);
	}

	private final ISimpleScriptComponentFactory<T> factory;

	public GuiCreateScriptComponentSimple(GuiScreen parent, Class<T> componentType, Consumer<IScriptComponent> add, Script script, @Nullable T component, float x, float y, ISimpleScriptComponentFactory<T> factory) {
		super(parent, componentType, add, script, component, x, y);
		this.factory = factory;
	}

	@Override
	protected T create(Script script, String name) {
		return this.factory.create(script, name);
	}
}
