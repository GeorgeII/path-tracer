public class Material {
    private Vector emissive;
    private Vector diffuse;

    public Material(Vector emissive, Vector diffuse) {
        this.emissive = emissive;
        this.diffuse = diffuse;
    }

    public Vector getEmissive() {
        return emissive;
    }

    public void setEmissive(Vector emissive) {
        this.emissive = emissive;
    }

    public Vector getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector diffuse) {
        this.diffuse = diffuse;
    }
}
