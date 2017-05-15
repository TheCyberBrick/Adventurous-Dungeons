package tcb.adventurousdungeons.common.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.client.handler.WorldRenderHandler;
import tcb.adventurousdungeons.util.CatmullRomSpline;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public class ItemSpline extends Item {
	public ItemSpline() {
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "spline_item"));
		this.setUnlocalizedName(ModInfo.ID + ".spline_item");
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInfo.ID + ":spline_item", "inventory"));
		this.setCreativeTab(CreativeTabs.MISC);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if(!world.isRemote) {
			List<Vec3d> points = this.getSelectedPoints(stack);
			if(player.isSneaking()) {
				if(!points.isEmpty()) {
					points.remove(points.size() - 1);
					this.setSelectedPoints(stack, points);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
				}
			} else {
				RayTraceResult target = this.getTargetPoint(stack, player, 1);
				if(target.typeOfHit == RayTraceResult.Type.MISS) {
					points.add(target.hitVec);
					this.setSelectedPoints(stack, points);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
				}
			}
		} else {
			/*if(points[0] != null && points[1] != null && player.isSneaking()) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiCreateArea());
			}*/
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
			List<Vec3d> points = this.getSelectedPoints(stack);
			RayTraceResult target = this.getTargetPoint(stack, player, 1);
			points.add(target.hitVec);
			this.setSelectedPoints(stack, points);
		}
		return EnumActionResult.SUCCESS;
	}

	@Nonnull
	public List<Vec3d> getSelectedPoints(ItemStack stack) {
		List<Vec3d> pts = new ArrayList<>();
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("pts", Constants.NBT.TAG_LIST)) {
			NBTTagList lst = stack.getTagCompound().getTagList("pts", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < lst.tagCount(); i++) {
				pts.add(MoreNBTUtils.readVec(lst.getCompoundTagAt(i)));
			}
		}
		return pts;
	}

	public void setSelectedPoints(ItemStack stack, @Nullable List<Vec3d> pts) {
		if(pts == null || pts.isEmpty()) {
			if(stack.hasTagCompound()) {
				stack.getTagCompound().removeTag("pts");
			}
		} else {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null) {
				stack.setTagCompound(nbt = new NBTTagCompound());
			}
			NBTTagList lst = new NBTTagList();
			for(Vec3d pt : pts) {
				lst.appendTag(MoreNBTUtils.writeVec(pt));
			}
			nbt.setTag("pts", lst);
		}
	}

	public RayTraceResult getTargetPoint(ItemStack stack, EntityPlayer player, float partialTicks) {
		Vec3d look = player.getLook(partialTicks);
		Vec3d eyes = player.getPositionEyes(partialTicks);
		RayTraceResult ray = player.world.rayTraceBlocks(eyes, eyes.add(look.scale(3.5D)), false, true, false);
		if(ray != null && ray.typeOfHit != RayTraceResult.Type.MISS) {
			if(player.isSneaking()) {
				double sx = Math.round(ray.hitVec.xCoord * 8.0D) / 8.0D;
				double sy = Math.round(ray.hitVec.yCoord * 8.0D) / 8.0D;
				double sz = Math.round(ray.hitVec.zCoord * 8.0D) / 8.0D;
				return new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(sx, sy, sz), ray.sideHit, ray.getBlockPos());
			}
			return ray;
		}
		Vec3d target = eyes.add(look.scale(3.5D));
		if(player.isSneaking()) {
			double sx = Math.round(target.xCoord * 8.0D) / 8.0D;
			double sy = Math.round(target.yCoord * 8.0D) / 8.0D;
			double sz = Math.round(target.zCoord * 8.0D) / 8.0D;
			target = new Vec3d(sx, sy, sz);
		}
		return new RayTraceResult(RayTraceResult.Type.MISS, target, EnumFacing.getFacingFromVector((float)look.xCoord, (float)look.yCoord, (float)look.zCoord), null);
	}

	@SideOnly(Side.CLIENT)
	public static final class SelectionRenderHandler {
		private SelectionRenderHandler() { }

		@SubscribeEvent
		public static void onRenderWorldLast(RenderWorldLastEvent event) {
			EntityPlayer player = Minecraft.getMinecraft().player;

			if(player != null) {
				ItemStack stack = null;
				if(player.getHeldItem(EnumHand.OFF_HAND) != null && player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemSpline) {
					stack = player.getHeldItem(EnumHand.OFF_HAND);
				}
				if(player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSpline) {
					stack = player.getHeldItem(EnumHand.MAIN_HAND);
				}

				if(stack != null) {
					ItemSpline item = (ItemSpline)stack.getItem();
					List<Vec3d> points = item.getSelectedPoints(stack);

					RayTraceResult targetPoint = item.getTargetPoint(stack, player, event.getPartialTicks());

					GlStateManager.pushMatrix();

					double rx = Minecraft.getMinecraft().getRenderManager().viewerPosX;
					double ry = Minecraft.getMinecraft().getRenderManager().viewerPosY;
					double rz = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

					GlStateManager.disableTexture2D();
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.glLineWidth(2F);
					GlStateManager.depthMask(false);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);

					if(player.isSneaking()) {
						GlStateManager.color(1, 1, 1, 0.25F);
						for(int xo = -12; xo <= 12; xo++) {
							for(int yo = -12; yo <= 12; yo++) {
								for(int zo = -12; zo <= 12; zo++) {
									BlockPos pos = new BlockPos(player.posX + xo, player.posY + yo, player.posZ + zo);
									IBlockState state = player.world.getBlockState(pos);
									if(state.getBlock() != Blocks.AIR) {
										AxisAlignedBB aabb = state.getBoundingBox(player.world, pos);
										WorldRenderHandler.drawBoundingBoxOutline(aabb.expand(0.002D, 0.002D, 0.002D).offset(pos.getX() - rx, pos.getY() - ry, pos.getZ() - rz));
									}
								}
							}
						}
						GlStateManager.color(1, 1, 1, 1);
					}

					if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
						GlStateManager.disableDepth();
					}

					if(targetPoint != null) {
						WorldRenderHandler.drawBoundingBoxOutline(new AxisAlignedBB(targetPoint.hitVec, targetPoint.hitVec).expand(0.05D, 0.05D, 0.05D).offset(-rx, -ry, -rz));

						if(targetPoint.typeOfHit == RayTraceResult.Type.MISS) {
							GlStateManager.color(0.7F, 0.7F, 0.7F, 1);
							for(EnumFacing dir : EnumFacing.values()) {
								RayTraceResult rayHelp = player.world.rayTraceBlocks(targetPoint.hitVec, targetPoint.hitVec.addVector(dir.getFrontOffsetX() * 16, dir.getFrontOffsetY() * 16, dir.getFrontOffsetZ() * 16), false, true, false);
								if(rayHelp != null) {
									WorldRenderHandler.drawBoundingBoxOutline(new AxisAlignedBB(rayHelp.hitVec, rayHelp.hitVec).expand(0.03D, 0.03D, 0.03D).offset(-rx, -ry, -rz));
									GL11.glBegin(GL11.GL_LINES);
									GL11.glVertex3d(targetPoint.hitVec.xCoord - rx, targetPoint.hitVec.yCoord - ry, targetPoint.hitVec.zCoord - rz);
									GL11.glVertex3d(rayHelp.hitVec.xCoord - rx, rayHelp.hitVec.yCoord - ry, rayHelp.hitVec.zCoord - rz);
									GL11.glEnd();
								}
							}
							GlStateManager.color(1, 1, 1, 1);
						}
					} 


					if(points.size() >= 4) {
						CatmullRomSpline spline = new CatmullRomSpline(points.toArray(new Vec3d[0]));

						int subdivs = points.size() * 20;
						float subdivDelta = 1.0F / (subdivs - 1);

						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.glBegin(GL11.GL_LINE_STRIP);

						for(int i = 0; i < subdivs; i++) {
							Vec3d pt = spline.interpolate(subdivDelta * i);
							GL11.glVertex3d(pt.xCoord - rx, pt.yCoord - ry, pt.zCoord - rz);
						}

						GlStateManager.glEnd();
					}

					GlStateManager.color(0, 0, 0, 1);
					for(Vec3d pt : points) {
						AxisAlignedBB aabb = new AxisAlignedBB(pt, pt).expand(0.025D, 0.025D, 0.025D);
						WorldRenderHandler.drawBoundingBoxOutline(aabb.offset(-rx, -ry, -rz));
					}

					GlStateManager.color(1, 1, 1, 1);

					/*if(points[0] != null) {
						AxisAlignedBB aabb = new AxisAlignedBB(points[0], points[1]);

						if(targetPoint == null && aabb.getAverageEdgeLength() <= 0.05F) {
							aabb = aabb.expand(0.01D, 0.01D, 0.01D);
						}

						GlStateManager.color(1, 1, 1, 1);
						WorldRenderHandler.drawBoundingBoxOutline(aabb.offset(-rx, -ry, -rz));

						GlStateManager.color(0.5F, 0.5F, 0.5F, 0.25F);
						GlStateManager.enablePolygonOffset();
						GlStateManager.doPolygonOffset(-0.1F, -10.0F);

						WorldRenderHandler.drawBoundingBox(aabb.offset(-rx, -ry, -rz));

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
