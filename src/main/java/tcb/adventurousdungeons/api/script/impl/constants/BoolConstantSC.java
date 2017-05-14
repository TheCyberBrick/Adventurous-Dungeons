package tcb.adventurousdungeons.api.script.impl.constants;

import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
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
 * The boolean constant component always returns the boolean that was specified
 */
public class BoolConstantSC extends DungeonScriptComponent {
	private OutputPort<Boolean> out;
	private boolean val;

	public BoolConstantSC(Script script) {
		super(script);
	}

	public BoolConstantSC(Script script, String name, boolean val) {
		super(script, name);
		this.val = val;
	}

	public boolean getBoolean() {
		return this.val;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", Boolean.class);		
	}

	@Override
	protected void run() {
		this.put(this.out, this.val);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setBoolean("val", this.val);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.val = nbt.getBoolean("val");
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
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<BoolConstantSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable BoolConstantSC component, float x, float y) {
			return new FactoryGui(parent, BoolConstantSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<BoolConstantSC> {
		public FactoryGui(GuiScreen parent, Class<BoolConstantSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable BoolConstantSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			GuiButton buttonTrue = new GuiButton(1, 2, 55, 40, 20, "True");
			buttonTrue.enabled = false;
			this.buttonList.add(buttonTrue);
			GuiButton buttonFalse = new GuiButton(2, 42, 55, 40, 20, "False");
			buttonFalse.enabled = true;
			this.buttonList.add(buttonFalse);
			
			this.addCreateSaveButton(2, 77, 80, 20);

			if(this.getInputComponent() != null) {
				if(this.getInputComponent().getBoolean()) {
					buttonTrue.enabled = false;
					buttonFalse.enabled = true;
				} else {
					buttonTrue.enabled = true;
					buttonFalse.enabled = false;
				}
			}

			this.addNameField();
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			super.actionPerformed(button);

			if(button.id == 1) {
				this.buttonList.get(0).enabled = false;
				this.buttonList.get(1).enabled = true;
			} else if(button.id == 2) {
				this.buttonList.get(0).enabled = true;
				this.buttonList.get(1).enabled = false;
			}
		}

		@Override
		protected BoolConstantSC create(Script script, String name) {
			return new BoolConstantSC(script, name, !this.buttonList.get(0).enabled);
		}
	}
}
