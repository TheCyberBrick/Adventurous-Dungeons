package tcb.adventurousdungeons.api.script.impl.constants;

import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
 * The direction constant component always returns the EnumFacing that was specified
 */
public class DirectionConstantSC extends DungeonScriptComponent {
	private OutputPort<EnumFacing> out;
	private EnumFacing val;

	public DirectionConstantSC(Script script) {
		super(script);
	}

	public DirectionConstantSC(Script script, String name, EnumFacing val) {
		super(script, name);
		this.val = val;
	}

	public EnumFacing getDirection() {
		return this.val;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", EnumFacing.class);		
	}

	@Override
	protected void run() {
		this.put(this.out, this.val);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setString("dir", this.val.getName2());
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		this.val = EnumFacing.byName(nbt.getString("dir"));
	}

	@Override
	public GuiScriptComponent getComponentGui(GuiEditScript gui) {
		return new GuiScriptComponent(gui, this) {
			@Override
			protected int[] getAdditionalArea() {
				return new int[]{this.font.getStringWidth("" + val.getName2()) + 3, 8};
			}

			@Override
			protected void renderAdditionalInfo(float partialTicks) {
				super.renderAdditionalInfo(partialTicks);

				this.font.drawString("" + val.getName2(), 2, 0, 0xFFFFFFFF);
			}
		};
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<DirectionConstantSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable DirectionConstantSC component, float x, float y) {
			return new FactoryGui(parent, DirectionConstantSC.class, add, script, component, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static class FactoryGui extends GuiCreateScriptComponent<DirectionConstantSC> {
		public FactoryGui(GuiScreen parent, Class<DirectionConstantSC> componentType, Consumer<IScriptComponent> add, Script script, @Nullable DirectionConstantSC component, float x, float y) {
			super(parent, componentType, add, script, component, x, y);
		}

		@Override
		public void initGui() {
			int id = 1;
			int xOff = 0;
			for(EnumFacing dir : EnumFacing.VALUES) {
				GuiButton btn = new GuiButton(id, 2 + xOff, 55, 40, 20, dir.getName2());
				if(id == 1) {
					btn.enabled = false;
				}
				this.buttonList.add(btn);
				id++;
				xOff += 40;
			}

			if(this.getInputComponent() != null) {
				EnumFacing dir = this.getInputComponent().getDirection();
				for(int i = 0; i < EnumFacing.VALUES.length; i++) {
					this.buttonList.get(i).enabled = true;
				}
				for(int i = 0; i < EnumFacing.VALUES.length; i++) {
					if(dir == EnumFacing.VALUES[i]) {
						this.buttonList.get(i).enabled = false;
					}
				}
			}

			this.buttonList.add(new GuiButton(0, 2, 77, 80, 20, "Create"));

			this.addNameField();
		}

		@Override
		protected void actionPerformed(GuiButton button) throws IOException {
			super.actionPerformed(button);

			if(button.id >= 1 && button.id < EnumFacing.VALUES.length + 1) {
				int index = button.id - 1;
				for(int i = 0; i < EnumFacing.VALUES.length; i++) {
					this.buttonList.get(i).enabled = true;
				}
				this.buttonList.get(index).enabled = false;
			}
		}

		@Override
		protected DirectionConstantSC create(Script script, String name) {
			EnumFacing dir = EnumFacing.NORTH;
			for(int i = 0; i < EnumFacing.VALUES.length; i++) {
				if(!this.buttonList.get(i).enabled) {
					dir = EnumFacing.VALUES[i];
				}
			}
			return new DirectionConstantSC(script, name, dir);
		}
	}
}
