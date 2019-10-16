package com.euler;

import com.euler.common.Timing;

public class Euler137 {
	private final static int GOAL=15;
	
	private static long solve()	{
		double s5=Math.sqrt(5d);
		double phi=(1+s5)/2;
		double phi4=Math.pow(phi,4);
		double v1=(Math.pow(phi4,GOAL+1)-phi4)/(s5*phi*(phi4-1));
		return (long)Math.round(v1);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler137::solve);
	}
}
