package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler130 {
	private final static int GOAL=25;
	
	private final static int[] SIEVE=new int[] {1,7,11,13,17,19,23,29};
	private final static int PRIME_LIMIT=IntMath.pow(10,5);
	
	private static long solve()	{
		boolean[] composites=Primes.sieve(PRIME_LIMIT);
		long result=0;
		int count=0;
		for (int i=90;;i+=30) for (int d:SIEVE)	{
			int n=i+d;
			if (!composites[n]) continue;
			int m=EulerUtils.minRepunitDivisible(n);
			if ((n%m)==1)	{
				result+=n;
				++count;
				if (count>=GOAL) return result;
			}
			
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler130::solve);
	}
}
