package tcb.adventurousdungeons.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.registries.ScriptComponentRegistry;

public class GuiAddScriptComponents extends GuiScreen {
	private GuiEditScript parent;
	private Script script;
	private float x, y;
	private List<IScriptComponentCreationGuiFactory<?>> factories = new ArrayList<>();

	public GuiAddScriptComponents(GuiEditScript parent, Script script, float x, float y) {
		this.parent = parent;
		this.script = script;
		this.x = x;
		this.y = y;
	}

	public GuiEditScript getParent() {
		return this.parent;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.factories.clear();

		ScaledResolution res = new ScaledResolution(this.mc);

		int id = 0;
		int xOff = 0;
		int yOff = 0;
		for(Class<? extends IScriptComponent> component : ScriptComponentRegistry.INSTANCE.getRegisteredComponents()) {
			IScriptComponentCreationGuiFactory<?> factory = ScriptComponentRegistry.INSTANCE.getFactoryGui(component);
			if(factory != null && !factory.isEditOnly()) {
				this.buttonList.add(new GuiButton(id, 2 + xOff, 2 + yOff, I18n.format(ScriptComponentRegistry.INSTANCE.getUnlocalizedName(component))));
				this.factories.add(factory);
				id++;

				yOff += 21;

				if(yOff + 21 >= res.getScaledHeight()) {
					yOff = 0;
					xOff += 201;
				}
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(keyCode == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen(this.parent);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button.id < this.factories.size()) {
			IScriptComponentCreationGuiFactory<?> factory = this.factories.get(button.id);
			GuiScreen factoryGui = factory.getFactoryGui(this.parent, (component) -> this.parent.addComponent(component), this.script, null, this.x, this.y);
			this.mc.displayGuiScreen(factoryGui);
		}
	}
}
