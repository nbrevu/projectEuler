package com.euler;

import com.google.common.math.IntMath;

public class Euler724_2 {
	private final static int N=IntMath.pow(10,8);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double dResult=0;
		double harmonic=1;
		double harmonic2=1;
		for (int i=2;i<=N;++i)	{
			dResult+=harmonic2;
			double inverse=1d/i;
			harmonic+=inverse;
			harmonic2+=inverse*inverse;
		}
		dResult+=N*harmonic*harmonic+harmonic;
		dResult*=0.5;
		long tac=System.nanoTime();
		long result=(long)Math.round(dResult);
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
