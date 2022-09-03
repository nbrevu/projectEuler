package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler717 {
	private final static long LIMIT=LongMath.pow(10l,2);
	
	private static long[] getOddPrimesBelow(long limit)	{
		return Primes.listLongPrimes(limit).stream().skip(1).mapToLong(Long::longValue).toArray();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=getOddPrimesBelow(LIMIT);
		long result=0;
		for (long p:primes)	{
			long a=EulerUtils.expMod(2l,p,p-1);
			BigInteger bigP=BigInteger.valueOf(p);
			BigInteger twoP=BigInteger.TWO.pow((int)p);
			BigInteger twoA=BigInteger.valueOf(EulerUtils.expMod(2l,a,p));
			BigInteger q=bigP.modInverse(twoP);
			long m=(twoP.subtract(q)).multiply(twoA).mod(twoP).mod(bigP).longValueExact();
			System.out.println(p+": "+m+".");
			result+=m;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		/*
		1603036763131
		Elapsed 7539.040817918 seconds.
		JAJA SI.
		 */
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
