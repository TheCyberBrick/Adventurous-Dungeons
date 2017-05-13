package tcb.adventurousdungeons.api.script.impl.constants;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.gui.GuiScriptComponent;
import tcb.adventurousdungeons.client.gui.GuiEditScript;

/**
 * The ItemStack constant component always returns the {@link ItemStack} that was specified
 */
public class ItemStackConstantSC extends DungeonScriptComponent {
	private OutputPort<ItemStack> out;
	private ItemStack stack;

	public ItemStackConstantSC(Script script) {
		super(script);
	}

	public ItemStackConstantSC(Script script, String name, ItemStack stack) {
		super(script, name);
		this.stack = stack;
	}

	public ItemStack getItemStack() {
		return this.stack;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", ItemStack.class);		
	}

	@Override
	protected void run() {
		this.put(this.out, this.stack.copy());
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		if(this.stack != ItemStack.EMPTY) {
			nbt.setTag("item", this.stack.serializeNBT());
		}
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		if(nbt.hasKey("item", Constants.NBT.TAG_COMPOUND)) {
			this.stack = new ItemStack(nbt.getCompoundTag("item"));
		} else {
			this.stack = ItemStack.EMPTY;
		}
	}

	@Override
	public GuiScriptComponent getComponentGui(GuiEditScript gui) {
		return new GuiScriptComponent(gui, this) {
			@Override
			protected int[] getAdditionalArea() {
				return new int[]{18, 18};
			}

			@Override
			protected void renderAdditionalInfo(float partialTicks) {
				super.renderAdditionalInfo(partialTicks);
				if(stack != null) {
					if(stack != ItemStack.EMPTY) {
						RenderHelper.enableGUIStandardItemLighting();

						GlStateManager.pushMatrix();
						GlStateManager.translate(this.getWidth() / 2 - 7, 0, 0);
						Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
						Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRendererObj, stack, 0, 0, null);
						GlStateManager.popMatrix();

						GlStateManager.disableLighting();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.color(1, 1, 1, 1);
					} else {
						Minecraft.getMinecraft().fontRendererObj.drawString("Empty", this.getWidth() / 2 - 13, 4, 0xFFFFFFFF);
					}
				}
			}
		};
	}
}
