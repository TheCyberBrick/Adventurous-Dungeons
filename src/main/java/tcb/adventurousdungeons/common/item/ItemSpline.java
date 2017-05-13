package tcb.adventurousdungeons.common.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import tcb.adventurousdungeons.ModInfo;

public class ItemSpline extends Item {
	public ItemSpline() {
		this.setRegistryName(new ResourceLocation(ModInfo.ID, "spline_item"));
		this.setUnlocalizedName(ModInfo.ID + ".spline_item");
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ModInfo.ID + ":spline_item", "inventory"));
		this.setCreativeTab(CreativeTabs.MISC);
		this.setMaxStackSize(1);
	}
}
