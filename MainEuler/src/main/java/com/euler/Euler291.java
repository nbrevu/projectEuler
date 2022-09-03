package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.euler.common.Primes.RabinMiller;
import com.google.common.math.LongMath;

public class Euler291 {
	private final static long LIMIT=5*LongMath.pow(10l,15);
	
	public static void main(String[] args)	{
		RabinMiller appraiser=new RabinMiller();
		List<Integer> witnesses=Arrays.asList(2,3,5,7,11,13,17,19,23);
		long prevSq=1;
		int count=0;
		for (long n=2;;++n)	{
			if ((n%1000000)==0) System.out.println(""+n+"...");
			long curSq=n*n;
			long sum=prevSq+curSq;
			if (sum>LIMIT) break;
			if (appraiser.isPrime(BigInteger.valueOf(sum),witnesses)) ++count;
			prevSq=curSq;
		}
		System.out.println(count);
	}
}
