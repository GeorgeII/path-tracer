public class Sphere {
    private Vector center;
    private double radius;
    private Material material;

    public Sphere(Vector center, double radius, Vector emissive, Vector diffuse) {
        this.center = center;
        this.radius = radius;
        this.material = new Material(emissive, diffuse);
    }

    public boolean isIntersect(Vector rayPos, Vector rayDir, RayHitInfo info) {
        Vector m = rayPos.substract(this.center);

        double b = m.dotProduct(rayDir);
        double c = m.dotProduct(m) - this.radius * this.radius;

        if (c > 0 && b > 0)
            return false;

        double discr = b * b - c;

        if (discr <= 0)
            return false;

        double collisionTime = -b - Math.sqrt(discr);

        if (collisionTime < 0)
            collisionTime = -b + Math.sqrt(discr);

        if (info.getCollisionTime() >= 0 && collisionTime > info.getCollisionTime())
            return false;

        Vector normal = rayPos.add(rayDir.multiply(collisionTime)).substract(this.center).normalize();

        if (normal.dotProduct(rayDir) > 0.0)
            normal = normal.multiply(-1.0);

        info.setCollisionTime(collisionTime);
        info.setIntersectionPoint(rayDir.multiply(collisionTime).add(rayPos));
        info.setMaterial(this.material);
        info.setSurfaceNormal(normal);

        return true;
    }
}
