package model;

import data.Constants;

public class LocalPoint {
    public double E,K;
    public double weight;
    //ETA and KSI

    public LocalPoint()
    {
    }

    public LocalPoint(double e, double k) {
        E = e;
        K = k;
        weight=1;
    }

    public LocalPoint(String point)
    {
        switch(point)
        {
            case "LD": {
                //For left-down node
                this.K= Constants.fractal(false);
                this.E= Constants.fractal(false);
                break;
            }
            case "RD":
            {
                //For right-down node
                this.K= Constants.fractal(true);
                this.E= Constants.fractal(false);
                break;
            }
            case "RU":
            {
                //For right-upper node
                this.K= Constants.fractal(true);
                this.E= Constants.fractal(true);
                break;
            }
            case "LU":
            {
                //For left-upper node
                this.K= Constants.fractal(false);
                this.E= Constants.fractal(true);
                break;
            }
        }
        //From -1 to 1 is 2 length and it's sum of weights on each side so each weight=1 (2D Element).
        weight=1;
    }

}
