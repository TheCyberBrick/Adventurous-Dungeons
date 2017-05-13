package tcb.adventurousdungeons.api.script;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IScriptComponentRegistry {
	public void register(ResourceLocation registryName, Class<? extends IScriptComponent> component);

	public List<Class<? extends IScriptComponent>> getRegisteredComponents();

	@Nullable
	public Class<? extends IScriptComponent> getComponent(ResourceLocation registryName);

	@Nullable
	public IScriptComponent createComponent(ResourceLocation registryName, Script script);

	@Nullable
	public ResourceLocation getComponentID(Class<? extends IScriptComponent> component);

	@SideOnly(Side.CLIENT)
	public <T extends IScriptComponent> void registerFactoryGui(Class<T> component, IScriptComponentCreationGuiFactory<T> factory);

	@SideOnly(Side.CLIENT)
	public <T extends IScriptComponent> IScriptComponentCreationGuiFactory<T> getFactoryGui(Class<T> component);

	@Nullable
	public String getUnlocalizedName(Class<? extends IScriptComponent> component);
}
