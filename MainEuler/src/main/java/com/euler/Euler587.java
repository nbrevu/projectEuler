package com.euler;

import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.sqrt;

public class Euler587 {
	// Total area of L section: (4-pi)/4=1-pi/4
	private final static double L_AREA=1-atan(1.0);
	private final static double PERCENT=0.1;
	private final static double GOAL_AREA=L_AREA*PERCENT/100.0;
	
	private static double getAreaForNCircles(long n)	{
		// The slope of the line is 1/N, so the line is y=x/N.
		// The circle is: (x-1)^2+(y-1)^2=1 -> y=1-sqrt(2x-x^2).
		// The contact point is C=N*(1+N-sqrt(2*N))/(1+N^2).
		// The triangular portion of the area is C^2/(2*N).
		// The "under the circle" portion is 1-c+asin(c-1)/2+(1-c)*sqrt(2*c-c^2)/2.
		double m=(double)n;
		double c=m*(1+m-sqrt(2*m))/(1+m*m);
		double a1=c*c/(2*m);
		double a2=1-c+asin(c-1)/2+(c-1)*sqrt(2*c-c*c)/2;
		return a1+a2;
	}
	
	public static void main(String[] args)	{
		for (long i=1;i<=1000000000l;++i)	{
			double a=getAreaForNCircles(i);
			if (a<GOAL_AREA)	{
				System.out.println(i);
				return;
			}
		}
	}
}
