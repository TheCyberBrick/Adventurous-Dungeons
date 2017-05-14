package tcb.adventurousdungeons.api.script.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import tcb.adventurousdungeons.api.script.IDungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.registries.ScriptComponentRegistry;

public abstract class GuiCreateScriptComponent<T extends IScriptComponent> extends GuiScreen {
	private Class<T> componentType;
	private T component;
	private GuiScreen parent;
	private Script script;
	private float x, y;
	private Consumer<IScriptComponent> add;

	private int resetButtonId;

	private Map<Integer, GuiTextField> textFields = new HashMap<>();
	protected GuiTextField componentNameField;

	public GuiCreateScriptComponent(GuiScreen parent, Class<T> componentType, Consumer<IScriptComponent> add, Script script, @Nullable T component, float x, float y) {
		this.parent = parent;
		this.script = script;
		this.x = x;
		this.y = y;
		this.add = add;
		this.componentType = componentType;
		this.component = component;
	}

	protected void addTextField(GuiTextField gui) {
		this.textFields.put(gui.getId(), gui);
	}

	protected GuiTextField getTextField(int id) {
		return this.textFields.get(id);
	}

	public GuiScreen getParent() {
		return this.parent;
	}

	public T getInputComponent() {
		return this.component;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.addCreateSaveButton(2, 44, 80, 20);
		this.addNameField();
	}

	protected int addCreateSaveButton(int x, int y, int width, int height) {
		this.buttonList.add(new GuiButton(0, x, y, width, height, this.getInputComponent() != null ? "Save" : "Create"));
		return 0;
	}

	protected int addNameField() {
		int id = this.getUniqueButtonID();

		this.buttonList.add(new GuiButton(id, 84, 22, 40, 20, "Reset"));
		this.resetButtonId = id;

		this.componentNameField = new GuiTextField(0, this.fontRendererObj, 2, 22, 80, 20);
		if(this.getInputComponent() != null) {
			this.componentNameField.setText(this.getInputComponent().getName());
		} else {
			this.componentNameField.setText(I18n.format(ScriptComponentRegistry.INSTANCE.getUnlocalizedName(this.componentType)));
		}

		return id;
	}

	protected int getUniqueButtonID() {
		boolean[] takenIds = new boolean[this.buttonList.size()];
		for(GuiButton button : this.buttonList) {
			if(button.id >= 0 && button.id < this.buttonList.size()) {
				takenIds[button.id] = true;
			}
		}
		int id = takenIds.length;
		for(int i = 0; i < takenIds.length; i++) {
			if(!takenIds[i]) {
				id = i;
			}
		}
		return id;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		this.componentNameField.updateCursorCounter();

		for(GuiTextField field : this.textFields.values()) {
			field.updateCursorCounter();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(keyCode == Keyboard.KEY_ESCAPE) {
			this.mc.displayGuiScreen(this.parent);
		} else {
			if(this.componentNameField.isFocused()) {
				this.componentNameField.textboxKeyTyped(typedChar, keyCode);
			}

			for(GuiTextField field : this.textFields.values()) {
				if(field.isFocused()) {
					field.textboxKeyTyped(typedChar, keyCode);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		this.componentNameField.mouseClicked(mouseX, mouseY, mouseButton);

		for(GuiTextField field : this.textFields.values()) {
			field.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		this.fontRendererObj.drawString("Create " + ScriptComponentRegistry.INSTANCE.getComponentID(this.componentType).toString(), 2, 2, 0xFFFFFFFF);

		this.fontRendererObj.drawString("Name: ", 2, 12, 0xFFFFFFFF);

		this.componentNameField.drawTextBox();

		for(GuiTextField field : this.textFields.values()) {
			field.drawTextBox();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button.id == 0) {
			T inst = this.create(this.script, this.componentNameField.getText());
			if(inst != null) {
				if(inst instanceof IDungeonScriptComponent) {
					((IDungeonScriptComponent)inst).setGuiX(this.x);
					((IDungeonScriptComponent)inst).setGuiY(this.y);
				}
				this.add.accept(inst);
				this.mc.displayGuiScreen(this.parent);
			}
		} else if(button.id == this.resetButtonId) {
			this.componentNameField.setText(I18n.format(ScriptComponentRegistry.INSTANCE.getUnlocalizedName(this.componentType)));
		}
	}

	/**
	 * Creates the component instance, may return null if
	 * something is missing or didn't work
	 * @return
	 */
	@Nullable
	protected abstract T create(Script script, String name);
}
