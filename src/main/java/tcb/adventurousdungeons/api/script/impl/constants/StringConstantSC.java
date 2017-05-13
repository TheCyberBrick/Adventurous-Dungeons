package tcb.adventurousdungeons.api.script.impl.constants;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.gui.GuiCreateScriptComponent;
import tcb.adventurousdungeons.api.script.gui.GuiScriptComponent;
import tcb.adventurousdungeons.client.gui.GuiEditScript;

/**
 * The string constant component always returns the string that was specified
 */
public class StringConstantSC extends DungeonScriptComponent {
	private OutputPort<String> out;
	private String str;

	public StringConstantSC(Script script) {
		super(script);
	}

	public StringConstantSC(Script script, String name, String str) {
		super(script, name);
		this.str = str;
	}

	public String getString() {
		return this.str;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", String.class);		
	}

	@Override
	protected void run() {
		this.put(this.out, this.str);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setString("str", this.str);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.str = nbt.getString("str");
	}

	@Override
	public GuiScriptComponent getComponentGui(GuiEditScript gui) {
		return new GuiScriptComponent(gui, this) {
			@Override
			protected int[] getAdditionalArea() {
				return new int[]{this.font.getStringWidth(str) + 3, 8};
			}

			@Override
			protected void renderAdditionalInfo(float partialTicks) {
				super.renderAdditionalInfo(partialTicks);

				this.font.drawString(str, 2, 0, 0xFFFFFFFF);
			}
		};
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<StringConstantSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable StringConstantSC component, float x, float y) {
			return new FactoryGui(parent, StringConstantSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<StringConstantSC> {
		public FactoryGui(GuiScreen parent, Class<StringConstantSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable StringConstantSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			this.buttonList.add(new GuiButton(0, 2, 77, 80, 20, "Create"));

			this.addTextField(new GuiTextField(1, this.fontRendererObj, 2, 55, 80, 20));

			this.addNameField();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);

			this.fontRendererObj.drawString("String: ", 2, 44, 0xFFFFFFFF);
		}

		@Override
		protected StringConstantSC create(Script script, String name) {
			return new StringConstantSC(script, name, this.getTextField(1).getText());
		}
	}
}
