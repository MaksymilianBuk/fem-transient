package utilities;

import calculation.Calc;
import data.GlobalData;
import model.Element;
import model.Node;

public abstract class Printing {
    public static void printMatrix(double[][] matrix, String name)
    {
        String temp="";
        System.out.println("----------------------------- "+name+" -----------------------------");
        for(int i=0;i<matrix.length;i++) {
            for (int j = 0; j < matrix[0].length; j++)
            {
                temp+="\t "+ Calc.round(matrix[i][j],3);
            }
            System.out.println(temp);
            temp="";
        }
    }

    public static void printVector(double[] vector, String name)
    {
        String temp="";
        System.out.println("----------------------------- "+name+" -----------------------------");
        for(int i=0;i<vector.length;i++) {
                temp+="\t "+ Calc.round(vector[i],3);
                System.out.println(temp);
            temp="";
        }

    }

    public static void printMaxMinWithIteration(int step, double min, double max)
    {
        System.out.println("["+step+"] has min value: "+Calc.round(min,2)+" and max value: "+Calc.round(max,2));
    }

    //Function that print out Universal Element
    public static void printElement(Element element)
    {

        System.out.println("Hello! I'm element!");
        System.out.println("My wages are: "+ element.pc1.weight+ ", "+element.pc2.weight+ ", "+element.pc3.weight+ ", "+element.pc4.weight);

        System.out.println("------------");
        System.out.println("Global points: ");
        System.out.println("Point1: \t\t"+element.nodes[0].x+"\t\t"+element.nodes[0].y);
        System.out.println("Point1: \t\t"+element.nodes[1].x+"\t\t"+element.nodes[1].y);
        System.out.println("Point1: \t\t"+element.nodes[2].x+"\t\t"+element.nodes[2].y);
        System.out.println("Point1: \t\t"+element.nodes[3].x+"\t\t"+element.nodes[3].y);

        System.out.println("------------");
        System.out.println("Local points: ");
        System.out.println("Point1: \t\t"+element.pc1.E+"\t\t"+element.pc1.K);
        System.out.println("Point2: \t\t"+element.pc2.E+"\t\t"+element.pc2.K);
        System.out.println("Point3: \t\t"+element.pc3.E+"\t\t"+element.pc3.K);
        System.out.println("Point4: \t\t"+element.pc4.E+"\t\t"+element.pc4.K);

    }

    public static void showAllNodes(Node[] nodes)
    {
        System.out.println("<<< PRINTING ALL NODES >>>");
        for(int i = 0; i< GlobalData.nN; i++)
        {
            System.out.println(nodes[i].toString());
        }
    }

    public static void showAllElements(Element [] elements)
    {
        System.out.println("<<< PRINTING ALL ELEMENTS >>>");
        for(int i=0;i<GlobalData.nE;i++)
        {
            System.out.println(elements[i].toString());
        }
    }

    public static void showAll(Node [] nodes, Element [] elements)
    {
        showAllNodes(nodes);
        System.out.println();
        showAllElements(elements);
    }
}
