package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler123 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private final static int PRIME_LIMIT=IntMath.pow(10,6);
	
	private static long solve()	{
		int[] primes=Primes.listIntPrimesAsArray(PRIME_LIMIT);
		int minimum=(int)LongMath.sqrt(LIMIT,RoundingMode.DOWN);
		int index=Arrays.binarySearch(primes,minimum);
		if (index<0) index=-index;
		if ((index%2)==1) ++index;
		for (;;index+=2)	{
			long p=primes[index];
			long rem=(2*(index+1))%p;
			if (rem*p>LIMIT)	{
				return index+1;
			}
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler123::solve);
	}
}
