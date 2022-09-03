package com.euler;

import java.util.Locale;

public class Euler695 {
	private final static int POINTS=100;
	
	/*
	 * This is not right, the result is closer to 0.10 but I'm getting 0.13.
	 * "0.1361028037" (wrong).
	 * Elapsed 1321.0971421000002 seconds.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double[] p=new double[POINTS];
		for (int i=0;i<POINTS;++i) p[i]=((2d*i+1d)/(2d*POINTS));
		double sum=0d;
		for (int x1=0;x1<POINTS;++x1) for (int x2=0;x2<POINTS;++x2)	{
			double x12=Math.abs(p[x2]-p[x1]);
			for (int y1=0;y1<POINTS;++y1) for (int y2=0;y2<POINTS;++y2)	{
				double y12=Math.abs(p[y2]-p[y1]);
				double a12=x12*y12;
				for (int x3=0;x3<POINTS;++x3)	{
					double x13=Math.abs(p[x3]-p[x1]);
					double x23=Math.abs(p[x3]-p[x2]);
					for (int y3=0;y3<POINTS;++y3)	{
						double y13=Math.abs(p[y3]-p[y1]);
						double a13=x13*y13;
						double y23=Math.abs(p[y3]-p[y2]);
						double a23=x23*y23;
						if ((a12<=a13)&&(a13<=a23)) sum+=a13;
						else if ((a12<=a23)&&(a23<=a13)) sum+=a23;
						else sum+=a12;
					}
				}
			}
		}
		double denom=Math.pow(1d/POINTS,6);
		double result=sum*denom;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.10f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
