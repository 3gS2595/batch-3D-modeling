package main.form;

public class Material {
    public double[] ka; // ambient
    public double[] kd; // diffuse
    public double[] ks; // specular
    public double[] kr; // reflection constant
    public double[] tr; // reflection constant

    public String name;
    public double ni;
    public double alpha = 100;

    public Material clone(){
        Material n = new Material();
        n.ka = this.ka;
        n.kd = this.kd;
        n.ks = this.ks;
        n.kr = this.kr;
        n.name = this.name;
        n.alpha = this.alpha;
        n.tr = this.tr;

        return n;
    }
}
