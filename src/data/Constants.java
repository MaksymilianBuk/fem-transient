package data;

import static java.lang.Math.sqrt;

public abstract class Constants {

    public static double fractal(boolean positive)
    {
        if(positive)
            return (1/sqrt(3));
        else
            return ((-1)/sqrt(3));
    }
}
