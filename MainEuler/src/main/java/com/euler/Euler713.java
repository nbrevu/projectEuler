package com.euler;

import com.google.common.math.LongMath;

public class Euler713 {
	private final static long N=LongMath.pow(10l,7);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (long i=2;i<=N;++i)	{
			long alpha=i-1;
			long m=N/alpha;
			long t=m*N-((m*(m+1))/2)*alpha;
			result+=t;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
