package data;

public abstract class GlobalData {

    //High and Length of grid
    public static double H,L;

    //Number of nodes Vertical(H) and Horizontal(L). Total number of nodes(nN) and elements(nE)
    public static int nH,nL,nN,nE;

    //Delta of X and Y axis
    public static double dX,dY;

    //Parameters of material
    public static double alpha, ro, cv, k;

    //Temperatures
    public static double ambientTemperature, initialTemperature;

    //Time
    public static double simulationTime, simulationStepTime;

}