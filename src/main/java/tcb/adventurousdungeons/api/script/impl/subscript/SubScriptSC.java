package tcb.adventurousdungeons.api.script.impl.subscript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IDungeonScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponent;
import tcb.adventurousdungeons.api.script.IScriptComponentCreationGuiFactory;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;
import tcb.adventurousdungeons.client.gui.GuiEditScript;

public class SubScriptSC extends DungeonScriptComponent {
	private Script subScript = new Script();

	private final List<String> imports = new ArrayList<>();
	private final List<String> exports = new ArrayList<>();

	public SubScriptSC(Script script) {
		super(script);
	}

	public SubScriptSC(Script script, String name, List<String> imports, List<String> exports) {
		super(script, name);
		this.imports.addAll(imports);
		this.exports.addAll(exports);
	}

	public List<String> getImports() {
		return Collections.unmodifiableList(this.imports);
	}

	public List<String> getExports() {
		return Collections.unmodifiableList(this.exports);
	}

	public Script getSubScript() {
		return this.subScript;
	}

	public void setSubScript(Script script) {
		this.subScript = script;
		for(IScriptComponent component : script.getComponents()) {
			if(component instanceof IDungeonScriptComponent) {
				((IDungeonScriptComponent)component).setDungeonComponent(this.getDungeonComponent());
			}
		}
	}

	@Override
	protected void createPorts() {
		for(String input : this.imports) {
			this.in(input, Object.class, false);
		}
		for(String output : this.exports) {
			this.out(output, Object.class);
		}
	}

	@Override
	protected void run() throws ScriptException {
		Map<String, Object> imports = new HashMap<>();
		for(String input : this.imports) {
			imports.put(input, this.get(this.getInput(input)));
		}
		for(IScriptComponent scriptComponent : this.subScript.getComponents()) {
			if(scriptComponent instanceof IDungeonScriptComponent) {
				((IDungeonScriptComponent)scriptComponent).setDungeonComponent(this.getDungeonComponent());
			}
		}
		for(IScriptComponent scriptComponent : this.subScript.getComponents()) {
			if(scriptComponent instanceof SubScriptImportSC) {
				((SubScriptImportSC) scriptComponent).setImportValues(imports);
				scriptComponent.execute();
			}
		}

	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);

		nbt.setTag("subScript", this.subScript.writeToNBT(new NBTTagCompound()));

		NBTTagList importsNbt = new NBTTagList();
		for(String im : this.imports) {
			importsNbt.appendTag(new NBTTagString(im));
		}
		nbt.setTag("imports", importsNbt);

		NBTTagList exportsNbt = new NBTTagList();
		for(String ex : this.exports) {
			exportsNbt.appendTag(new NBTTagString(ex));
		}
		nbt.setTag("exports", exportsNbt);

		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);

		Script script = new Script();
		script.readFromNBT(nbt.getCompoundTag("subScript"));
		this.setSubScript(script);

		this.imports.clear();
		NBTTagList importsNbt = nbt.getTagList("imports", Constants.NBT.TAG_STRING);
		for(int i = 0; i < importsNbt.tagCount(); i++) {
			this.imports.add(importsNbt.getStringTagAt(i));
		}

		this.exports.clear();
		NBTTagList exportsNbt = new NBTTagList();
		for(int i = 0; i < exportsNbt.tagCount(); i++) {
			this.exports.add(exportsNbt.getStringTagAt(i));
		}
	}

	@SideOnly(Side.CLIENT)
	public static class GuiFactory implements IScriptComponentCreationGuiFactory<SubScriptSC> {
		@Override
		public GuiScreen getFactoryGui(GuiScreen parent, Consumer<IScriptComponent> add, Script script, @Nullable SubScriptSC component, float x, float y) {
			Script subScriptCopy = new Script();
			subScriptCopy.readFromNBT(component.getSubScript().writeToNBT(new NBTTagCompound()));
			return new GuiEditScript(parent, component.getDungeonComponent().getDungeon(), component.getDungeonComponent(), subScriptCopy, component);
		}

		@Override
		public boolean isEditOnly() {
			return true;
		}
	}
}
