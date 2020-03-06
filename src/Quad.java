public class Quad {
    private Vector a, b, c, d;
    private Material material;
    private Vector normal;

    public Quad(Vector a, Vector b, Vector c, Vector d, Vector emissive, Vector diffuse) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.material = new Material(emissive, diffuse);

        Vector e1 = this.b.substract(this.a);
        Vector e2 = this.c.substract(this.a);
        normal = e1.crossProduct(e2).normalize();
    }

    public boolean isIntersect(Vector rayPos, Vector rayDir, RayHitInfo info) {
        Vector pa = (this.a).substract(rayPos);
        Vector pb = (this.b).substract(rayPos);
        Vector pc = (this.c).substract(rayPos);

        Vector m = pc.crossProduct(rayDir);
        Vector r;
        double v = pa.dotProduct(m);

        if (v >= 0.0) {
            double u = -pb.dotProduct(m);
            if (u < 0.0)
                return false;

            double w = rayDir.scalarTripleProduct(pb, pa);
            if (w < 0.0)
                return false;

            double denom = 1.0 / (u + v + w);
            u *= denom;
            v *= denom;
            w *= denom;
            r = (this.a).multiply(u).add((this.b).multiply(v)).add((this.c).multiply(w));
        }
        else {
            Vector pd = (this.d).substract(rayPos);
            double u = pd.dotProduct(m);
            if (u < 0.0)
                return false;

            double w = rayDir.scalarTripleProduct(pa, pd);
            if (w < 0.0)
                return false;

            v = -v;

            double denom = 1.0 / (u + v + w);
            u *= denom;
            v *= denom;
            w *= denom;
            r = (this.a).multiply(u).add((this.d).multiply(v)).add((this.c).multiply(w));
        }

        Vector normal = this.normal;
        if ((this.normal).dotProduct(rayDir) > 0)
            normal = normal.multiply(-1.0);

        double t = 0.0;
        if (Math.abs(rayDir.x) > 0.0) {
            t = (r.x - rayPos.x) / rayDir.x;
        }
        else if (Math.abs(rayDir.y) > 0.0) {
            t = (r.y - rayPos.y) / rayDir.y;
        }
        else if (Math.abs(rayDir.z) > 0.0) {
            t = (r.z - rayPos.z) / rayDir.z;
        }

        if (t < 0.0)
            return false;

        if (info.getCollisionTime() >= 0.0 && t > info.getCollisionTime())
            return false;

        info.setCollisionTime(t);
        info.setIntersectionPoint(r);
        info.setMaterial(this.material);
        info.setSurfaceNormal(normal);

        return true;
    }
}
