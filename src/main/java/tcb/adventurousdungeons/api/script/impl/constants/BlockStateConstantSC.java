package tcb.adventurousdungeons.api.script.impl.constants;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.gui.GuiScriptComponent;
import tcb.adventurousdungeons.client.gui.GuiEditScript;

/**
 * The block state constant component always returns the {@link IBlockState} that was specified
 */
public class BlockStateConstantSC extends DungeonScriptComponent {
	private OutputPort<IBlockState> out;
	private IBlockState state;

	public BlockStateConstantSC(Script script) {
		super(script);
	}

	public BlockStateConstantSC(Script script, String name, IBlockState state) {
		super(script, name);
		this.state = state;
	}

	public IBlockState getBlockState() {
		return this.state;
	}

	@Override
	protected void createPorts() {
		this.out = this.out("out", IBlockState.class);		
	}

	@Override
	protected void run() {
		this.put(this.out, this.state);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);
		nbt.setString("block", this.state.getBlock().getRegistryName().toString());
		nbt.setInteger("meta", this.state.getBlock().getMetaFromState(this.state));
		return nbt;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);
		Block block = Block.getBlockFromName(nbt.getString("block"));
		if(block != null) {
			this.state = block.getStateFromMeta(nbt.getInteger("meta"));
		}
	}

	@SideOnly(Side.CLIENT)
	private static Framebuffer fbo;

	@Override
	public GuiScriptComponent getComponentGui(GuiEditScript gui) {
		return new GuiScriptComponent(gui, this) {
			private int ticks = 0;

			private Framebuffer getFbo() {
				int w = getGui().mc.displayWidth;
				int h = getGui().mc.displayHeight;
				if(fbo == null) {
					fbo = new Framebuffer(w, h, true);
				}
				if(fbo.framebufferWidth != w || fbo.framebufferHeight != h) {
					fbo.deleteFramebuffer();
					fbo = new Framebuffer(w, h, true);
				}
				return fbo;
			}

			@Override
			protected int[] getAdditionalArea() {
				return new int[]{30, 30};
			}

			@Override
			public void update() {
				super.update();

				this.ticks++;
			}

			@Override
			protected void renderAdditionalInfo(float partialTicks) {
				super.renderAdditionalInfo(partialTicks);
				if(state != null) {
					if(state.getBlock() == Blocks.AIR) {
						Minecraft.getMinecraft().fontRendererObj.drawString("Air", this.getWidth() / 2 - 6, 10, 0xFFFFFFFF);
					} else {
						BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
						TextureManager texManager = Minecraft.getMinecraft().getTextureManager();
						texManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

						getFbo().bindFramebuffer(true);
						GlStateManager.clearColor(0, 0, 0, 0);
						GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

						GlStateManager.pushMatrix();
						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.enableTexture2D();
						GlStateManager.enableRescaleNormal();
						GlStateManager.enableBlend();
						GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
						RenderHelper.enableGUIStandardItemLighting();
						GlStateManager.translate(this.getWidth() / 2, 14, 0);
						GlStateManager.rotate(-30, 1, 0, 0);
						GlStateManager.rotate(this.ticks + partialTicks, 0, 1, 0);
						GlStateManager.translate(8, 8, -8);
						GlStateManager.scale(-16, -16, -16);
						renderer.renderBlockBrightness(state, 1);
						RenderHelper.disableStandardItemLighting();
						GlStateManager.popMatrix();

						ScaledResolution res = new ScaledResolution(getGui().mc);

						GlStateManager.pushMatrix();
						GlStateManager.matrixMode(GL11.GL_MODELVIEW);
						GlStateManager.loadIdentity();
						GlStateManager.translate(0.0F, 0.0F, -2000.0F);

						Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

						GlStateManager.bindTexture(getFbo().framebufferTexture);
						GlStateManager.glBegin(GL11.GL_QUADS);
						GlStateManager.glTexCoord2f(0, 0);
						GlStateManager.glVertex3f(0, (float)res.getScaledHeight_double(), 0);
						GlStateManager.glTexCoord2f(1, 0);
						GlStateManager.glVertex3f((float)res.getScaledWidth_double(), (float)res.getScaledHeight_double(), 0);
						GlStateManager.glTexCoord2f(1, 1);
						GlStateManager.glVertex3f((float)res.getScaledWidth_double(), 0, 0);
						GlStateManager.glTexCoord2f(0, 1);
						GlStateManager.glVertex3f(0, 0, 0);
						GlStateManager.glEnd();

						GlStateManager.popMatrix();
					}
				}
			}
		};
	}
}
