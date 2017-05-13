package tcb.adventurousdungeons.util;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class AABBUtil extends AxisAlignedBB {
	public final World world;

	public AABBUtil(World world, AxisAlignedBB aabb) {
		super(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
		this.world = world;
	}

	public List<Entity> getEntities() {
		return this.getEntities(null);
	}

	public List<Entity> getEntities(@Nullable Predicate<Entity> filter) {
		return this.world.getEntitiesWithinAABB(Entity.class, this, filter);
	}

	public double getVolume() {
		return (this.maxX - this.minX) * (this.maxY - this.minY) * (this.maxZ - this.minZ);
	}

	public void processBlocks(Consumer<BlockPos> process, boolean onEdges) {
		this.processBlocks(process, null, onEdges);
	}

	public void processBlocks(Consumer<BlockPos> process, @Nullable Predicate<IBlockState> filter, boolean onEdges) {
		BlockPos p1;
		if(onEdges) {
			p1 = new BlockPos(this.minX, this.minY, this.minZ);
		} else {
			p1 = new BlockPos(this.minX + 1, this.minY + 1, this.minZ + 1);
		}
		BlockPos p2;
		if(onEdges) {
			p2 = new BlockPos(this.maxX, this.maxY, this.maxZ);
		} else {
			p2 = new BlockPos(this.maxX - 1, this.maxY - 1, this.maxZ - 1);
		}
		for(BlockPos pos : MutableBlockPos.getAllInBox(p1, p2)) {
			if(filter == null || filter.apply(this.world.getBlockState(pos))) {
				process.accept(pos.toImmutable());
			}
		}
	}
}
