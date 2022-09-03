package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler335_2 {
	private final static long MOD=LongMath.pow(7l,9);
	private final static long N=LongMath.pow(10l,18);
	
	// 6*S(n) = 8*4^n - 9*3^n + 24*2^n - 11
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=8*EulerUtils.expMod(4,N,MOD)-9*EulerUtils.expMod(3,N,MOD)+24*EulerUtils.expMod(2,N,MOD)-11;
		result*=EulerUtils.modulusInverse(6l,MOD);
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
