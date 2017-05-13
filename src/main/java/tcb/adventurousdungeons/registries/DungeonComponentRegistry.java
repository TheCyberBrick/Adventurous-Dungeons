package tcb.adventurousdungeons.registries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.dungeon.IDungeon;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponent;
import tcb.adventurousdungeons.api.dungeon.component.IDungeonComponentRegistry;
import tcb.adventurousdungeons.api.dungeon.component.impl.MoveEntitySplineDC;
import tcb.adventurousdungeons.api.dungeon.component.impl.DungeonAreaDC;
import tcb.adventurousdungeons.api.dungeon.component.impl.EntityAreaTriggerDC;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;

public class DungeonComponentRegistry implements IDungeonComponentRegistry {
	private final Map<ResourceLocation, Constructor<? extends IDungeonComponent>> components = new HashMap<>();
	private final Map<Class<? extends IDungeonComponent>, ResourceLocation> ids = new HashMap<>();

	public static final DungeonComponentRegistry INSTANCE = new DungeonComponentRegistry();

	public static void register() {
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "entity_area_trigger"), EntityAreaTriggerDC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "area"), DungeonAreaDC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "script"), ScriptDC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "move_entity_spline"), MoveEntitySplineDC.class);
	}

	@Override
	public void register(ResourceLocation registryName, Class<? extends IDungeonComponent> component) {
		if(components.containsKey(registryName)) {
			throw new RuntimeException("Duplicate registry name: " + registryName);
		}
		Constructor<? extends IDungeonComponent> ctor = null;
		try {
			ctor = component.getConstructor(IDungeon.class);
			if((ctor.getModifiers() & Modifier.PUBLIC) == 0) {
				ctor = null;
			}
		} catch(Exception ex) { }
		if(ctor == null) {
			throw new RuntimeException(String.format("Dungeon component %s does not have a valid public (IDungeon) constructor", registryName));
		}
		components.put(registryName, ctor);
		ids.put(component, registryName);
	}

	@Override
	public Class<? extends IDungeonComponent> getComponent(ResourceLocation registryName) {
		Constructor<? extends IDungeonComponent> ctor = components.get(registryName);
		if(ctor != null) {
			return ctor.getDeclaringClass();
		}
		return null;
	}

	@Override
	public IDungeonComponent createComponent(ResourceLocation registryName, IDungeon dungeon) {
		Constructor<? extends IDungeonComponent> ctor = components.get(registryName);
		if(ctor != null) {
			try {
				return ctor.newInstance(dungeon);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) { }
		}
		return null;
	}

	@Override
	public ResourceLocation getComponentID(Class<? extends IDungeonComponent> component) {
		return ids.get(component);
	}
}
