package model;

import data.GlobalData;

public class Grid {
    public Node[] nodes;
    public Element [] elements ;

    public Grid(Node[] nodes, Element[] elements) {
        this.nodes = nodes;
        this.elements = elements;
    }

    @Override
    public String toString() {
        return "Grid. The grid is made of: "+ GlobalData.nE+ " elements which contains "+GlobalData.nN +" nodes. Height: "+GlobalData.H+" and length: "+GlobalData.L+"\n";
    }
}
