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
 * This component combines multiple inputs
 * into a list
 */
public class IterableSC extends DungeonScriptComponent {
	private int numInputs;

	private final List<InputPort<Object>> inputs = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	private OutputPort<Iterable> output;

	public IterableSC(Script script) {
		super(script);
	}

	public IterableSC(Script script, String name, int numInputs) {
		super(script, name);
		this.numInputs = numInputs;
	}

	public int getNumInputs() {
		return this.numInputs;
	}

	@Override
	protected void createPorts() {
		for(int i = 0; i < this.numInputs; i++) {
			InputPort<Object> input = this.in("in_" + i, Object.class, false);
			this.inputs.add(input);
		}
		this.output = this.out("out", Iterable.class);
	}

	@Override
	protected void run() throws ScriptException {
		List<Object> lst = new ArrayList<>();
		for(InputPort<Object> input : this.inputs) {
			if(input.isConnected()) {
				lst.add(this.get(input));
			}
		}
		this.put(this.output, lst);
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
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<IterableSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable IterableSC component, float x, float y) {
			return new FactoryGui(parent, IterableSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<IterableSC> {
		public FactoryGui(GuiScreen parent, Class<IterableSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable IterableSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			this.buttonList.add(new GuiButton(0, 2, 77, 80, 20, "Create"));

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
		protected IterableSC create(Script script, String name) {
			String inNum = this.getTextField(1).getText();
			try {
				int num = Integer.parseInt(inNum);
				if(num < 1) {
					this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_nr_range"));
					return null;
				}
				return new IterableSC(script, name, num);
			} catch(NumberFormatException ex) { 
				this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(ModInfo.ID + ".gui.invalid_int"));
			}
			return null;
		}
	}
}
