package tcb.adventurousdungeons.common.item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;
import tcb.adventurousdungeons.api.storage.IChunkStorage;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.api.storage.LocalRegion;
import tcb.adventurousdungeons.api.storage.LocalStorageReference;
import tcb.adventurousdungeons.api.storage.StorageUUID;
import tcb.adventurousdungeons.common.storage.ADLocalStorage;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class ItemTest extends Item {
	public ItemTest() {
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "test_item"));
		this.setUnlocalizedName(ModInfo.ID + ".test_item");
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInfo.ID + ":test_item", "inventory"));
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		//				if(world.isRemote) {
		//					Minecraft.getMinecraft().displayGuiScreen(new GuiEditScript(new Script()));
		//				}
		//
		if(!world.isRemote && hand == EnumHand.MAIN_HAND) {
			IWorldStorage storage = WorldStorageImpl.getCapability(world);

			if(player.isSneaking()) {
				IChunkStorage chunkStorage = storage.getChunkStorage(world.getChunkFromBlockCoords(pos));
				List<LocalStorageReference> refs = new ArrayList<>();
				refs.addAll(chunkStorage.getLocalStorageReferences());
				for(LocalStorageReference ref : refs) {
					ILocalStorage local = storage.getLocalStorageHandler().getLocalStorage(ref.getID());
					if(local instanceof IDungeon && ((IDungeon)local).getBoundingBox().isVecInside(new Vec3d(pos).addVector(hitX, hitY, hitZ))) {
						storage.getLocalStorageHandler().removeLocalStorage(local);
					}
				}
			} else {
				ADLocalStorage local = new ADLocalStorage(storage, new StorageUUID(UUID.randomUUID()), LocalRegion.getFromBlockPos(pos));

				local.setBoundingBox(new AxisAlignedBB(pos).expand(10, 6, 10));

				local.addDungeonComponent(new ScriptDC(local));

				local.linkChunks();

				storage.getLocalStorageHandler().addLocalStorage(local);
			}
		}

		return EnumActionResult.SUCCESS;
	}
}
