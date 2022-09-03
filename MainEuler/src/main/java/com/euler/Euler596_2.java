package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler596_2 {
	private final static long LIMIT=100000000l;
	private final static long MOD=1000000007l;
	
	private final static BigInteger TWO=BigInteger.valueOf(2l);
	
	private static BigInteger triangular(long n)	{
		BigInteger nBig=BigInteger.valueOf(n);
		return nBig.add(BigInteger.ONE).multiply(nBig).divide(TWO);
	}
	
	private static BigInteger modulusSum(long n)	{
		// This method computes sum(n mod k,k,1,n).
		BigInteger result=BigInteger.ZERO;
		long srn=LongMath.sqrt(n,RoundingMode.FLOOR);
		long currentMax=n;
		long currentMin=n/2+1;
		long q=1;
		long r=0;
		do	{
			long distance=(currentMax-currentMin);
			result=result.add(BigInteger.valueOf(q).multiply(triangular(distance))).add(BigInteger.valueOf(r*(distance+1)));
			r=n%(currentMin-1);
			++q;
			currentMax=currentMin-1;
			long nextNumber=n/(q+1);
			currentMin=nextNumber+1;
		}	while (q<srn);
		long smallResult=0;
		for (long l=1;l<=currentMax;++l) smallResult+=n%l;
		return result.add(BigInteger.valueOf(smallResult));
	}

	private static BigInteger sumOfSigma(long limit)	{
		BigInteger limitBig=BigInteger.valueOf(limit);
		return limitBig.multiply(limitBig).subtract(modulusSum(limit));
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long l2=LIMIT*LIMIT;
		BigInteger r8=BigInteger.valueOf(8).multiply(sumOfSigma(l2));
		BigInteger r32=BigInteger.valueOf(32).multiply(sumOfSigma(l2/4));
		BigInteger result=BigInteger.ONE.add(r8).subtract(r32);
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(MOD)));
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Calculated in "+seconds+" seconds.");
	}
}
