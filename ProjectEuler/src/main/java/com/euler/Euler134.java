package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler134 {
	private final static int PRIME_LIMIT=IntMath.pow(10,6);
	
	private static long solve()	{
		long result=0;
		int[] primes=Primes.listIntPrimesAsArray(PRIME_LIMIT+100);
		long current10Pow=10;
		for (int i=3;i<primes.length;++i)	{
			if (primes[i-1]>PRIME_LIMIT) break;
			else if (primes[i-1]>current10Pow) current10Pow*=10;
			long pow10Inverse=EulerUtils.modulusInverse(current10Pow,primes[i]);
			long firstHalf=((primes[i]-primes[i-1])*pow10Inverse)%primes[i];
			result+=primes[i-1]+firstHalf*current10Pow;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler134::solve);
	}
}
