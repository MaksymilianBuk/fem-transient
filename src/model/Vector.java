package model;

public class Vector {
    public double [] valueVector;

    public Vector() {
        this.valueVector=new double[4];
    }
    public Vector(double a,double b, double c, double d)
    {
        this.valueVector= new double[4];
        this.valueVector[0]=a;
        this.valueVector[1]=b;
        this.valueVector[2]=c;
        this.valueVector[3]=d;
    }
}
