package tcb.adventurousdungeons.api.dungeon.component.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextComponentTranslation;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.common.network.serverbound.MessageDeleteComponent;
import tcb.adventurousdungeons.common.network.serverbound.MessageSetComponentName;

public class GuiEditDungeonComponent extends GuiScreen {
	private IDungeonComponent component;

	private GuiTextField componentNameField;

	public GuiEditDungeonComponent(IDungeonComponent component) {
		this.component = component;
	}

	public IDungeonComponent getComponent() {
		return this.component;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.buttonList.add(new GuiButton(0, 2, 2, 80, 20, "Set name"));
		this.buttonList.add(new GuiButton(1, 2, 24, 80, 20, "Remove"));

		this.componentNameField = new GuiTextField(0, this.fontRendererObj, 84, 2, 80, 20);
		this.componentNameField.setText(this.component.getName());
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		this.componentNameField.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if (this.componentNameField.isFocused()) {
			this.componentNameField.textboxKeyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		this.componentNameField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		this.componentNameField.drawTextBox();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);

		if(button.id == 0) {
			if(this.componentNameField.getText().length() >= 3 && this.componentNameField.getText().length() <= 64) {
				AdventurousDungeons.getNetwork().sendToServer(new MessageSetComponentName(this.component, this.componentNameField.getText()));
			} else {
				Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.component_name_too_long_short"));
			}
		}

		if(button.id == 1) {
			AdventurousDungeons.getNetwork().sendToServer(new MessageDeleteComponent(this.component));
		}
	}
}
