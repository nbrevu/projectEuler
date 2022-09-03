package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler717_2 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	private static long[] getOddPrimesBelow(long limit)	{
		return Primes.listLongPrimes(limit).stream().skip(1).mapToLong(Long::longValue).toArray();
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] primes=getOddPrimesBelow(LIMIT);
		long result=0;
		for (long p:primes)	{
			long a=EulerUtils.expMod(2l,p,p-1);
			long r=EulerUtils.expMod(2l,a,p);
			// Some operations exceed 64 bits because they use mod p^2 where p>2^16, therefore p>2^32 and a size >2^64 is needed.
			BigInteger twoP=EulerUtils.expMod(BigInteger.TWO,p,BigInteger.valueOf(p*p));
			long pMinusOne=twoP.multiply(BigInteger.valueOf((p-1)/2)).add(BigInteger.ONE).divide(BigInteger.valueOf(p)).longValueExact()%p;
			long s=LongMath.divide((p-1)*r,2*p,RoundingMode.UP);
			long partialResult=(2*s-r*pMinusOne)%p;
			if (partialResult<0) partialResult+=p;
			result+=partialResult;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
