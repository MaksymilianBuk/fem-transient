package calculation;

import data.GlobalData;
import model.*;
import utilities.ElementUtilities;


public abstract class Calc {

    //Shape functions 2D
    public static double N1(LocalPoint localPoint, Derivative derivative)
    {
        if(derivative.equals(Derivative.NONE))
        {
            return (0.25d * (1 - localPoint.K) * (1 - localPoint.E));
        }
        else if(derivative.equals(Derivative.BY_ETA))
        {
            return (-0.25d * (1 - localPoint.K));
        }
        else
        {
            return (-0.25d *(1- localPoint.E));
        }
    }
    public static double N2(LocalPoint localPoint, Derivative derivative)
    {
        if(derivative.equals(Derivative.NONE))
        {
            return (0.25d * (1 + localPoint.K) * (1 - localPoint.E));
        }
        else if(derivative.equals(Derivative.BY_ETA))
        {
            return (-0.25d * (1 + localPoint.K));
        }
        else
        {
            return (0.25d *(1- localPoint.E));
        }
    }
    public static double N3(LocalPoint localPoint, Derivative derivative)
    {
        if(derivative.equals(Derivative.NONE))
        {
            return (0.25d * (1 + localPoint.K) * (1 + localPoint.E));
        }
        else if(derivative.equals(Derivative.BY_ETA))
        {
            return (0.25d * (1 + localPoint.K));
        }
        else
        {
            return (0.25d *(1+ localPoint.E));
        }
    }
    public static double N4(LocalPoint localPoint, Derivative derivative)
    {
        if(derivative.equals(Derivative.NONE))
        {
            return (0.25d * (1 - localPoint.K) * (1 + localPoint.E));
        }
        else if(derivative.equals(Derivative.BY_ETA))
        {
            return (0.25d * (1 - localPoint.K));
        }
        else
        {
            return (-0.25d *(1+ localPoint.E));
        }
    }

    public static double[] vectorAggregation(Vector[] vectors,Element [] elements)
    {
        if(vectors.length!=elements.length)
        {
            System.err.println("Cannot aggregate. Number of elements are different that number of vectors");
            return new double[]{0};
        }
        double [] result= new double[GlobalData.nN];

        for(int k=0;k<vectors.length;k++)
        {
            fillFinalVectorByElementVector(result,vectors[k],elements[k]);
        }
        return result;

    }

    public static void fillFinalVectorByElementVector(double[] finalVector, Vector elementVector, Element element)
    {
        for(int i=0;i<4;i++)
        {
            finalVector[element.nodes[i].nodeId-1]+=elementVector.valueVector[i];
        }
    }

    //Fulfill aggregation matrix by array of matrices and elements
    public static double[][] matrixAggregation(Matrix [] matrices, Element [] elements)
    {
        if(matrices.length!=elements.length)
        {
            System.err.println("Cannot aggregate. Number of elements are different that number of matrices");
            return new double[][]{{0},{0}};
        }

        double [][] result= new double[GlobalData.nN][GlobalData.nN];

        for(int k=0;k<matrices.length;k++)
        {
            fillFinalMatrixByElementMatrix(result,matrices[k],elements[k]);
        }
        return result;
    }

    //Add values from matrix of one point to aggregation matrix
    public static void fillFinalMatrixByElementMatrix(double[][] finalMatrix, Matrix elementMatrix, Element element)
    {
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                finalMatrix[element.nodes[i].nodeId-1][element.nodes[j].nodeId-1]+=elementMatrix.value[i][j];
            }
        }
    }

    //Calculate P Vector for one element
    public static Vector calculatePOneElement(Element element)
    {
        double [] resultVector= new double[]{0,0,0,0};

        if(Calc.checkBorder(element.nodes[0],element.nodes[1]))
        {
            resultVector=Calc.addVectorsSameDimension(resultVector,calculatePOneBorder(element.pc1,element.pc2,element.nodes[0],element.nodes[1]));
        }
        if(Calc.checkBorder(element.nodes[1],element.nodes[2]))
        {
            resultVector=Calc.addVectorsSameDimension(resultVector,calculatePOneBorder(element.pc2,element.pc3,element.nodes[1],element.nodes[2]));
        }
        if(Calc.checkBorder(element.nodes[2],element.nodes[3]))
        {
            resultVector=Calc.addVectorsSameDimension(resultVector,calculatePOneBorder(element.pc3, element.pc4,element.nodes[2],element.nodes[3]));
        }
        if(Calc.checkBorder(element.nodes[3],element.nodes[0]))
        {
            resultVector=Calc.addVectorsSameDimension(resultVector,calculatePOneBorder(element.pc4,element.pc1,element.nodes[3],element.nodes[0]));
        }
        Calc.multiplyVectorByValue(resultVector,-1d);

        return new Vector(resultVector[0],resultVector[1],resultVector[2],resultVector[3]);
    }

    //Calculate P Vector for one border that it is on edge of grid
    public static double[] calculatePOneBorder(LocalPoint previousLocal, LocalPoint nextLocal, Node previousNode, Node nextNode)
    {
        double [] savedPoints= ElementUtilities.backupPoints(previousLocal,nextLocal);
        double [] previousVector;
        double [] nextVector;

        ElementUtilities.moveIntoBorder(previousLocal,nextLocal);

        previousVector= new double[]{
                Calc.N1(previousLocal,Derivative.NONE),
                Calc.N2(previousLocal,Derivative.NONE),
                Calc.N3(previousLocal,Derivative.NONE),
                Calc.N4(previousLocal,Derivative.NONE)};

        nextVector= new double[]{
                Calc.N1(nextLocal,Derivative.NONE),
                Calc.N2(nextLocal,Derivative.NONE),
                Calc.N3(nextLocal,Derivative.NONE),
                Calc.N4(nextLocal,Derivative.NONE)};

        Calc.multiplyVectorByValue(previousVector,GlobalData.ambientTemperature);
        Calc.multiplyVectorByValue(nextVector,GlobalData.ambientTemperature);

        Calc.multiplyVectorByValue(previousVector,GlobalData.alpha);
        Calc.multiplyVectorByValue(nextVector, GlobalData.alpha);

        Calc.multiplyVectorByValue(previousVector,previousLocal.weight);
        Calc.multiplyVectorByValue(nextVector,nextLocal.weight);

        double det1D= 0.5d*Calc.fullDistanceBetweenNodes(previousNode,nextNode);

        Calc.multiplyVectorByValue(previousVector,det1D);
        Calc.multiplyVectorByValue(nextVector,det1D);

        //Return points to normal position
        ElementUtilities.loadUpBackup(previousLocal,nextLocal,savedPoints);

        return Calc.addVectorsSameDimension(previousVector,nextVector);
    }

    //Calculate H Matrix Boundary Condition for one element of grid
    public static Matrix calculateHBCOneElement(Element element)
    {
        Matrix resultMatrix= new Matrix();
        if(Calc.checkBorder(element.nodes[0],element.nodes[1]))
        {
            resultMatrix=Calc.addMatrices(resultMatrix,calculateHBCOneBorder(element.pc1,element.pc2,element.nodes[0],element.nodes[1]));
        }
        if(Calc.checkBorder(element.nodes[1],element.nodes[2]))
        {
            resultMatrix=Calc.addMatrices(resultMatrix,calculateHBCOneBorder(element.pc2,element.pc3,element.nodes[1],element.nodes[2]));
        }
        if(Calc.checkBorder(element.nodes[2],element.nodes[3]))
        {
            resultMatrix=Calc.addMatrices(resultMatrix,calculateHBCOneBorder(element.pc3, element.pc4,element.nodes[2],element.nodes[3]));
        }
        if(Calc.checkBorder(element.nodes[3],element.nodes[0]))
        {
            resultMatrix=Calc.addMatrices(resultMatrix,calculateHBCOneBorder(element.pc4,element.pc1,element.nodes[3],element.nodes[0]));
        }

        return resultMatrix;
    }

    //Calculate H Matrix Boundary Condition for one border that it is on edge of grid
    public static Matrix calculateHBCOneBorder(LocalPoint previousLocal, LocalPoint nextLocal, Node previousNode, Node nextNode)
    {
        double [] savedPoints= ElementUtilities.backupPoints(previousLocal,nextLocal);
        Matrix matrixLocalPrevious= new Matrix();
        Matrix matrixLocalNext= new Matrix();

        ElementUtilities.moveIntoBorder(previousLocal,nextLocal);

        matrixLocalPrevious=Calc.multiplySameVector4x4ToMatrix
                (new double[]{
                        Calc.N1(previousLocal,Derivative.NONE),
                        Calc.N2(previousLocal,Derivative.NONE),
                        Calc.N3(previousLocal,Derivative.NONE),
                        Calc.N4(previousLocal,Derivative.NONE)});

        matrixLocalNext=Calc.multiplySameVector4x4ToMatrix
                (new double[]{
                        Calc.N1(nextLocal,Derivative.NONE),
                        Calc.N2(nextLocal,Derivative.NONE),
                        Calc.N3(nextLocal,Derivative.NONE),
                        Calc.N4(nextLocal,Derivative.NONE)});

        matrixLocalPrevious=Calc.multiplyMatrixByValue(matrixLocalPrevious,GlobalData.alpha);
        matrixLocalNext=Calc.multiplyMatrixByValue(matrixLocalNext,GlobalData.alpha);

        matrixLocalPrevious=Calc.multiplyMatrixByValue(matrixLocalPrevious,previousLocal.weight);
        matrixLocalNext=Calc.multiplyMatrixByValue(matrixLocalNext,nextLocal.weight);

        double det1D= 0.5d*Calc.fullDistanceBetweenNodes(previousNode,nextNode);

        matrixLocalPrevious=Calc.multiplyMatrixByValue(matrixLocalPrevious,det1D);
        matrixLocalNext=Calc.multiplyMatrixByValue(matrixLocalNext,det1D);

        //Return points to normal position
        ElementUtilities.loadUpBackup(previousLocal,nextLocal,savedPoints);

        return Calc.addMatrices(matrixLocalNext,matrixLocalPrevious);
    }

    //Calculate detJ 1D as a distance between two nodes (used Pythagoras function)
    public static double fullDistanceBetweenNodes(Node previousNode, Node nextNode)
    {
        double tempX=(previousNode.x-nextNode.x)*(previousNode.x-nextNode.x);
        double tempY=(previousNode.y-nextNode.y)*(previousNode.y-nextNode.y);

        return Math.sqrt(tempX+tempY);
    }

    //Check if it's a border of grid
    public static boolean checkBorder(Node previous, Node next)
    {
        if(previous.bc)
        {
            if (next.bc)
            {
                return true;
            }
        }
        return false;
    }

    //Calculate integral of C Matrices of one element
    public static Matrix calculateIntegralC(Element element) {
        Matrix matrix= new Matrix();
        Matrix matrixTemp= new Matrix();
        for(int i=0;i<4;i++)
        {
            matrixTemp= Calc.calculateOnePointC(element,(i+1));
            matrix= Calc.addMatrices(matrix,matrixTemp);
        }
        return matrix;
    }

    //Calculate Matrix C for one specific point
    public static Matrix calculateOnePointC(Element element, int numberOfPoint)
    {
        Matrix matrix= new Matrix();
        matrix= Calc.multiplySameVector4x4ToMatrix(Calc.shapeFunctionVectorC(element,numberOfPoint));
        matrix= Calc.multiplyMatrixByValue(matrix, Calc.getWeightByNumberOfPoint(element,numberOfPoint));
        matrix= Calc.multiplyMatrixByValue(matrix, Calc.getWeightByNumberOfPoint(element,numberOfPoint));
        matrix= Calc.multiplyMatrixByValue(matrix, GlobalData.ro);
        matrix= Calc.multiplyMatrixByValue(matrix, GlobalData.cv);
        matrix= Calc.multiplyMatrixByValue(matrix,countDetJ(element,numberOfPoint));
        return matrix;
    }

    //Return weight of each point of element by number of point
    public static double getWeightByNumberOfPoint(Element element, int numberOfPoint)
    {
        switch (numberOfPoint)
        {
            case 1:
            {
                //return element.w1;
                return element.pc1.weight;
            }
            case 2:
            {
                //return element.w2;
                return element.pc2.weight;
            }
            case 3:
            {
                //return element.w3;
                return element.pc3.weight;
            }
            case 4:
            {
                //return element.w4;
                return element.pc4.weight;
            }
            default:
            {
                System.err.println("Error with parsing numberOfPoint to weight of point occurred");
                return 0;
            }
        }

    }

    //Calculate shape function vector 4x1 for one specific point (1-4)
    public static double[] shapeFunctionVectorC(Element element, int numberOfPoint)
    {
        double[] array= new double[4];
        LocalPoint localPoint= new LocalPoint();
        switch (numberOfPoint)
        {
            case 1:
            {
                localPoint=element.pc1;
                break;
            }
            case 2:
            {
                localPoint=element.pc2;
                break;
            }
            case 3:
            {
                localPoint=element.pc3;
                break;
            }
            case 4:
            {
                localPoint=element.pc4;
                break;
            }
            default:
            {
                System.err.println("Local point in shape function method in C Matrix error");
                break;
            }
        }
        array[0]= Calc.N1(localPoint,Derivative.NONE);
        array[1]= Calc.N2(localPoint,Derivative.NONE);
        array[2]= Calc.N3(localPoint,Derivative.NONE);
        array[3]= Calc.N4(localPoint,Derivative.NONE);
        return array;
    }

    //Calculate integral of H Matrices of one element
    public static Matrix calculateIntegralH(Element element) {
        Matrix matrix= new Matrix();
        Matrix matrixTemp;
        for(int i=0;i<4;i++)
        {
            matrixTemp=Calc.calculateOnePointH(element,(i+1));
            matrixTemp=Calc.multiplyMatrixByValue(matrixTemp,Calc.getWeightByNumberOfPoint(element,(i+1)));
            matrixTemp=Calc.multiplyMatrixByValue(matrixTemp,Calc.getWeightByNumberOfPoint(element,(i+1)));
            matrix=Calc.addMatrices(matrix,matrixTemp);
        }
        return matrix;
    }

    //Calculate matrix H for only one point
    public static Matrix calculateOnePointH(Element element, int numberOfPoint) {
        Matrix matrixXTemp= new Matrix();
        Matrix matrixYTemp= new Matrix();

        matrixXTemp=Calc.calculateMatrixDnForXorY(element,numberOfPoint,Coordinate.X);
        matrixYTemp=Calc.calculateMatrixDnForXorY(element,numberOfPoint,Coordinate.Y);

        //Right now we split Matrix into rows
        Matrix matrixX= new Matrix();
        Matrix matrixY= new Matrix();
        matrixX= multiplySameVector4x4ToMatrix(new double[]{matrixXTemp.value[(numberOfPoint-1)][0],matrixXTemp.value[(numberOfPoint-1)][1],matrixXTemp.value[(numberOfPoint-1)][2],matrixXTemp.value[(numberOfPoint-1)][3]});
        matrixY= multiplySameVector4x4ToMatrix(new double[]{matrixYTemp.value[(numberOfPoint-1)][0],matrixYTemp.value[(numberOfPoint-1)][1],matrixYTemp.value[(numberOfPoint-1)][2],matrixYTemp.value[(numberOfPoint-1)][3]});

        matrixX= Calc.multiplyMatrixByValue(matrixX,countDetJ(element,numberOfPoint));
        matrixY= Calc.multiplyMatrixByValue(matrixY,countDetJ(element,numberOfPoint));

        Matrix finalMatrix= new Matrix();
        finalMatrix= Calc.addMatrices(matrixX,matrixY);
        finalMatrix= Calc.multiplyMatrixByValue(finalMatrix,GlobalData.k);

        return finalMatrix;
    }

    //Calculates matrix for X or Y for each numberOfPoint
    public static Matrix calculateMatrixDnForXorY(Element element, int numberOfPoint, Coordinate coordinate) {
        Matrix matrix= new Matrix();
        double[] row1,row2,row3,row4;
        row1= new double[4];
        row2= new double[4];
        row3= new double[4];
        row4= new double[4];

        if(coordinate.equals(Coordinate.X))
        {
            row1= calculateDNRowH(element,numberOfPoint,Derivative.BY_X,element.pc1);
            row2= calculateDNRowH(element,numberOfPoint,Derivative.BY_X,element.pc2);
            row3= calculateDNRowH(element,numberOfPoint,Derivative.BY_X,element.pc3);
            row4= calculateDNRowH(element,numberOfPoint,Derivative.BY_X,element.pc4);
        }
        if (coordinate.equals(Coordinate.Y))
        {
            row1= calculateDNRowH(element,numberOfPoint,Derivative.BY_Y,element.pc1);
            row2= calculateDNRowH(element,numberOfPoint,Derivative.BY_Y,element.pc2);
            row3= calculateDNRowH(element,numberOfPoint,Derivative.BY_Y,element.pc3);
            row4= calculateDNRowH(element,numberOfPoint,Derivative.BY_Y,element.pc4);
        }

        matrix.value[0][0]=row1[0];
        matrix.value[0][1]=row1[1];
        matrix.value[0][2]=row1[2];
        matrix.value[0][3]=row1[3];
        matrix.value[1][0]=row2[0];
        matrix.value[1][1]=row2[1];
        matrix.value[1][2]=row2[2];
        matrix.value[1][3]=row2[3];
        matrix.value[2][0]=row3[0];
        matrix.value[2][1]=row3[1];
        matrix.value[2][2]=row3[2];
        matrix.value[2][3]=row3[3];
        matrix.value[3][0]=row4[0];
        matrix.value[3][1]=row4[1];
        matrix.value[3][2]=row4[2];
        matrix.value[3][3]=row4[3];

        return matrix;
    }

    //Calculate determinant of Jakoby Matrix
    public static double countDetJ(Element element, int numberOfPoint) {
        double det=0;
        double dxdksi,dydeta, dydksi, dxdeta;

        dxdksi=Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.X);
        dydeta=Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.Y);
        dydksi=Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.Y);
        dxdeta=Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.X);

        det= (dxdksi*dydeta) - (dydksi*dxdeta);
        return det;
    }

    /** The most basic (low level) function **/
    //Calculate like dx/deta if use Derivative.BY_ETA and Coordinate.X
    public static double calculateDerivatives(Element element, int numberOfPoint, Derivative derivative, Coordinate coordinate) {
        double temp = 0;
        double [] coordinates= new double[4];

        if(coordinate.equals(Coordinate.X))
        {
            coordinates[0]=element.nodes[0].x;
            coordinates[1]=element.nodes[1].x;
            coordinates[2]=element.nodes[2].x;
            coordinates[3]=element.nodes[3].x;
        }
        else if(coordinate.equals(Coordinate.Y))
        {
            coordinates[0]=element.nodes[0].y;
            coordinates[1]=element.nodes[1].y;
            coordinates[2]=element.nodes[2].y;
            coordinates[3]=element.nodes[3].y;
        }
        else
        {
            System.err.println("Something went wrong with setting coordinates!");
        }

        switch (numberOfPoint) {
            case 1:
            {
                temp += Calc.N1(element.pc1, derivative) * coordinates[0];
                temp += Calc.N2(element.pc1, derivative) * coordinates[1];
                temp += Calc.N3(element.pc1, derivative) * coordinates[2];
                temp += Calc.N4(element.pc1, derivative) * coordinates[3];
                break;
            }
            case 2:
            {
                temp += Calc.N1(element.pc2, derivative) * coordinates[0];
                temp += Calc.N2(element.pc2, derivative) * coordinates[1];
                temp += Calc.N3(element.pc2, derivative) * coordinates[2];
                temp += Calc.N4(element.pc2, derivative) * coordinates[3];
                break;
            }
            case 3:
            {
                temp += Calc.N1(element.pc3, derivative) * coordinates[0];
                temp += Calc.N2(element.pc3, derivative) * coordinates[1];
                temp += Calc.N3(element.pc3, derivative) * coordinates[2];
                temp += Calc.N4(element.pc3, derivative) * coordinates[3];
                break;
            }
            case 4:
            {
                temp += Calc.N1(element.pc4, derivative) * coordinates[0];
                temp += Calc.N2(element.pc4, derivative) * coordinates[1];
                temp += Calc.N3(element.pc4, derivative) * coordinates[2];
                temp += Calc.N4(element.pc4, derivative) * coordinates[3];
                break;
            }
            default:
            {
                break;
            }

    }

        return temp;
    }

    //Calculate all row like dN/dx for each pc
    public static double[] calculateDNRowH(Element element, int numberOfPoint, Derivative derivative, LocalPoint pc) {
        double []temp= new double[4];
        double oneOverDetJ=1./countDetJ(element,numberOfPoint);

        //If it's X value
        if(derivative.equals(Derivative.BY_X))
        {
            temp[0]=oneOverDetJ*((Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.Y)*Calc.N1(pc,Derivative.BY_KSI))+((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.Y)*Calc.N1(pc,Derivative.BY_ETA)));
            temp[1]=oneOverDetJ*((Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.Y)*Calc.N2(pc,Derivative.BY_KSI))+((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.Y)*Calc.N2(pc,Derivative.BY_ETA)));
            temp[2]=oneOverDetJ*((Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.Y)*Calc.N3(pc,Derivative.BY_KSI))+((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.Y)*Calc.N3(pc,Derivative.BY_ETA)));
            temp[3]=oneOverDetJ*((Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.Y)*Calc.N4(pc,Derivative.BY_KSI))+((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.Y)*Calc.N4(pc,Derivative.BY_ETA)));
        }
        if (derivative.equals(Derivative.BY_Y))
        {
            temp[0]=oneOverDetJ*(((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.X)*Calc.N1(pc,Derivative.BY_KSI))+(Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.X)*Calc.N1(pc,Derivative.BY_ETA)));
            temp[1]=oneOverDetJ*(((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.X)*Calc.N2(pc,Derivative.BY_KSI))+(Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.X)*Calc.N2(pc,Derivative.BY_ETA)));
            temp[2]=oneOverDetJ*(((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.X)*Calc.N3(pc,Derivative.BY_KSI))+(Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.X)*Calc.N3(pc,Derivative.BY_ETA)));
            temp[3]=oneOverDetJ*(((-1)*Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_ETA,Coordinate.X)*Calc.N4(pc,Derivative.BY_KSI))+(Calc.calculateDerivatives(element,numberOfPoint,Derivative.BY_KSI,Coordinate.X)*Calc.N4(pc,Derivative.BY_ETA)));
        }

        return temp;
    }

    //Add 4x4 matrix to another
    public static Matrix addMatrices(Matrix matrix1, Matrix matrix2){
        Matrix temp= new Matrix();

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                temp.value[i][j]=matrix1.value[i][j]+matrix2.value[i][j];
            }
        }
        return temp;
    }

    //Multiply 4x4 matrix by some value
    public static Matrix multiplyMatrixByValue(Matrix matrix, double value) {
        Matrix tempMatrix= new Matrix();
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tempMatrix.value[i][j]=matrix.value[i][j]*value;
            }
        }

        return tempMatrix;
    }

    //Multiply two vectors 4x1 (normal and transposition) to get matrix 4x4
    public static Matrix multiplySameVector4x4ToMatrix(double[] row)
    {
        Matrix temp= new Matrix();
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                temp.value[i][j]=row[i]*row[j];
            }
        }

        return temp;
    }

    //Multiply vector with any length by value in parameter
    public static void multiplyVectorByValue(double[] row, double value)
    {
        for(int i=0;i<row.length;i++)
        {
            row[i]=row[i]*value;
        }
    }

    //Add two vectors with the same dimension and return sum of these
    public static double[] addVectorsSameDimension(double [] previous, double [] next)
    {
        double[] temp= new double[previous.length];
        if(previous.length!=next.length)
        {
            System.err.println("This vectors aren't same dimension");
            return previous;
        }

        for(int i=0;i<previous.length;i++)
        {
            temp[i]=previous[i]+next[i];
        }
        return temp;
    }

    //Subtract vectors with same dimension and return difference
    public static double[] subtractVectorsSameDimension(double [] previous, double [] next)
    {
        double[] temp= new double[previous.length];
        if(previous.length!=next.length)
        {
            System.err.println("This vectors aren't same dimension");
            return previous;
        }

        for(int i=0;i<previous.length;i++)
        {
            temp[i]=previous[i]-next[i];
        }
        return temp;
    }

    //Multiply or Divide matrix 2D by values
    public static double [][] multiplyOrDivideMatrix(double [][] tab, double value, boolean multiplying)
    {
        double[][] temp= new double[tab.length][tab[0].length];
        if(multiplying)
        {
            for(int i=0;i<tab[0].length;i++)
            {
                for(int j=0;j<tab.length;j++)
                {
                    temp[i][j]=tab[i][j]*value;
                }
            }
        }
        else
        {
            for(int i=0;i<tab[0].length;i++)
            {
                for(int j=0;j<tab.length;j++)
                {
                    temp[i][j]=tab[i][j]/value;
                }
            }
        }
        return temp;
    }

    //Overload
    //Add two same dimension matrix
    public static double [][] addMatrices(double [][] matrix1, double [][] matrix2)
    {
        double[][] temp= new double[matrix1.length][matrix1[0].length];

        for(int i=0;i<matrix1.length;i++)
        {
            for(int j=0;j<matrix1[0].length;j++)
            {
                temp[i][j]=matrix1[i][j]+matrix2[i][j];
            }
        }
        return temp;
    }

    //Multiply matrix (n x n) with vector (1 x n) to get vector (1 x n)
    public static double[] multiplyMatrixAndVector(double[][] a, double[] x)
    {
        int m = a.length;
        int n = a[0].length;
        double[] temp = new double[m];

        for (int i = 0; i < m; i++)
        {
            for (int j = 0; j < n; j++)
            {
                temp[i] += a[i][j] * x[j];
            }
        }
        return temp;
    }

    public static double getMaxFromVector(double [] vector)
    {
        double max= vector[0];
        for(int i=0;i<vector.length;i++)
        {
            if(vector[i]>max)
            {
                max=vector[i];
            }
        }
        return max;
    }

    public static double getMinFromVector(double [] vector)
    {
        double min= vector[0];
        for(int i=0;i<vector.length;i++)
        {
            if(vector[i]<min)
            {
                min=vector[i];
            }
        }
        return min;
    }

    //Round value to n-places after comma
    public static double round(double value, double n)
    {
        value=value*Math.pow(10,n);
        value=Math.round(value);
        value=value/Math.pow(10,n);
        return value;
    }
}
