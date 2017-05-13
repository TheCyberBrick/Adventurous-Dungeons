package tcb.adventurousdungeons.util;

import net.minecraft.util.math.Vec3d;

public class Line {
	public Vec3d sourcePoint = new Vec3d(0, 0, 0);
	public Vec3d direction = new Vec3d(0, 0, 0);

	public Line(double sx, double sy, double sz, double dx, double dy, double dz) {
		this.sourcePoint = new Vec3d(sx, sy, sz);
		this.direction = new Vec3d(dx, dy, dz);
	}

	public Vec3d intersect(Line line) {
		double a = this.sourcePoint.xCoord;
		double b = this.direction.xCoord;
		double c = line.sourcePoint.xCoord;
		double d = line.direction.xCoord;
		double e = this.sourcePoint.yCoord;
		double f = this.direction.yCoord;
		double g = line.sourcePoint.yCoord;
		double h = line.direction.yCoord;
		double te = -(a*h-c*h-d*(e-g));
		double be = b*h-d*f;
		if(be == 0) {
			return this.intersectXZ(line);
		}
		double t = te / be;
		return new Vec3d(this.sourcePoint.xCoord + this.direction.xCoord * t, this.sourcePoint.yCoord + this.direction.yCoord * t, this.sourcePoint.zCoord + this.direction.zCoord * t);
	}

	private Vec3d intersectXZ(Line line) {
		double a = this.sourcePoint.xCoord;
		double b = this.direction.xCoord;
		double c = line.sourcePoint.xCoord;
		double d = line.direction.xCoord;
		double e = this.sourcePoint.zCoord;
		double f = this.direction.zCoord;
		double g = line.sourcePoint.zCoord;
		double h = line.direction.zCoord;
		double te = -(a*h-c*h-d*(e-g));
		double be = b*h-d*f;
		if(be == 0) {
			return this.intersectYZ(line);
		}
		double t = te / be;
		return new Vec3d(this.sourcePoint.xCoord + this.direction.xCoord * t, this.sourcePoint.yCoord + this.direction.yCoord * t, this.sourcePoint.zCoord + this.direction.zCoord * t);
	}

	private Vec3d intersectYZ(Line line) {
		double a = this.sourcePoint.yCoord;
		double b = this.direction.yCoord;
		double c = line.sourcePoint.yCoord;
		double d = line.direction.yCoord;
		double e = this.sourcePoint.zCoord;
		double f = this.direction.zCoord;
		double g = line.sourcePoint.zCoord;
		double h = line.direction.zCoord;
		double te = -(a*h-c*h-d*(e-g));
		double be = b*h-d*f;
		if(be == 0) {
			return null;
		}
		double t = te / be;
		return new Vec3d(this.sourcePoint.xCoord + this.direction.xCoord * t, this.sourcePoint.yCoord + this.direction.yCoord * t, this.sourcePoint.zCoord + this.direction.zCoord * t);
	}

	public Vec3d intersectPlane(Vec3d pointOnPlane, Vec3d planeNormal) {
		Vec3d result = new Vec3d(this.sourcePoint.xCoord, this.sourcePoint.yCoord, this.sourcePoint.zCoord);
		double d = pointOnPlane.subtract(this.sourcePoint).dotProduct(planeNormal) / this.direction.dotProduct(planeNormal);
		result = result.add(this.direction.scale(d));
		if(this.direction.dotProduct(planeNormal) == 0.0D) {
			return null;
		}
		return result;
	}
}
