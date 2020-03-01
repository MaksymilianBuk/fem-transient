package model;

import calculation.Calc;

public class Matrix {
    public double[][] value;

    public Matrix() {
        this.value = new double[4][4];
    }

    @Override
    public String toString()
    {
        String temp="";
        temp+="----------------------------------------\n";
        temp+="Hello world! I'm Matrix\n\n";
        temp+="Values:\n";
        for(int i=0;i<4;i++)
        {
            temp+= this.value[i][0]+"\t\t"+this.value[i][1]+"\t\t"+this.value[i][2]+"\t\t"+this.value[i][3]+"\n";
        }
        return temp;
    }
}
