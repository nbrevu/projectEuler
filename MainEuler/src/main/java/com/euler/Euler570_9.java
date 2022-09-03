package com.euler;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler570_9 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (long n=3;n<=LIMIT;++n)	{
			long exp=n-2;
			long secondArg=7*n+3;
			long firstArg=2*EulerUtils.expMod(4l,exp,secondArg)-EulerUtils.expMod(3l,exp,secondArg);
			firstArg=(firstArg+secondArg)%secondArg;
			result+=EulerUtils.gcd(firstArg,secondArg);
		}
		result*=6;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
