package com.euler;

import java.util.Locale;

public class Euler286 {
	// I REALLY didn't expect to get this right at the first try :O.
	private final static int THROWS=50;
	private final static int HITS=20;
	
	private final static double PROBABILITY=0.02;
	private final static double TOL=1e-14;
	
	private static double getFailProb(int x,double q)	{
		return ((double)x)/q;
	}
	
	private static double calculate(double q)	{
		double[][] probs=new double[1+THROWS][1+HITS];
		probs[0][0]=1.0;
		for (int i=1;i<=THROWS;++i)	{
			double failProb=getFailProb(i,q);
			double hitProb=1-failProb;
			probs[i][0]=probs[i-1][0]*failProb;
			for (int j=1;j<=HITS;++j) probs[i][j]=failProb*probs[i-1][j]+hitProb*probs[i-1][j-1];
		}
		return probs[THROWS][HITS]-PROBABILITY;
	}
	
	public static double binarySearch(double qL,double qR)	{
		double valL=calculate(qL);
		double valR=calculate(qR);
		if (Math.signum(valL*valR)>0) throw new IllegalArgumentException("No.");
		for (;;)	{
			double qH=(qL+qR)/2.0;
			if ((qR-qH)<TOL) return qH;
			double valH=calculate(qH);
			if (Math.abs(valH)<TOL) return qH;
			if (Math.signum(valL*valH)>0)	{
				qL=qH;
				valL=valH;
			}	else	{
				qR=qH;
				valR=valH;
			}
		}
	}
	
	public static void main(String[] args)	{
		double left=50.0;
		double right=1e5;
		double result=binarySearch(left,right);
		System.out.println(String.format(Locale.UK,"%.10f",result));
	}
}
