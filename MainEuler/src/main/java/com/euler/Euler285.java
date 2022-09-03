package com.euler;

public class Euler285 {
	private final static int LIMIT=100000;
	
	private static double circleIntegral(double a,double r,int k)	{
		double r2=r*r;
		double a2=a*a;
		double sqDiff=r2-a2;
		return 0.5*(a*Math.sqrt(sqDiff)+r2*Math.atan(a/Math.sqrt(sqDiff)))-a/k;
	}
	
	private static double sq(double in)	{
		return in*in;
	}
	
	private static double getArea(int k)	{
		double areaSmall;
		double dk=(double)k;
		double ik=1.0/dk;
		if (k==1) areaSmall=0;
		else	{
			double rMinus=(dk-0.5)/dk;
			double infLimit=circleIntegral(ik,rMinus,k);
			double supLimit=circleIntegral(Math.sqrt(sq(rMinus)-sq(ik)),rMinus,k);
			areaSmall=supLimit-infLimit;
			/*
             area_1 k = integral (sqrt (((k-0.5)/k)^2 - (1/k)^2)) ((k-0.5)/k) k - integral (1/k) ((k-0.5)/k) k
             area_2 k = integral (sqrt (((k+0.5)/k)^2 - (1/k)^2)) ((k+0.5)/k) k - integral (1/k) ((k+0.5)/k) k
			 */
		}
		double rPlus=(dk+0.5)/dk;
		double infLimit=circleIntegral(ik,rPlus,k);
		double supLimit=circleIntegral(Math.sqrt(sq(rPlus)-sq(ik)),rPlus,k);
		double areaLarge=supLimit-infLimit;
		return areaLarge-areaSmall;
	}
	
	public static void main(String[] args)	{
		double sum=0.0;
		for (int i=1;i<=LIMIT;++i) sum+=i*getArea(i);
		System.out.println(sum);
	}
}
