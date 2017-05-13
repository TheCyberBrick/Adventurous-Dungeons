package tcb.adventurousdungeons.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class MoreNBTUtils {
	private MoreNBTUtils() { }

	public static NBTTagCompound writeAABB(AxisAlignedBB aabb) {
		NBTTagCompound boxNbt = new NBTTagCompound();
		boxNbt.setDouble("minX", aabb.minX);
		boxNbt.setDouble("minY", aabb.minY);
		boxNbt.setDouble("minZ", aabb.minZ);
		boxNbt.setDouble("maxX", aabb.maxX);
		boxNbt.setDouble("maxY", aabb.maxY);
		boxNbt.setDouble("maxZ", aabb.maxZ);
		return boxNbt;
	}

	public static AxisAlignedBB readAABB(NBTTagCompound boxNbt) {
		double minX = boxNbt.getDouble("minX");
		double minY = boxNbt.getDouble("minY");
		double minZ = boxNbt.getDouble("minZ");
		double maxX = boxNbt.getDouble("maxX");
		double maxY = boxNbt.getDouble("maxY");
		double maxZ = boxNbt.getDouble("maxZ");
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static NBTTagCompound writeVec(Vec3d vec) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("x", vec.xCoord);
		nbt.setDouble("y", vec.yCoord);
		nbt.setDouble("z", vec.zCoord);
		return nbt;
	}
	
	public static Vec3d readVec(NBTTagCompound nbt) {
		return new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
	}
}
