public class RayHitInfo {
    private Material material;
    private Vector surfaceNormal;
    private Vector intersectionPoint;
    private double collisionTime;

    public RayHitInfo() {
        material = null;
        surfaceNormal = null;
        intersectionPoint = null;
        collisionTime = -1.0;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Vector getSurfaceNormal() {
        return surfaceNormal;
    }

    public void setSurfaceNormal(Vector surfaceNormal) {
        this.surfaceNormal = surfaceNormal;
    }

    public Vector getIntersectionPoint() {
        return intersectionPoint;
    }

    public void setIntersectionPoint(Vector intersectionPoint) {
        this.intersectionPoint = intersectionPoint;
    }

    public double getCollisionTime() {
        return collisionTime;
    }

    public void setCollisionTime(double collisionTime) {
        this.collisionTime = collisionTime;
    }
}
