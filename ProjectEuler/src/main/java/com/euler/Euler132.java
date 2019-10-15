package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler132 {
	private final static int N=IntMath.pow(10,9);
	private final static int GOAL=40;
	
	private final static int PRIME_LIMIT=IntMath.pow(10,6);
	
	private static long solve()	{
		boolean[] composites=Primes.sieve(PRIME_LIMIT);
		long result=0;
		int count=0;
		boolean add4=false;
		for (int i=11;;i+=(add4?4:2),add4=!add4) if (!composites[i])	{
			int nRep=EulerUtils.minRepunitDivisible(i);
			if (N%nRep==0)	{
				System.out.println((1+count)+": "+i+", "+nRep+".");
				result+=i;
				++count;
				if (count>=GOAL) return result;
			}
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler132::solve);
	}
}
