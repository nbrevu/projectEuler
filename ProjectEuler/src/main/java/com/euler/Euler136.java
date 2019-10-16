package com.euler;

import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler136 {
	private final static int LIMIT=50*IntMath.pow(10,6);
	
	private static long solve()	{
		int result=2;
		boolean[] composites=Primes.sieve(LIMIT);
		int l1=LIMIT/16;
		int l2=LIMIT/4;
		for (int i=3;i<=l1;i+=2) if (!composites[i]) result+=2;
		for (int i=l1+(((l1&1)==1)?2:1);i<=l2;i+=2) if (!composites[i]) ++result;
		for (int i=3;i<=LIMIT;i+=4) if (!composites[i]) ++result;
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler136::solve);
	}
}
