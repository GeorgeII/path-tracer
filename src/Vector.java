import java.util.Random;

public class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector normalize() {
        double len = length();
        return new Vector(x/len, y/len, z/len);
    }

    public Vector add(Vector b) {
        return new Vector(this.x + b.x, this.y + b.y, this.z + b.z);
    }

    public Vector add(double b) {
        return new Vector(this.x + b, this.y + b, this.z + b);
    }

    public Vector substract(Vector b) {
        return new Vector(this.x - b.x, this.y - b.y, this.z - b.z);
    }

    public Vector multiply(Vector b) {
        return new Vector(this.x * b.x, this.y * b.y, this.z * b.z);
    }

    public Vector multiply(double b) {
        return new Vector(this.x * b, this.y * b, this.z * b);
    }

    public Vector divide(double b) {
        return new Vector(this.x / b, this.y / b, this.z / b);
    }

    public Vector inverse() {
        return new Vector(-this.x, -this.y, -this.z);
    }

    public double dotProduct(Vector b) {
        return this.x * b.x + this.y * b.y + this.z * b.z;
    }

    public Vector crossProduct(Vector b) {
        return new Vector(
                this.y * b.z - this.z * b.y,
                this.z * b.x - this.x * b.z,
                this.x * b.y - this.y * b.x
        );
    }

    public double scalarTripleProduct(Vector b, Vector c) {
        return this.crossProduct(b).dotProduct(c);
    }

    public Vector cosineSampleHemisphere() {
        Random rnd = new Random();
        double r1 = 2.0 * Math.PI * rnd.nextDouble();
        double r2 = rnd.nextDouble();
        double r2s = Math.sqrt(r2);

        Vector u;
        if (Math.abs(x) > 0.1) {
            u = new Vector(0.0, 1.0, 0.0).crossProduct(this);
        }
        else {
            u = new Vector(1.0, 0.0, 0.0).crossProduct(this);
        }

        u = u.normalize();
        Vector v = this.crossProduct(u);
        Vector d = u.multiply(Math.cos(r1)*r2s).add(v.multiply(Math.sin(r1)*r2s)).add(this.multiply(Math.sqrt(1-r2)));
        d = d.normalize();

        return d;
    }
}
