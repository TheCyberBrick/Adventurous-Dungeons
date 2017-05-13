package tcb.adventurousdungeons.api.dungeon.component;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import tcb.adventurousdungeons.api.dungeon.IDungeon;

public interface IDungeonComponentRegistry {
	public void register(ResourceLocation registryName, Class<? extends IDungeonComponent> component);

	@Nullable
	public Class<? extends IDungeonComponent> getComponent(ResourceLocation registryName);

	@Nullable
	public IDungeonComponent createComponent(ResourceLocation registryName, IDungeon dungeon);
	
	@Nullable
	public ResourceLocation getComponentID(Class<? extends IDungeonComponent> component);
}
