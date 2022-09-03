package com.euler;

public class Euler770_5 {
	// The truly good solution comes from Euler770_4, but this should also be fast and reasonably precise.
	// 12732268082 for 1.99999.
	// 1271967 for 1.999.
	// 509270354 for 1.99995.
	private final static double GOAL=1.9999;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double x=2/GOAL-1;
		double f=0.5;
		long n;
		for (n=2;;++n)	{
			double nn=2*n;
			f*=1-1/nn;
			if (f<=x) break;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(n);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
