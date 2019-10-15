package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler133 {
	private final static int PRIME_LIMIT=IntMath.pow(10,5);
	
	private static boolean canBeDivisorOfPowerOfTenRepunit(int n)	{
		int rep=EulerUtils.minRepunitDivisible(n);
		while ((rep%2)==0) rep/=2;
		while ((rep%5)==0) rep/=5;
		return rep==1;
	}
	
	private static long solve()	{
		boolean[] composites=Primes.sieve(PRIME_LIMIT);
		long result=10;	// 2+3+5.
		boolean add4=true;
		for (int i=7;i<PRIME_LIMIT;i+=(add4?4:2),add4=!add4) if (!composites[i]&&!canBeDivisorOfPowerOfTenRepunit(i)) result+=i;
		return result;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler133::solve);
	}
}
