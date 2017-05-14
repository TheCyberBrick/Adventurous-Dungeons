package tcb.adventurousdungeons.api.script.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

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
 * Combines multiple strings into one string, each string
 * is separated by the separator, if connected.
 */
public class StringCombinerSC extends DungeonScriptComponent {
	private int numInputs;

	private InputPort<Object> separator;
	private final List<InputPort<Object>> inputs = new ArrayList<>();
	private OutputPort<String> out;

	public StringCombinerSC(Script script) {
		super(script);
	}

	public StringCombinerSC(Script script, String name, int numInputs) {
		super(script, name);
		this.numInputs = numInputs;
	}

	public int getNumInputs() {
		return this.numInputs;
	}

	@Override
	protected void createPorts() {
		this.inputs.add(this.in("in_0", Object.class, true));
		for(int i = 1; i < this.numInputs; i++) {
			this.inputs.add(this.in("in_" + i, Object.class, false));
		}
		this.separator = this.in("separator", Object.class, false);
		this.out = this.out("out", String.class);
	}

	@Override
	protected void run() throws ScriptException {
		String separator = null;
		if(this.separator.isConnected()) {
			separator = this.get(this.separator).toString();
		}
		List<String> strings = new ArrayList<>();
		for(InputPort<Object> input : this.inputs) {
			String val = this.get(input).toString();
			if(val != null) {
				strings.add(val);
			}
		}
		StringBuilder str = new StringBuilder();
		int len = strings.size();
		int i = 0;
		for(String value : strings) {
			str.append(value);
			if(separator != null && i < len - 1) {
				str.append(separator);
			}
			i++;
		}
		this.put(this.out, str.toString());
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setInteger("numInputs", this.numInputs);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.numInputs = nbt.getInteger("numInputs");
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<StringCombinerSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable StringCombinerSC component, float x, float y) {
			return new FactoryGui(parent, StringCombinerSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<StringCombinerSC> {
		public FactoryGui(GuiScreen parent, Class<StringCombinerSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable StringCombinerSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			this.addCreateSaveButton(2, 77, 80, 20);

			GuiTextField textField = new GuiTextField(1, this.fontRendererObj, 2, 55, 80, 20);
			this.addTextField(textField);

			if(this.getInputComponent() != null) {
				textField.setText("" + this.getInputComponent().getNumInputs());
			}

			this.addNameField();
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks) {
			super.drawScreen(mouseX, mouseY, partialTicks);

			this.fontRendererObj.drawString("Number of Inputs: ", 2, 44, 0xFFFFFFFF);
		}

		@Override
		protected StringCombinerSC create(Script script, String name) {
			String inNum = this.getTextField(1).getText();
			try {
				int num = Integer.parseInt(inNum);
				if(num <= 1) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_nr_range"));
					return null;
				}
				return new StringCombinerSC(script, name, num);
			} catch(NumberFormatException ex) { 
				this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_int"));
			}
			return null;
		}
	}
}
