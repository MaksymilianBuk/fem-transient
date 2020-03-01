package utilities;

import data.GlobalData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class FileUtilities {
    public static void loadFromTxtFile() throws FileNotFoundException
    {
        String [] temp;
        File file = new File("res/mes.txt");
        Scanner in = new Scanner(file);

        //Set H
        String line = in.nextLine();
        temp=line.split(":");
        GlobalData.H=Double.parseDouble(temp[1].trim());

        //Set L
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.L=Double.parseDouble(temp[1].trim());

        //Set nH
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.nH=Integer.parseInt(temp[1].trim());

        //Set nL
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.nL=Integer.parseInt(temp[1].trim());

        //Set Alpha parameter
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.alpha=Double.parseDouble(temp[1].trim());

        //Set Density parameter
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.ro=Double.parseDouble(temp[1].trim());

        //Set Specific Heat parameter
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.cv=Double.parseDouble(temp[1].trim());

        //Set Conductivity
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.k=Double.parseDouble(temp[1].trim());

        //Set ambient temperature
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.ambientTemperature=Double.parseDouble(temp[1].trim());

        //Set initial temperature of material. Whole body has the same temperature on start.
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.initialTemperature=Double.parseDouble(temp[1].trim());

        //Set time of whole simulation process
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.simulationTime=Double.parseDouble(temp[1].trim());

        //Set time of steps in process
        line = in.nextLine();
        temp=line.split(":");
        GlobalData.simulationStepTime=Double.parseDouble(temp[1].trim());

        //Set total number of Nodes and Elements
        GlobalData.nN =GlobalData.nH*GlobalData.nL;
        GlobalData.nE=(GlobalData.nH-1)*(GlobalData.nL-1);

        //Set delta of X and Y
        GlobalData.dX= GlobalData.L/(GlobalData.nL-1);
        GlobalData.dY=GlobalData.H/(GlobalData.nH-1);



    }
}
