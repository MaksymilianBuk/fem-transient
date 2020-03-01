import calculation.Calc;
import calculation.Gauss;
import data.GlobalData;
import model.*;
import utilities.ElementUtilities;
import utilities.FileUtilities;
import utilities.Printing;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args)
    {
        System.out.println("\nMaksymilian Buk- FEM\nProject for FEM laboratories\nAGH University of Science and Technology\n\n");
        try {
            FileUtilities.loadFromTxtFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Error occurred with opening file");
            System.exit(0);
        }

        //Laboratory number 1- Initialize nodes and elements in grid
        Node [] nodes= ElementUtilities.initiateNodes();
        Element [] elements = ElementUtilities.initiateElements(nodes);
        Grid grid= new Grid(nodes,elements);

        //Printing nodes and elements (x,y etc).
        //System.out.println(grid.toString());
        //ElementUtilities.showAll(nodes, elements);


        //Laboratory number 2,3,5- Calculate H (with BC), C, P and aggregate

        //Matrix C
        Matrix [] matricesC= new Matrix[elements.length];
        for(int i=0; i<matricesC.length;i++)
        {
            matricesC[i]=Calc.calculateIntegralC(elements[i]);
        }
        double [][] aggregateC= Calc.matrixAggregation(matricesC,elements);


        //Matrix H
        Matrix [] matricesH= new Matrix[elements.length];
        for(int i=0;i<matricesH.length;i++)
        {
            //WITHOUT BC
            //matricesH[i]=Calc.calculateIntegralH(elements[i]);

            //WITH BC
            matricesH[i]=Calc.addMatrices(Calc.calculateIntegralH(elements[i]),Calc.calculateHBCOneElement(elements[i]));
        }
        double [][] aggregateH= Calc.matrixAggregation(matricesH,elements);

        //Vector P
        Vector [] vectorsP= new Vector[elements.length];
        for(int i=0;i<elements.length;i++)
        {
            vectorsP[i]=Calc.calculatePOneElement(elements[i]);
        }
        double[] aggregateP= Calc.vectorAggregation(vectorsP,elements);

        //Laboratory number 6- solve linear equations
        //Here we have 2 aggregated matrices (H and C) as double [][] and 1 vector (P) as double []
        // HGauss= HAggregate +(C/dt)
        // PGauss= PAggregate +(C/dt)*{T(i-1)}
        double [][] cDtGauss;
        double [][] HGauss;
        double [] PGauss;
        double [] initialTemperature;
        double[] temperatureVector;

        initialTemperature=ElementUtilities.initiateTemperature();
        temperatureVector=initialTemperature;

        //Printing 0[s] step
        Printing.printMaxMinWithIteration(0,Calc.getMinFromVector(initialTemperature),Calc.getMaxFromVector(initialTemperature));

        int numberOfSteps=(int)Math.floor(GlobalData.simulationTime/GlobalData.simulationStepTime);

        for(int i=1; i<=20;i++)
        {
            cDtGauss= Calc.multiplyOrDivideMatrix(aggregateC,GlobalData.simulationStepTime,false);
            HGauss= Calc.addMatrices(aggregateH,cDtGauss);
            PGauss= Calc.multiplyMatrixAndVector(cDtGauss,temperatureVector);
            PGauss=Calc.subtractVectorsSameDimension(PGauss,aggregateP);
            temperatureVector = Gauss.lsolve(HGauss,PGauss);
            Printing.printMaxMinWithIteration( (int)(i*GlobalData.simulationStepTime), Calc.getMinFromVector(temperatureVector),Calc.getMaxFromVector(temperatureVector));
        }
    }
}
