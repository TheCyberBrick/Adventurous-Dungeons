package tcb.adventurousdungeons.client.handler;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.ILocalDungeonComponent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.common.item.ItemAreaSelection;
import tcb.adventurousdungeons.common.item.ItemComponentSelection;
import tcb.adventurousdungeons.common.item.ItemScriptEditor;
import tcb.adventurousdungeons.common.item.ItemTest;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class WorldRenderHandler {
	private WorldRenderHandler() { }

	@SubscribeEvent
	public static void onRenderWorldLast(RenderWorldLastEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(player != null && player.getHeldItemMainhand() != null) {
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.getItem() instanceof ItemTest || stack.getItem() instanceof ItemComponentSelection || stack.getItem() instanceof ItemAreaSelection || stack.getItem() instanceof ItemScriptEditor) {
				World world = Minecraft.getMinecraft().world;
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);

				for(ILocalStorage storage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
					if(storage instanceof IDungeon) {
						IDungeon dungeon = (IDungeon) storage;

						if(dungeon.getBoundingBox() == null) {
							continue;
						}

						GlStateManager.pushMatrix();
						if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
							GlStateManager.disableDepth();
						}
						GlStateManager.disableTexture2D();
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.glLineWidth(1.5F);
						GlStateManager.depthMask(false);
						GL11.glEnable(GL11.GL_LINE_SMOOTH);

						Random rnd = new Random(storage.getID().hashCode());

						float red = (0.25F + rnd.nextFloat() / 2.0F * 0.75F);
						float green = (0.25F + rnd.nextFloat() / 2.0F * 0.75F);
						float blue = (0.25F + rnd.nextFloat() * 0.75F);
						float alpha = 0.25F;

						AxisAlignedBB aabb = dungeon.getBoundingBox();

						GlStateManager.color(red, green, blue, alpha);
						//drawBoundingBox(aabb.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));

						GlStateManager.color(red / 1.5F, green / 1.5F, blue / 1.5F, 1.0F);
						drawBoundingBoxOutline(aabb.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));

						for(IDungeonComponent dungeonComponent : dungeon.getDungeonComponents()) {
							if(dungeonComponent instanceof ILocalDungeonComponent) {
								ILocalDungeonComponent localComponent = (ILocalDungeonComponent) dungeonComponent;
								if(localComponent.getBounds() != null) {
									GlStateManager.color(1, 0, 0, 1.0F);
									drawBoundingBoxOutline(localComponent.getBounds().offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));
								}
							}
						}

						Vec3d center = new Vec3d((aabb.maxX + aabb.minX) / 2.0D, (aabb.maxY + aabb.minY) / 2.0D, (aabb.maxZ + aabb.minZ) / 2.0D).addVector(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ);

						GlStateManager.pushMatrix();
						GlStateManager.translate(center.xCoord, center.yCoord, center.zCoord);

						float scale = Math.max(2.0F, (float)center.lengthVector() / 10.0F);

						GlStateManager.scale(scale, scale, scale);

						//renderTag(Minecraft.getMinecraft().fontRendererObj, storage.getID().getStringID(), 0, 0, 0, 0, Minecraft.getMinecraft().getRenderManager().playerViewY, Minecraft.getMinecraft().getRenderManager().playerViewX, Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2);

						GlStateManager.enableBlend();

						GlStateManager.popMatrix();

						GlStateManager.disableTexture2D();
						GlStateManager.color(1, 1, 1, 1);

						GlStateManager.enableDepth();
						GlStateManager.glLineWidth(2F);

						/*ILocationGuard guard = location.getGuard();
						if(guard != null) {
							GlStateManager.doPolygonOffset(-0.1F, -10.0F);
							GlStateManager.enablePolygonOffset();
							for(int xo = -8; xo <= 8; xo++) {
								for(int yo = -8; yo <= 8; yo++) {
									for(int zo = -8; zo <= 8; zo++) {
										BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition().add(xo, yo, zo);
										if(pos.getY() >= 0) {
											IBlockState state = world.getBlockState(pos);
											boolean guarded = guard.isGuarded(world, Minecraft.getMinecraft().thePlayer, pos);
											if(guarded) {
												if(state.getBlock() != Blocks.AIR) {
													GlStateManager.color(1, 0, 0, 0.25F);
													drawBoundingBox(state.getBoundingBox(world, pos).offset(pos).offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));
												} else {
													GlStateManager.color(1, 0, 0, 0.8F);
													drawBoundingBoxOutline(new AxisAlignedBB(pos).offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));
												}
											}
										}
									}
								}
							}
							GlStateManager.disablePolygonOffset();
						}*/

						GL11.glDisable(GL11.GL_LINE_SMOOTH);
						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
						GlStateManager.depthMask(true);
						GlStateManager.enableTexture2D();
						GlStateManager.enableDepth();
						GlStateManager.disableBlend();
						GlStateManager.popMatrix();
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void renderTag(FontRenderer fontRenderer, String str, float x, float y, float z, int yOffset, float playerViewY, float playerViewX, boolean thirdPerson) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float)(thirdPerson ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		int i = fontRenderer.getStringWidth(str) / 2;
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double)(-i - 1), (double)(-1 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos((double)(-i - 1), (double)(8 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos((double)(i + 1), (double)(8 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos((double)(i + 1), (double)(-1 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();

		fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, yOffset,  0xFFFFFFFF);
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public static void drawBoundingBox(AxisAlignedBB axisalignedbb) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glEnd();
	}

	@SideOnly(Side.CLIENT)
	public static void drawBoundingBoxOutline(AxisAlignedBB par1AxisAlignedBB) {
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glEnd();
	}
}
