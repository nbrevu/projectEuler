package com.euler;

import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler100 {
	private final static long LIMIT=LongMath.pow(10l,12);
	
	private static long solve()	{
		long aPrev=1;
		long aCurr=3;
		for (;;)	{
			long a=6*aCurr-aPrev-2;
			if (a>LIMIT) return aCurr;
			aPrev=aCurr;
			aCurr=a;
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler100::solve);
	}
}
