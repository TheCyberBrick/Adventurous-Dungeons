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
 * This component redirects multiple inputs
 * into a single output. If the value is requested
 * from another component the value of the port
 * specified by defaultInput is chosen.
 */
public class MergerSC extends DungeonScriptComponent {
	private int numInputs, defaultInput;

	private final List<InputPort<Object>> inputs = new ArrayList<>();
	private OutputPort<Object> output;

	public MergerSC(Script script) {
		super(script);
	}

	public MergerSC(Script script, String name, int numInputs, int defaultInput) {
		super(script, name);
		this.numInputs = numInputs;
		this.defaultInput = defaultInput;
	}

	public int getNumInputs() {
		return this.numInputs;
	}

	public int getDefaultInput() {
		return this.defaultInput;
	}

	@Override
	protected void createPorts() {
		InputPort<Object> defaultInputPort = null;
		for(int i = 0; i < this.numInputs; i++) {
			InputPort<Object> input = this.in("in_" + i, Object.class, false);
			if(i == this.defaultInput) {
				defaultInputPort = input;
			} else {
				this.inputs.add(input);
			}
		}
		if(defaultInputPort != null) {
			this.inputs.add(0, defaultInputPort);
		}
		this.output = this.out("out", Object.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void run() throws ScriptException {
		if(this.getCallerPort() != null && this.getCallerPort().isInput()) {
			this.put(this.output, this.get((InputPort<Object>) this.getCallerPort()));
			return;
		}
		for(InputPort<Object> input : this.inputs) {
			if(input.isConnected()) {
				this.put(this.output, this.get(input));
				return;
			}
		}
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setInteger("numInputs", this.numInputs);
		nbt.setInteger("defaultInput", this.defaultInput);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.numInputs = nbt.getInteger("numInputs");
		this.defaultInput = nbt.getInteger("defaultInput");
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<MergerSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable MergerSC component, float x, float y) {
			return new FactoryGui(parent, MergerSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<MergerSC> {
		public FactoryGui(GuiScreen parent, Class<MergerSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable MergerSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			this.addCreateSaveButton(2, 108, 80, 20);
			
			GuiTextField textFieldInputs = new GuiTextField(1, this.fontRendererObj, 2, 55, 80, 20);
			this.addTextField(textFieldInputs);
			GuiTextField textFieldDefaultInput = new GuiTextField(2, this.fontRendererObj, 2, 86, 80, 20);
			this.addTextField(textFieldDefaultInput);

			if(this.getInputComponent() != null) {
				textFieldInputs.setText("" + this.getInputComponent().getNumInputs());
				textFieldDefaultInput.setText("" + this.getInputComponent().getDefaultInput());
			}

			this.addNameField();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);

			this.fontRendererObj.drawString("Number of Inputs: ", 2, 44, 0xFFFFFFFF);
			this.fontRendererObj.drawString("Default Input: ", 2, 77, 0xFFFFFFFF);
		}

		@Override
		protected MergerSC create(Script script, String name) {
			String inNum = this.getTextField(1).getText();
			String inDefault = this.getTextField(2).getText();
			try {
				int num = Integer.parseInt(inNum);
				if(num <= 1) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_nr_range"));
					return null;
				}
				int numDefault = Integer.parseInt(inDefault);
				if(numDefault < 0 || numDefault >= num) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_nr_range"));
					return null;
				}
				return new MergerSC(script, name, num, numDefault);
			} catch(NumberFormatException ex) { 
				this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_int"));
			}
			return null;
		}
	}
}
