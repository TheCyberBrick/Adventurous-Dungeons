package tcb.adventurousdungeons.api.script.impl;

import java.util.ArrayList;
import java.util.List;
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
import tcb.adventurousdungeons.api.script.InputPort;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;
import tcb.adventurousdungeons.api.script.gui.GuiCreateScriptComponent;

/**
 * This component redirects one input
 * into a multiple outputs
 */
public class DuplicatorSC extends DungeonScriptComponent {
	private int numOutputs;

	private final List<OutputPort<Object>> outputs = new ArrayList<>();
	private InputPort<Object> input;

	public DuplicatorSC(Script script) {
		super(script);
	}

	public DuplicatorSC(Script script, String name, int numOutputs) {
		super(script, name);
		this.numOutputs = numOutputs;
	}

	public int getNumOutputs() {
		return this.numOutputs;
	}

	@Override
	protected void createPorts() {
		for(int i = 0; i < this.numOutputs; i++) {
			this.outputs.add(this.out("out_" + i, Object.class));
		}
		this.input = this.in("in", Object.class, true);
	}

	@Override
	protected void run() throws ScriptException {
		Object value = this.get(this.input);
		for(OutputPort<Object> output : this.outputs) {
			this.put(output, value);
		}
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setInteger("numOutputs", this.numOutputs);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.numOutputs = nbt.getInteger("numOutputs");
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<DuplicatorSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable DuplicatorSC component, float x, float y) {
			return new FactoryGui(parent, DuplicatorSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<DuplicatorSC> {
		public FactoryGui(GuiScreen parent, Class<DuplicatorSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable DuplicatorSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			this.addCreateSaveButton(2, 77, 80, 20);
			
			GuiTextField textField = new GuiTextField(1, this.fontRendererObj, 2, 55, 80, 20);
			this.addTextField(textField);

			if(this.getInputComponent() != null) {
				textField.setText("" + this.getInputComponent().getNumOutputs());
			}

			this.addNameField();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);

			this.fontRendererObj.drawString("Number of Outputs: ", 2, 44, 0xFFFFFFFF);
		}

		@Override
		protected DuplicatorSC create(Script script, String name) {
			String inNum = this.getTextField(1).getText();
			try {
				int num = Integer.parseInt(inNum);
				if(num <= 1) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_nr_range"));
					return null;
				}
				return new DuplicatorSC(script, name, num);
			} catch(NumberFormatException ex) { 
				this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_int"));
			}
			return null;
		}
	}
}
