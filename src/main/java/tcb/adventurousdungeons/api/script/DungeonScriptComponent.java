package tcb.adventurousdungeons.api.script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import tcb.adventurousdungeons.api.dungeon.component.impl.ScriptDC;
import tcb.adventurousdungeons.util.MoreNBTUtils;

public abstract class DungeonScriptComponent extends ScriptComponent implements IDungeonScriptComponent {
	private ScriptDC dungeonComponent;
	private float guiX, guiY;
	private boolean additionalInfo;
	private Map<String, List<Vec3d>> splinePoints = new HashMap<>();

	public DungeonScriptComponent(Script script) {
		super(script);
	}

	public DungeonScriptComponent(Script script, String name) {
		super(script, name);
	}

	@Override
	public ScriptDC getDungeonComponent() {
		return this.dungeonComponent;
	}

	@Override
	public void setDungeonComponent(ScriptDC component) {
		this.dungeonComponent = component;
	}

	@Override
	public void setGuiX(float x) {
		this.guiX = x;
	}

	@Override
	public void setGuiY(float y) {
		this.guiY = y;
	}

	@Override
	public float getGuiX() {
		return this.guiX;
	}

	@Override
	public float getGuiY() {
		return this.guiY;
	}

	@Override
	public boolean isAdditionalInfoOpen() {
		return this.additionalInfo;
	}

	@Override
	public void setAdditionalInfoOpen(boolean open) {
		this.additionalInfo = open;
	}

	@Override
	public List<Vec3d> getSplinePoints(String port) {
		return this.splinePoints.get(port);
	}

	@Override
	public void setSplinePoints(String port, List<Vec3d> points) {
		this.splinePoints.put(port, points);
	}

	@Override
	public NBTTagCompound serialize(NBTTagCompound nbt) {
		nbt.setUniqueId("uuid", this.getID());
		nbt.setString("name", this.getName());
		nbt.setFloat("guiX", this.getGuiX());
		nbt.setFloat("guiY", this.getGuiY());
		nbt.setBoolean("additionalInfo", this.isAdditionalInfoOpen());
		if(this.getError() != null) {
			nbt.setString("error", this.getError());
		}
		NBTTagList splinePointsNbt = new NBTTagList();
		for(OutputPort<?> out : this.getOutputs()) {
			List<Vec3d> splinePts = this.getSplinePoints(out.getName());
			if(splinePts != null) {
				NBTTagCompound splineNbt = new NBTTagCompound();
				splineNbt.setString("port", out.getName());
				NBTTagList splinePointsListNbt = new NBTTagList();
				for(Vec3d pt : splinePts) {
					splinePointsListNbt.appendTag(MoreNBTUtils.writeVec(pt));
				}
				splineNbt.setTag("controlPoints", splinePointsListNbt);
				splinePointsNbt.appendTag(splineNbt);
			}
		}
		nbt.setTag("splinePoints", splinePointsNbt);
		return nbt;
	}

	@Override
	public void deserialize(NBTTagCompound nbt) {
		this.setID(nbt.getUniqueId("uuid"));
		this.setName(nbt.getString("name"));
		this.setGuiX(nbt.getFloat("guiX"));
		this.setGuiY(nbt.getFloat("guiY"));
		this.setAdditionalInfoOpen(nbt.getBoolean("additionalInfo"));
		if(nbt.hasKey("error", Constants.NBT.TAG_STRING)) {
			this.setError(nbt.getString("error"));
		} else {
			this.setError(null);
		}
		NBTTagList splinePointsNbt = nbt.getTagList("splinePoints", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < splinePointsNbt.tagCount(); i++) {
			NBTTagCompound splineNbt = splinePointsNbt.getCompoundTagAt(i);
			String port = splineNbt.getString("port");
			NBTTagList splinePointsListNbt = splineNbt.getTagList("controlPoints", Constants.NBT.TAG_COMPOUND);
			List<Vec3d> pts = new ArrayList<>(splinePointsListNbt.tagCount());
			for(int k = 0; k < splinePointsListNbt.tagCount(); k++) {
				pts.add(MoreNBTUtils.readVec(splinePointsListNbt.getCompoundTagAt(k)));
			}
			this.setSplinePoints(port, pts);
		}
	}
}
