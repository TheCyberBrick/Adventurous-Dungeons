package tcb.adventurousdungeons.api.script.impl.subscript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.api.script.DungeonScriptComponent;
import tcb.adventurousdungeons.api.script.OutputPort;
import tcb.adventurousdungeons.api.script.Script;
import tcb.adventurousdungeons.api.script.ScriptException;

public class SubScriptImportSC extends DungeonScriptComponent {
	private final List<String> imports = new ArrayList<>();

	private Map<String, Object> vals;

	public SubScriptImportSC(Script script) {
		super(script);
	}

	public SubScriptImportSC(Script script, String name, List<String> imports) {
		super(script, name);
		this.imports.addAll(imports);
	}

	public void setImportValues(Map<String, Object> values) {
		this.vals = values;
	}

	@Override
	protected boolean hasProgramFlowInput() {
		return false;
	}

	@Override
	protected boolean hasProgramFlowOutput() {
		return true;
	}
	
	@Override
	protected void createPorts() {
		for(String im : this.imports) {
			this.out(im, Object.class);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void run() throws ScriptException {
		for(String im : this.imports) {
			this.put((OutputPort<Object>) this.getOutput(im), this.vals.get(im));
		}
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		super.serialize(nbt);

		NBTTagList importsNbt = new NBTTagList();
		for(String im : this.imports) {
			importsNbt.appendTag(new NBTTagString(im));
		}
		nbt.setTag("imports", importsNbt);

		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		super.deserialize(nbt);

		this.imports.clear();
		NBTTagList importsNbt = nbt.getTagList("imports", Constants.NBT.TAG_STRING);
		for(int i = 0; i < importsNbt.tagCount(); i++) {
			this.imports.add(importsNbt.getStringTagAt(i));
		}
	}
}
