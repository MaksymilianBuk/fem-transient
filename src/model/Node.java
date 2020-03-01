package model;

public class Node {
    public int nodeId;
    public double x;
    public double y;
    public double t;
    //Boundary condition- if true then on border. If false it's not
    public boolean bc;

    public Node(int nodeId, double x, double y, double t, boolean bc) {
        this.nodeId = nodeId;
        this.x = x;
        this.y = y;
        this.t = t;
        this.bc = bc;
    }

    @Override
    public String toString() {
        return "Node.\t ID:" +nodeId+ "\t\tX:" +x+ "\t\tY:" +y+ "\t\tBC:" +bc;
    }
}
