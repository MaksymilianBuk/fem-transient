package model;

public class Element {

    public int elementId;
    //Global Points
    public Node [] nodes;
    //Local points
    public LocalPoint pc1,pc2,pc3,pc4;

    //LeftDown, RightDown, RightUp, LeftUp
    public Element(int elementId, Node ld, Node rd, Node ru, Node lu) {

        this.elementId = elementId;

        this.nodes= new Node[]{ld,rd,ru,lu};

        this.pc1=new LocalPoint("LD");
        this.pc2=new LocalPoint("RD");
        this.pc3=new LocalPoint("RU");
        this.pc4=new LocalPoint("LU");
    }

    @Override
    public String toString() {
        return "Element. \tID: "+elementId+"\t\tNodes IDs:\t["+nodes[0].nodeId +"\t"+nodes[1].nodeId+"\t"+nodes[2].nodeId +"\t"+nodes[3].nodeId +"]";
    }
}
