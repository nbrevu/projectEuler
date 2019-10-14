package com.euler;

import com.euler.common.Primes;
import com.euler.common.Timing;

public class Euler128 {
	private final static int GOAL=2000;
	
	private final static int PRIME_LIMIT=1000000;
	
	private static boolean allPrimes(boolean[] composites,long... ps)	{
		for (long p:ps) if (composites[(int)p]) return false;
		return true;
	}

	private static long solve()	{
		int count=2;	// 1 and 2.
		boolean[] composites=Primes.sieve(PRIME_LIMIT);
		for (long ring=1;;++ring)	{
			long r6=ring*6;
			if (composites[(int)(r6+5)]) continue;
			long r12=r6<<1;
			if (allPrimes(composites,r6+7,r12+17))	{
				++count;
				if (count>=GOAL) return 3*ring*(ring+1)+2;
			}
			if (allPrimes(composites,r12+5,r6+11))	{
				++count;
				if (count>=GOAL) return 3*(ring+1)*(ring+2)+1;
			}
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler128::solve);
	}
}
