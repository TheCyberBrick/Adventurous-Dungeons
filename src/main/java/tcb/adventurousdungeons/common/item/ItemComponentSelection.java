package tcb.adventurousdungeons.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.ILocalDungeonComponent;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.StorageID;
import tcb.adventurousdungeons.client.handler.WorldRenderHandler;
import tcb.adventurousdungeons.common.network.clientbound.MessageEditDungeonComponent;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class ItemComponentSelection extends Item {
	public ItemComponentSelection() {
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "component_selection_item"));
		this.setUnlocalizedName(ModInfo.ID + ".component_selection_item");
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInfo.ID + ":component_selection_item", "inventory"));
		this.setCreativeTab(CreativeTabs.MISC);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
			if(player.isSneaking()) {
				ILocalDungeonComponent selected = this.getSelectedComponent(player.world, stack);
				if(selected != null) {
					AdventurousDungeons.getNetwork().sendTo(new MessageEditDungeonComponent(selected), (EntityPlayerMP)player);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
				}
			} else {
				Pair<ILocalDungeonComponent, RayTraceResult> target = this.getTargetComponent(stack, player, 1);
				this.setSelectedComponent(stack, target != null ? target.getKey() : null);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote) {
			ItemStack stack = player.getHeldItem(hand);
			Pair<ILocalDungeonComponent, RayTraceResult> target = this.getTargetComponent(stack, player, 1);
			this.setSelectedComponent(stack, target != null ? target.getKey() : null);
		}
		return EnumActionResult.SUCCESS;
	}

	public ILocalDungeonComponent getSelectedComponent(World world, ItemStack stack) {
		if(stack.hasTagCompound()) {
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt.hasKey("dungeonID") && nbt.hasKey("componentID")) {
				StorageID dungeonID = StorageID.readFromNBT(nbt.getCompoundTag("dungeonID"));
				StorageID componentID = StorageID.readFromNBT(nbt.getCompoundTag("componentID"));
				IWorldStorage worldStorage = WorldStorageImpl.getCapability(world);
				ILocalStorage localStorage = worldStorage.getLocalStorageHandler().getLocalStorage(dungeonID);
				if(localStorage instanceof IDungeon) {
					IDungeon dungeon = (IDungeon) localStorage;
					IDungeonComponent dungeonComponent = dungeon.getDungeonComponent(componentID);
					if(dungeonComponent instanceof ILocalDungeonComponent) {
						return (ILocalDungeonComponent) dungeonComponent;
					}
				}
			}
		}
		return null;
	}

	public void setSelectedComponent(ItemStack stack, @Nullable ILocalDungeonComponent component) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null) {
			stack.setTagCompound(nbt = new NBTTagCompound());
		}
		if(component == null) {
			nbt.removeTag("dungeonID");
			nbt.removeTag("componentID");
		} else {
			nbt.setTag("dungeonID", component.getDungeon().getID().writeToNBT(new NBTTagCompound()));
			nbt.setTag("componentID", component.getID().writeToNBT(new NBTTagCompound()));
		}
	}

	public Pair<ILocalDungeonComponent, RayTraceResult> getTargetComponent(ItemStack stack, EntityPlayer player, float partialTicks) {
		Vec3d look = player.getLook(partialTicks);
		Vec3d eyes = player.getPositionEyes(partialTicks);

		List<Pair<ILocalDungeonComponent, RayTraceResult>> intercepts = new ArrayList<>();

		IWorldStorage worldStorage = WorldStorageImpl.getCapability(player.world);
		Collection<ILocalStorage> localStorages = worldStorage.getLocalStorageHandler().getLoadedStorages();
		for(ILocalStorage localStorage : localStorages) {
			if(localStorage instanceof IDungeon) {
				IDungeon dungeon = (IDungeon) localStorage;
				for(IDungeonComponent dungeonComponent : dungeon.getDungeonComponents()) {
					if(dungeonComponent instanceof ILocalDungeonComponent) {
						ILocalDungeonComponent localComponent = (ILocalDungeonComponent) dungeonComponent;
						AxisAlignedBB aabb = localComponent.getBounds();
						if(aabb != null) {
							RayTraceResult intercept = aabb.calculateIntercept(eyes, eyes.add(look.scale(aabb.isVecInside(eyes) ? 128 : 8)));
							if(intercept != null && intercept.typeOfHit != RayTraceResult.Type.MISS) {
								intercepts.add(Pair.of(localComponent, intercept));
							}
						}
					}
				}
			}
		}

		Collections.sort(intercepts, (i1, i2) -> Double.compare(i1.getValue().hitVec.distanceTo(eyes), i2.getValue().hitVec.distanceTo(eyes)));

		//Take block collisions into consideration
		if(!intercepts.isEmpty()) {
			RayTraceResult blockRayTrace = player.world.rayTraceBlocks(eyes, eyes.add(look.scale(128)), false);
			if(blockRayTrace != null && blockRayTrace.typeOfHit != RayTraceResult.Type.MISS && blockRayTrace.hitVec.distanceTo(eyes) < intercepts.get(0).getValue().hitVec.distanceTo(eyes)) {
				intercepts.clear();
			}
		}

		return intercepts.isEmpty() ? null : intercepts.get(0);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		ILocalDungeonComponent component = this.getSelectedComponent(playerIn.world, stack);
		tooltip.add("ID: " + (component != null ? component.getID().getStringID() : "-"));
		tooltip.add("Name: " + (component != null ? component.getName() : "-"));
		tooltip.add("Bounds: " + (component != null ? component.getBounds().toString() : "-"));
	}

	@SideOnly(Side.CLIENT)
	public static final class SelectionRenderHandler {
		private SelectionRenderHandler() { }

		@SubscribeEvent
		public static void onRenderOverlay(RenderGameOverlayEvent event) {
			if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
				EntityPlayer player = Minecraft.getMinecraft().player;
				ItemStack stack = null;
				if(player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemComponentSelection) {
					stack = player.getHeldItem(EnumHand.MAIN_HAND);
				}
				if(stack != null) {
					FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

					ItemComponentSelection item = (ItemComponentSelection)stack.getItem();
					Pair<ILocalDungeonComponent, RayTraceResult> target = item.getTargetComponent(stack, player, event.getPartialTicks());
					ILocalDungeonComponent selected = item.getSelectedComponent(player.world, stack);

					int yOff = 0;

					if(selected != null) {
						int w = Math.max(font.getStringWidth("ID: " + selected.getID().getStringID()), font.getStringWidth("Name: " + selected.getName()));
						Gui.drawRect(0, 0, w + 2, 32, 0x90303030);
						font.drawString("Selected:", 2, 2 + yOff, 0xFFFFFFFF);
						yOff += 10;
						font.drawString("ID: " + selected.getID().getStringID(), 2, 2 + yOff, 0xFFFFFFFF);
						yOff += 10;
						font.drawString("Name: " + selected.getName(), 2, 2 + yOff, 0xFFFFFFFF);
						yOff += 14;
					}

					if(target != null) {
						int w = Math.max(font.getStringWidth("ID: " + target.getKey().getID().getStringID()), font.getStringWidth("Name: " + target.getKey().getName()));
						Gui.drawRect(0, yOff, w + 2, yOff + 32, 0x90303030);
						font.drawString("Looking at:", 2, 2 + yOff, 0xFFFFFFFF);
						yOff += 10;
						font.drawString("ID: " + target.getKey().getID().getStringID(), 2, 2 + yOff, 0xFFFFFFFF);
						yOff += 10;
						font.drawString("Name: " + target.getKey().getName(), 2, 2 + yOff, 0xFFFFFFFF);
					}
				}
			}
		}

		@SubscribeEvent
		public static void onRenderWorldLast(RenderWorldLastEvent event) {
			EntityPlayer player = Minecraft.getMinecraft().player;

			if(player != null) {
				ItemStack stack = null;
				if(player.getHeldItem(EnumHand.MAIN_HAND) != null && player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemComponentSelection) {
					stack = player.getHeldItem(EnumHand.MAIN_HAND);
				}

				if(stack != null) {
					ItemComponentSelection item = (ItemComponentSelection)stack.getItem();
					Pair<ILocalDungeonComponent, RayTraceResult> target = item.getTargetComponent(stack, player, event.getPartialTicks());
					ILocalDungeonComponent selected = item.getSelectedComponent(player.world, stack);

					if(target != null || selected != null) {
						GlStateManager.pushMatrix();

						double rx = Minecraft.getMinecraft().getRenderManager().viewerPosX;
						double ry = Minecraft.getMinecraft().getRenderManager().viewerPosY;
						double rz = Minecraft.getMinecraft().getRenderManager().viewerPosZ;

						GlStateManager.disableTexture2D();
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
						GlStateManager.glLineWidth(1.5F);
						GlStateManager.depthMask(false);
						GL11.glEnable(GL11.GL_LINE_SMOOTH);

						if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
							GlStateManager.disableDepth();
						}

						GlStateManager.enablePolygonOffset();
						GlStateManager.doPolygonOffset(-0.1F, -10.0F);
						
						if(target != null) {
							GlStateManager.color(1, 1, 1, 0.2F);
							WorldRenderHandler.drawBoundingBox(target.getKey().getBounds().offset(-rx, -ry, -rz));
						}
						if(selected != null) {
							GlStateManager.color(1, 1, 1, 0.3F);
							WorldRenderHandler.drawBoundingBox(selected.getBounds().offset(-rx, -ry, -rz));
							GlStateManager.color(0, 0, 0, 1);
							WorldRenderHandler.drawBoundingBoxOutline(selected.getBounds().offset(-rx, -ry, -rz));
						}
						
						GlStateManager.disablePolygonOffset();

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
}
