public class Material {
    Vector emissive;
    Vector diffuse;

    public Material(Vector emissive, Vector diffuse) {
        this.emissive = emissive;
        this.diffuse = diffuse;
    }

    public Vector getEmissive() {
        return this.emissive;
    }

    public void setEmissive(Vector emissive) {
        this.emissive = emissive;
    }

    public Vector getDiffuse() {
        return this.diffuse;
    }

    public void setDiffuse(Vector diffuse) {
        this.diffuse = diffuse;
    }
}
