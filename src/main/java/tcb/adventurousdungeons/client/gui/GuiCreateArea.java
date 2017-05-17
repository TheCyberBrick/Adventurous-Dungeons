package tcb.adventurousdungeons.client.gui;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.common.item.ItemAreaSelection;
import tcb.adventurousdungeons.common.network.serverbound.MessageCreateArea;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class GuiCreateArea extends GuiScreen {
	private GuiTextField componentNameField;

	@Override
	public void initGui() {
		super.initGui();

		this.buttonList.add(new GuiButton(0, 2, 2, 80, 20, "Add area"));
		this.buttonList.add(new GuiButton(1, 2, 24, 80, 20, "Add entity trigger"));

		this.componentNameField = new GuiTextField(0, this.fontRendererObj, 84, 2, 80, 20);
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

		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		if(stack == null || stack.getItem() instanceof ItemAreaSelection == false) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".no_area_selection_item"));
			return;
		}

		if(this.componentNameField.getText().length() < 3 || this.componentNameField.getText().length() > 64) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".component_name_too_long_short"));
			return;
		}

		Vec3d[] points = ((ItemAreaSelection)stack.getItem()).getSelectionPoints(stack);

		if(points[0] == null || points[1] == null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".not_enough_selections"));
			return;
		}

		AxisAlignedBB aabb = new AxisAlignedBB(points[0], points[1]);

		IDungeon dungeon = null;
		IWorldStorage worldStorage = WorldStorageImpl.getCapability(player.world);
		Collection<ILocalStorage> localStorages = worldStorage.getLocalStorageHandler().getLoadedStorages();
		for(ILocalStorage localStorage : localStorages) {
			if(localStorage instanceof IDungeon) {
				if(aabb.intersectsWith(((IDungeon) localStorage).getBoundingBox())) {
					dungeon = (IDungeon) localStorage;
					break;
				}
			}
		}

		if(dungeon == null) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("gui." + ModInfo.ID + ".no_dungeon"));
			return;
		}

		if(button.id == 0) {
			AdventurousDungeons.getNetwork().sendToServer(new MessageCreateArea(dungeon, aabb, this.componentNameField.getText(), 0));
		}

		if(button.id == 1) {
			AdventurousDungeons.getNetwork().sendToServer(new MessageCreateArea(dungeon, aabb, this.componentNameField.getText(), 1));
		}
	}
}
