package com.euler;

import com.euler.common.Timing;

public class Euler138 {
	private final static int GOAL=12;
	
	private static long solve()	{
		/*
		double s5=Math.sqrt(5d);
		double phi=(1+s5)/2;
		double phi6=Math.pow(phi,6);
		double phi9=Math.pow(phi,9);
		double v1=(Math.pow(phi,6*GOAL+9)-phi9)/(2*s5*(phi6-1));
		double phi_=1-phi;
		double phi_6=Math.pow(phi_,6);
		double phi_9=Math.pow(phi_,9);
		double v2=(Math.pow(phi_,6*GOAL+9)-phi_9)/(2*s5*(phi_6-1));
		return (long)Math.round(v1-v2);
		/*/
		long fprev=1;
		long fcurr=17;
		long result=fcurr;
		for (int i=2;i<=GOAL;++i)	{
			long f=18*fcurr-fprev;
			result+=f;
			fprev=fcurr;
			fcurr=f;
		}
		return result;
		//*/
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler138::solve);
	}
}
