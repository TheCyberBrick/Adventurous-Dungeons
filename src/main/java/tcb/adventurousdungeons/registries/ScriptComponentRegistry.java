package tcb.adventurousdungeons.registries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import tcb.adventurousdungeons.ModInfo;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.IScriptComponentRegistry;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.gui.GuiCreateScriptComponentSimple;
import tcb.adventurousdungeons.api.script.gui.GuiCreateScriptComponentSimple.ISimpleScriptComponentFactory;
import tcb.adventurousdungeons.api.script.impl.CreateABBSC;
import tcb.adventurousdungeons.api.script.impl.DungeonAreaSC;
import tcb.adventurousdungeons.api.script.impl.DungeonEntitiesSC;
import tcb.adventurousdungeons.api.script.impl.DuplicatorSC;
import tcb.adventurousdungeons.api.script.impl.GetBlockStateSC;
import tcb.adventurousdungeons.api.script.impl.IfElseSC;
import tcb.adventurousdungeons.api.script.impl.IterableSC;
import tcb.adventurousdungeons.api.script.impl.IteratorSC;
import tcb.adventurousdungeons.api.script.impl.MergerSC;
import tcb.adventurousdungeons.api.script.impl.SetBlocksSC;
import tcb.adventurousdungeons.api.script.impl.StringCombinerSC;
import tcb.adventurousdungeons.api.script.impl.WriteToChatSC;
import tcb.adventurousdungeons.api.script.impl.bool.BoolAndSC;
import tcb.adventurousdungeons.api.script.impl.bool.BoolNotSC;
import tcb.adventurousdungeons.api.script.impl.bool.BoolOrSC;
import tcb.adventurousdungeons.api.script.impl.bool.EqualsSC;
import tcb.adventurousdungeons.api.script.impl.bool.GreaterEQSC;
import tcb.adventurousdungeons.api.script.impl.bool.GreaterSC;
import tcb.adventurousdungeons.api.script.impl.bool.IntersectsAABBSC;
import tcb.adventurousdungeons.api.script.impl.bool.IsNullSC;
import tcb.adventurousdungeons.api.script.impl.bool.LessEQSC;
import tcb.adventurousdungeons.api.script.impl.bool.LessSC;
import tcb.adventurousdungeons.api.script.impl.constants.BlockStateConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.BoolConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.DirectionConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.DoubleConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.DungeonComponentConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.IntConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.ItemStackConstantSC;
import tcb.adventurousdungeons.api.script.impl.constants.StringConstantSC;
import tcb.adventurousdungeons.api.script.impl.entity.CreateItemEntitySC;
import tcb.adventurousdungeons.api.script.impl.entity.GetEntityBoundsSC;
import tcb.adventurousdungeons.api.script.impl.entity.SpawnEntitySC;
import tcb.adventurousdungeons.api.script.impl.item.CreateItemStackSC;
import tcb.adventurousdungeons.api.script.impl.item.GetBlockItemHolderSC;
import tcb.adventurousdungeons.api.script.impl.item.GetEntityItemItemHolderSC;
import tcb.adventurousdungeons.api.script.impl.item.GetItemStackCountSC;
import tcb.adventurousdungeons.api.script.impl.item.GetItemStackItemSC;
import tcb.adventurousdungeons.api.script.impl.item.GetItemStackMetaSC;
import tcb.adventurousdungeons.api.script.impl.item.GetItemStackSC;
import tcb.adventurousdungeons.api.script.impl.item.GetPlayerHeldItemHolderSC;
import tcb.adventurousdungeons.api.script.impl.item.GetPlayerItemHolderSC;
import tcb.adventurousdungeons.api.script.impl.item.SetItemStackCountSC;
import tcb.adventurousdungeons.api.script.impl.item.SetItemStackMetaSC;
import tcb.adventurousdungeons.api.script.impl.item.SetItemStackSC;
import tcb.adventurousdungeons.api.script.impl.math.AddDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.AddIntSC;
import tcb.adventurousdungeons.api.script.impl.math.CeilDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.DivDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.DivIntSC;
import tcb.adventurousdungeons.api.script.impl.math.DoubleToIntSC;
import tcb.adventurousdungeons.api.script.impl.math.FloorDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.IntToDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.MulDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.MulIntSC;
import tcb.adventurousdungeons.api.script.impl.math.SubDoubleSC;
import tcb.adventurousdungeons.api.script.impl.math.SubIntSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.AddVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.CeilVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.CreateVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.DivVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.FloorVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.LengthVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.MulVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.SubVecSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.ToVec3dSC;
import tcb.adventurousdungeons.api.script.impl.math.vec.ToVec3iSC;
import tcb.adventurousdungeons.api.script.impl.trigger.BlockActivateTriggerSC;
import tcb.adventurousdungeons.api.script.impl.trigger.EntityAreaTriggerSC;
import tcb.adventurousdungeons.api.script.impl.trigger.TickTriggerSC;

public class ScriptComponentRegistry implements IScriptComponentRegistry {
	private final List<Class<? extends IScriptComponent>> components = new ArrayList<>();
	private final Map<ResourceLocation, Constructor<? extends IScriptComponent>> componentMap = new HashMap<>();
	private final Map<Class<? extends IScriptComponent>, ResourceLocation> ids = new HashMap<>();

	private final Map<Class<? extends IScriptComponent>, IScriptComponentCreationGuiFactory<?>> factories = new HashMap<>();

	public static final ScriptComponentRegistry INSTANCE = new ScriptComponentRegistry();

	public static void register() {
		//Bool
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "equals"), EqualsSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "is_null"), IsNullSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "intersects_aabb"), IntersectsAABBSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "bool_and"), BoolAndSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "bool_or"), BoolOrSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "greater_equal"), GreaterEQSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "greater"), GreaterSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "less_equal"), LessEQSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "less"), LessSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "bool_not"), BoolNotSC.class);

		//Entity
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "create_item_entity"), CreateItemEntitySC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "spawn_entity"), SpawnEntitySC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_entity_bounds"), GetEntityBoundsSC.class);

		//Item
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "create_item_stack"), CreateItemStackSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_item_stack_count"), GetItemStackCountSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_item_stack_item"), GetItemStackItemSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "set_item_stack_count"), SetItemStackCountSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_item_stack_meta"), GetItemStackMetaSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "set_item_stack_meta"), SetItemStackMetaSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_item_stack"), GetItemStackSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "set_item_stack"), SetItemStackSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_player_item_holder"), GetPlayerItemHolderSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_block_item_holder"), GetBlockItemHolderSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_player_held_item_holder"), GetPlayerHeldItemHolderSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_entity_item_item_holder"), GetEntityItemItemHolderSC.class);

		//Math
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "add_vec"), AddVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "div_vec"), DivVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "floor_vec"), FloorVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "ceil_vec"), CeilVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "length_vec"), LengthVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "mul_vec"), MulVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "sub_vec"), SubVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "to_vec_double"), ToVec3dSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "to_vec_int"), ToVec3iSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "create_vec"), CreateVecSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "add_double"), AddDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "ceil_double"), CeilDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "div_double"), DivDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "floor_double"), FloorDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "mul_double"), MulDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "sub_double"), SubDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "int_to_double"), IntToDoubleSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "double_to_int"), DoubleToIntSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "add_int"), AddIntSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "sub_int"), SubIntSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "mul_int"), MulIntSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "div_int"), DivIntSC.class);

		//Triggers
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "entity_area_trigger"), EntityAreaTriggerSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "block_activate_trigger"), BlockActivateTriggerSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "tick_trigger"), TickTriggerSC.class);

		//Constants
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "block_state_constant"), BlockStateConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "dungeon_component"), DungeonComponentConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "item_stack_constant"), ItemStackConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "string_constant"), StringConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "int_constant"), IntConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "double_constant"), DoubleConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "bool_constant"), BoolConstantSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "dir_constant"), DirectionConstantSC.class);

		//Misc
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "dungeon_area"), DungeonAreaSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "dungeon_entities"), DungeonEntitiesSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "if_else"), IfElseSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "iterator"), IteratorSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "merger"), MergerSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "set_blocks"), SetBlocksSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "string_combiner"), StringCombinerSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "write_to_chat"), WriteToChatSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "iterable"), IterableSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "duplicator"), DuplicatorSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "create_aabb"), CreateABBSC.class);
		INSTANCE.register(new ResourceLocation(ModInfo.ID, "get_block_state"), GetBlockStateSC.class);
	}

	public static void registerGuiFactories() {
		//Bool
		registerSimpleFactoryGui(EqualsSC.class, (s, n) -> new EqualsSC(s, n));
		registerSimpleFactoryGui(IsNullSC.class, (s, n) -> new IsNullSC(s, n));
		registerSimpleFactoryGui(BoolAndSC.class, (s, n) -> new BoolAndSC(s, n));
		registerSimpleFactoryGui(IntersectsAABBSC.class, (s, n) -> new IntersectsAABBSC(s, n));
		registerSimpleFactoryGui(BoolOrSC.class, (s, n) -> new BoolOrSC(s, n));
		registerSimpleFactoryGui(GreaterEQSC.class, (s, n) -> new GreaterEQSC(s, n));
		registerSimpleFactoryGui(GreaterSC.class, (s, n) -> new GreaterSC(s, n));
		registerSimpleFactoryGui(LessEQSC.class, (s, n) -> new LessEQSC(s, n));
		registerSimpleFactoryGui(LessSC.class, (s, n) -> new LessSC(s, n));
		registerSimpleFactoryGui(BoolNotSC.class, (s, n) -> new BoolNotSC(s, n));

		//Entity
		registerSimpleFactoryGui(CreateItemEntitySC.class, (s, n) -> new CreateItemEntitySC(s, n));
		registerSimpleFactoryGui(SpawnEntitySC.class, (s, n) -> new SpawnEntitySC(s, n));
		registerSimpleFactoryGui(GetEntityBoundsSC.class, (s, n) -> new GetEntityBoundsSC(s, n));

		//Item
		registerSimpleFactoryGui(CreateItemStackSC.class, (s, n) -> new CreateItemStackSC(s, n));
		registerSimpleFactoryGui(GetItemStackCountSC.class, (s, n) -> new GetItemStackCountSC(s, n));
		registerSimpleFactoryGui(GetItemStackItemSC.class, (s, n) -> new GetItemStackItemSC(s, n));
		registerSimpleFactoryGui(SetItemStackCountSC.class, (s, n) -> new SetItemStackCountSC(s, n));
		registerSimpleFactoryGui(GetItemStackMetaSC.class, (s, n) -> new GetItemStackMetaSC(s, n));
		registerSimpleFactoryGui(SetItemStackMetaSC.class, (s, n) -> new SetItemStackMetaSC(s, n));
		registerSimpleFactoryGui(GetItemStackSC.class, (s, n) -> new GetItemStackSC(s, n));
		registerSimpleFactoryGui(SetItemStackSC.class, (s, n) -> new SetItemStackSC(s, n));
		registerSimpleFactoryGui(GetPlayerItemHolderSC.class, (s, n) -> new GetPlayerItemHolderSC(s, n));
		registerSimpleFactoryGui(GetBlockItemHolderSC.class, (s, n) -> new GetBlockItemHolderSC(s, n));
		registerSimpleFactoryGui(GetPlayerHeldItemHolderSC.class, (s, n) -> new GetPlayerHeldItemHolderSC(s, n));
		registerSimpleFactoryGui(GetEntityItemItemHolderSC.class, (s, n) -> new GetEntityItemItemHolderSC(s, n));

		//Math
		registerSimpleFactoryGui(AddVecSC.class, (s, n) -> new AddVecSC(s, n));
		registerSimpleFactoryGui(DivVecSC.class, (s, n) -> new DivVecSC(s, n));
		registerSimpleFactoryGui(FloorVecSC.class, (s, n) -> new FloorVecSC(s, n));
		registerSimpleFactoryGui(CeilVecSC.class, (s, n) -> new CeilVecSC(s, n));
		registerSimpleFactoryGui(LengthVecSC.class, (s, n) -> new LengthVecSC(s, n));
		registerSimpleFactoryGui(MulVecSC.class, (s, n) -> new MulVecSC(s, n));
		registerSimpleFactoryGui(SubVecSC.class, (s, n) -> new SubVecSC(s, n));
		registerSimpleFactoryGui(ToVec3dSC.class, (s, n) -> new ToVec3dSC(s, n));
		registerSimpleFactoryGui(ToVec3iSC.class, (s, n) -> new ToVec3iSC(s, n));
		registerSimpleFactoryGui(CreateVecSC.class, (s, n) -> new CreateVecSC(s, n));
		registerSimpleFactoryGui(AddDoubleSC.class, (s, n) -> new AddDoubleSC(s, n));
		registerSimpleFactoryGui(CeilDoubleSC.class, (s, n) -> new CeilDoubleSC(s, n));
		registerSimpleFactoryGui(DivDoubleSC.class, (s, n) -> new DivDoubleSC(s, n));
		registerSimpleFactoryGui(FloorDoubleSC.class, (s, n) -> new FloorDoubleSC(s, n));
		registerSimpleFactoryGui(MulDoubleSC.class, (s, n) -> new MulDoubleSC(s, n));
		registerSimpleFactoryGui(SubDoubleSC.class, (s, n) -> new SubDoubleSC(s, n));
		registerSimpleFactoryGui(IntToDoubleSC.class, (s, n) -> new IntToDoubleSC(s, n));
		registerSimpleFactoryGui(DoubleToIntSC.class, (s, n) -> new DoubleToIntSC(s, n));
		registerSimpleFactoryGui(AddIntSC.class, (s, n) -> new AddIntSC(s, n));
		registerSimpleFactoryGui(SubIntSC.class, (s, n) -> new SubIntSC(s, n));
		registerSimpleFactoryGui(MulIntSC.class, (s, n) -> new MulIntSC(s, n));
		registerSimpleFactoryGui(DivIntSC.class, (s, n) -> new DivIntSC(s, n));

		//Triggers
		registerSimpleFactoryGui(EntityAreaTriggerSC.class, (s, n) -> new EntityAreaTriggerSC(s, n));
		registerSimpleFactoryGui(BlockActivateTriggerSC.class, (s, n) -> new BlockActivateTriggerSC(s, n));
		registerSimpleFactoryGui(TickTriggerSC.class, (s, n) -> new TickTriggerSC(s, n));

		//Constants
		INSTANCE.registerFactoryGui(StringConstantSC.class, new StringConstantSC.GuiFactory());
		INSTANCE.registerFactoryGui(IntConstantSC.class, new IntConstantSC.GuiFactory());
		INSTANCE.registerFactoryGui(DoubleConstantSC.class, new DoubleConstantSC.GuiFactory());
		INSTANCE.registerFactoryGui(BoolConstantSC.class, new BoolConstantSC.GuiFactory());
		INSTANCE.registerFactoryGui(DirectionConstantSC.class, new DirectionConstantSC.GuiFactory());

		//Misc
		registerSimpleFactoryGui(DungeonAreaSC.class, (s, n) -> new DungeonAreaSC(s, n));
		registerSimpleFactoryGui(DungeonEntitiesSC.class, (s, n) -> new DungeonEntitiesSC(s, n));
		registerSimpleFactoryGui(IfElseSC.class, (s, n) -> new IfElseSC(s, n));
		registerSimpleFactoryGui(IteratorSC.class, (s, n) -> new IteratorSC(s, n));
		INSTANCE.registerFactoryGui(MergerSC.class, new MergerSC.GuiFactory());
		registerSimpleFactoryGui(SetBlocksSC.class, (s, n) -> new SetBlocksSC(s, n));
		INSTANCE.registerFactoryGui(StringCombinerSC.class, new StringCombinerSC.GuiFactory());
		registerSimpleFactoryGui(WriteToChatSC.class, (s, n) -> new WriteToChatSC(s, n));
		INSTANCE.registerFactoryGui(IterableSC.class, new IterableSC.GuiFactory());
		INSTANCE.registerFactoryGui(DuplicatorSC.class, new DuplicatorSC.GuiFactory());
		registerSimpleFactoryGui(CreateABBSC.class, (s, n) -> new CreateABBSC(s, n));
		registerSimpleFactoryGui(GetBlockStateSC.class, (s, n) -> new GetBlockStateSC(s, n));
	}

	private static <T extends IScriptComponent> void registerSimpleFactoryGui(Class<T> componentType, ISimpleScriptComponentFactory<T> factory) {
		INSTANCE.registerFactoryGui(componentType, (parent, add, script, component, x, y) -> new GuiCreateScriptComponentSimple<T>(parent, componentType, add, script, component, x, y, factory));
	}

	@Override
	public void register(ResourceLocation registryName, Class<? extends IScriptComponent> component) {
		if(this.componentMap.containsKey(registryName)) {
			throw new RuntimeException("Duplicate registry name: " + registryName);
		}
		Constructor<? extends IScriptComponent> ctor = null;
		try {
			ctor = component.getConstructor(Script.class);
			if((ctor.getModifiers() & Modifier.PUBLIC) == 0) {
				ctor = null;
			}
		} catch(Exception ex) { }
		if(ctor == null) {
			throw new RuntimeException(String.format("Script component %s does not have a valid public (Script) constructor", registryName));
		}
		this.componentMap.put(registryName, ctor);
		this.ids.put(component, registryName);
		this.components.add(component);
	}

	@Override
	public Class<? extends IScriptComponent> getComponent(ResourceLocation registryName) {
		Constructor<? extends IScriptComponent> ctor = this.componentMap.get(registryName);
		if(ctor != null) {
			return ctor.getDeclaringClass();
		}
		return null;
	}

	@Override
	public IScriptComponent createComponent(ResourceLocation registryName, Script script) {
		Constructor<? extends IScriptComponent> ctor = this.componentMap.get(registryName);
		if(ctor != null) {
			try {
				return ctor.newInstance(script);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) { }
		}
		return null;
	}

	@Override
	public ResourceLocation getComponentID(Class<? extends IScriptComponent> component) {
		return this.ids.get(component);
	}

	@Override
	public List<Class<? extends IScriptComponent>> getRegisteredComponents() {
		return Collections.unmodifiableList(this.components);
	}

	@Override
	public <T extends IScriptComponent> void registerFactoryGui(Class<T> component, IScriptComponentCreationGuiFactory<T> factory) {
		if(this.factories.containsKey(component)) {
			throw new RuntimeException("Duplicate script component creation gui factory: " + component.getName());
		}
		this.factories.put(component, factory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IScriptComponent> IScriptComponentCreationGuiFactory<T> getFactoryGui(Class<T> component) {
		return (IScriptComponentCreationGuiFactory<T>)this.factories.get(component);
	}

	@Override
	public String getUnlocalizedName(Class<? extends IScriptComponent> component) {
		ResourceLocation regName = this.getComponentID(component);
		if(regName == null) {
			return null;
		}
		return "script_component." + regName.getResourceDomain() + "." + regName.getResourcePath();
	}
}
