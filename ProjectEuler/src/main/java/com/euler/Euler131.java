package com.euler;

import com.euler.common.Primes;
import com.euler.common.Timing;

public class Euler131 {
	private final static int LIMIT=1000000;
	
	private static long solve()	{
		boolean[] composites=Primes.sieve(LIMIT);
		int count=0;
		int previousCube=1;
		for (int i=2;;++i)	{
			int cube=i*i*i;
			int diff=cube-previousCube;
			if (diff>=LIMIT) return count;
			if (!composites[diff]) ++count;
			previousCube=cube;
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler131::solve);
	}
}
