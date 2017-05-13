package tcb.adventurousdungeons.api.script.impl.constants;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.gui.GuiCreateScriptComponent;
import tcb.adventurousdungeons.api.script.gui.GuiScriptComponent;
import tcb.adventurousdungeons.client.gui.GuiEditScript;

/**
 * The int constant component always returns the int that was specified
 */
public class IntConstantSC extends DungeonScriptComponent {
	private OutputPort<Integer> out;
	private int val;

	public IntConstantSC(Script script) {
		super(script);
	}

	public IntConstantSC(Script script, String name, int val) {
		super(script, name);
		this.val = val;
	}

	public int getInt() {
		return this.val;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", Integer.class);		
	}

	@Override
	protected void run() {
		this.put(this.out, this.val);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setInteger("val", this.val);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.val = nbt.getInteger("val");
	}

	@Override
	public GuiScriptComponent getComponentGui(GuiEditScript gui) {
		return new GuiScriptComponent(gui, this) {
			@Override
			protected int[] getAdditionalArea() {
				return new int[]{this.font.getStringWidth("" + val) + 3, 8};
			}

			@Override
			protected void renderAdditionalInfo(float partialTicks) {
				super.renderAdditionalInfo(partialTicks);

				this.font.drawString("" + val, 2, 0, 0xFFFFFFFF);
			}
		};
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<IntConstantSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable IntConstantSC component, float x, float y) {
			return new FactoryGui(parent, IntConstantSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<IntConstantSC> {
		public FactoryGui(GuiScreen parent, Class<IntConstantSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable IntConstantSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			this.buttonList.add(new GuiButton(0, 2, 77, 80, 20, "Create"));

			GuiTextField textField = new GuiTextField(1, this.fontRendererObj, 2, 55, 80, 20);
			this.addTextField(textField);

			if(this.getInputComponent() != null) {
				textField.setText("" + this.getInputComponent().getInt());
			}

			this.addNameField();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);

			this.fontRendererObj.drawString("Integer: ", 2, 44, 0xFFFFFFFF);
		}

		@Override
		protected IntConstantSC create(Script script, String name) {
			String inNum = this.getTextField(1).getText();
			try {
				return new IntConstantSC(script, name, Integer.parseInt(inNum));
			} catch(NumberFormatException ex) { 
				this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_int"));
			}
			return null;
		}
	}
}
