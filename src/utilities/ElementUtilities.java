package utilities;

import calculation.Calc;
import data.GlobalData;
import model.*;

import java.util.Random;

public abstract class ElementUtilities {

    //Initiate nodes values by GlobalData parameters
    public static Node [] initiateNodes()
    {
        Node[] nodes= new Node[GlobalData.nN];
        double tempX=0;
        double tempY=0;
        boolean tempBC;

        for(int i = 0; i< GlobalData.nN; i++)
        {

            if(tempX==0||tempY==0||(tempX+(0.5d*GlobalData.dX))>=GlobalData.L||(tempY+(0.5d*GlobalData.dY))>=GlobalData.H)
            {
                tempBC=true;
            }
            else
            {
                tempBC=false;
            }

            nodes[i]=new Node(i+1,tempX,tempY,GlobalData.initialTemperature,tempBC);

            tempY+=GlobalData.dY;
            if(tempY>=GlobalData.H)
            {
                tempX+=GlobalData.dX;
                tempY=0;
            }
        }
        return nodes;
    }

    static Node findNodeById(Node[] nodes, int id)
    {
        if(id>GlobalData.nN)
        {
            System.out.println("Node with ID: "+id+" not found!");
            return null;
        }
        for(int i = 0; i<GlobalData.nN; i++)
        {
            if(nodes[i].nodeId==id)
            {
                return nodes[i];
            }
        }
        return null;
    }

    public static Element[] initiateElements(Node[] nodes)
    {
        Element [] elements= new Element[GlobalData.nE];
        int tempId;
        int shift=0;
        for(int i=0;i<GlobalData.nE;i++)
        {
            tempId=i+1;
            if((i%(GlobalData.nH-1)==0)&&i!=0)
            {
                shift++;
            }

            elements[i]=new Element(tempId,
                    ElementUtilities.findNodeById(nodes,tempId+shift),
                    ElementUtilities.findNodeById(nodes,tempId+GlobalData.nH+shift),
                    ElementUtilities.findNodeById(nodes,tempId+GlobalData.nH+1+shift),
                    ElementUtilities.findNodeById(nodes,tempId+1+shift));
        }
        return elements;
    }

    public static Element findElementById(Element[] elements, int id)
    {
        if(id>GlobalData.nE)
        {
            System.out.println("Element with ID: "+id+" not found!");
            return null;
        }
        for(int i=0;i<GlobalData.nE;i++)
        {
            if(elements[i].elementId==id)
            {
                return elements[i];
            }
        }
        return null;
    }

    //Move local point into border (-1 or 1 value) and OVERWRITE position (!). Making copy of LocalPoints is necessarily
    public static void moveIntoBorder(LocalPoint previousLocal, LocalPoint nextLocal)
    {
        if(previousLocal.E==nextLocal.E)
        {
            if(previousLocal.E>0)
            {
                previousLocal.E=1.0d;
                nextLocal.E=1.0d;
            }
            else
            {
                previousLocal.E=-1.0d;
                nextLocal.E=-1.0d;
            }
        }
        else if(previousLocal.K==nextLocal.K)
        {
            if(previousLocal.K>0)
            {
                previousLocal.K=1.0d;
                nextLocal.K=1.0d;
            }
            else
            {
                previousLocal.K=-1.0d;
                nextLocal.K=-1.0d;
            }
        }
    }

    //Restore normal values for each integration point
    public static void loadUpBackup(LocalPoint previousPoint, LocalPoint nextPoint, double[] backup)
    {
        previousPoint.K=backup[0];
        previousPoint.E=backup[1];
        nextPoint.K=backup[2];
        nextPoint.E=backup[3];
    }

    //Save coordinates of two local points, as a backup for moving to border operation
    public static double[] backupPoints(LocalPoint previousPoint, LocalPoint nextPoint)
    {
        //Previous KSI, Previous ETA,   Next KSI,   Next ETA
        //0,            1,              2,          3
        double[] values= new double[4];
        values[0]=previousPoint.K;
        values[1]=previousPoint.E;
        values[2]=nextPoint.K;
        values[3]=nextPoint.E;
        return values;
    }

    //Init temperature
    public static double [] initiateTemperature()
    {
        double [] temp= new double[GlobalData.nN];
        for(int i=0; i<temp.length; i++)
        {
            temp[i]=GlobalData.initialTemperature;
        }
        return temp;
    }
}
