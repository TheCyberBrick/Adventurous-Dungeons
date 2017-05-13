package tcb.adventurousdungeons.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import tcb.adventurousdungeons.AdventurousDungeons;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;
import tcb.adventurousdungeons.api.storage.ILocalStorage;
import tcb.adventurousdungeons.api.storage.IWorldStorage;
import tcb.adventurousdungeons.common.network.common.MessageEditDungeonScript;
import tcb.adventurousdungeons.common.storage.WorldStorageImpl;

public class ItemScriptEditor extends Item {
	public ItemScriptEditor() {
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "script_editor"));
		this.setUnlocalizedName(ModInfo.ID + ".script_editor");
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInfo.ID + ":script_editor", "inventory"));
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!world.isRemote && hand == EnumHand.MAIN_HAND) {
			IWorldStorage storage = WorldStorageImpl.getCapability(world);

			for(ILocalStorage localStorage : storage.getLocalStorageHandler().getLoadedStorages()) {
				if(localStorage instanceof IDungeon) {
					IDungeon dungeon = (IDungeon) localStorage;
					if(dungeon.getBoundingBox().isVecInside(new Vec3d(pos).addVector(hitX, hitY, hitZ))) {
						for(IDungeonComponent component : dungeon.getDungeonComponents()) {
							if(component instanceof ScriptDC) {
								AdventurousDungeons.getNetwork().sendTo(MessageEditDungeonScript.createClientbound((ScriptDC) component), (EntityPlayerMP) player);
							}
						}
					}
				}
			}
		}

		return EnumActionResult.SUCCESS;
	}
}
