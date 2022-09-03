package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler403_3 {
	private final static long N=10000;
	
	private final static BigInteger B5=BigInteger.valueOf(5);
	private final static BigInteger B6=BigInteger.valueOf(6);
	
	private static BigInteger singleCakeValue(BigInteger x)	{
		BigInteger x3=x.multiply(x).multiply(x);
		return x3.add(B5.multiply(x)).add(B6).divide(B6);
	}
	
	private static long countEvenOccurrences(long min,long max)	{
		if ((min&1)==1) ++min;
		if ((max&1)==1) --max;
		if (min>max) return 0;
		else if (min==max) return (min==0)?1:2;	// Actually min==0 never happens... but let's be strict.
		return max-min+((min==0)?1:2);
	}
	
	private static long countOddOccurrences(long min,long max)	{
		if ((min&1)==0) ++min;
		if ((max&1)==0) --max;
		if (min>max) return 0;
		else if (min==max) return 2;
		return max-min+2;
	}
	
	private static long countOccurrences(long min,long max,boolean even)	{
		if (even) return countEvenOccurrences(min,max);
		else return countOddOccurrences(min,max);
	}
	
	public static void main(String[] args)	{
		BigInteger result=BigInteger.ZERO;
		long lim1=LongMath.sqrt(4*N,RoundingMode.DOWN);
		long lim2=LongMath.sqrt(N*N-4*N,RoundingMode.DOWN);
		long lim3=LongMath.sqrt(N*N+4*N,RoundingMode.DOWN);	// In practice it's always N+1.
		/*-
		for (long k=0;k<=lim1;++k)	{
			long maxA=LongMath.sqrt(k*k+4*N,RoundingMode.DOWN);
			BigInteger factor=BigInteger.valueOf(countOccurrences(0,maxA,(k&1)==0));
			System.out.println(String.format("k=%d => %s.",k,factor.toString()));
			result=result.add(factor.multiply(singleCakeValue(BigInteger.valueOf(k))));
		}
		for (long k=lim1+1;k<=lim2;++k)	{
			long minA=LongMath.sqrt(k*k-4*N,RoundingMode.UP);
			long maxA=LongMath.sqrt(k*k+4*N,RoundingMode.DOWN);
			BigInteger factor=BigInteger.valueOf(countOccurrences(minA,maxA,(k&1)==0));
			System.out.println(String.format("k=%d => %s.",k,factor.toString()));
			result=result.add(factor.multiply(singleCakeValue(BigInteger.valueOf(k))));
		}
		for (long k=lim2+1;k<=lim3;++k)	{
			long minA=LongMath.sqrt(k*k-4*N,RoundingMode.DOWN);
			long maxA=N;
			BigInteger factor=BigInteger.valueOf(countOccurrences(minA,maxA,(k&1)==0));
			System.out.println(String.format("k=%d => %s.",k,factor.toString()));
			result=result.add(factor.multiply(singleCakeValue(BigInteger.valueOf(k))));
		}
		*/
		for (long k=0;k<=lim3;++k)	{
			long minA=(k<=lim1)?0:LongMath.sqrt(k*k-4*N,RoundingMode.UP);
			long maxA=(k<=lim2)?LongMath.sqrt(k*k+4*N,RoundingMode.DOWN):N;
			BigInteger factor=BigInteger.valueOf(countOccurrences(minA,maxA,(k&1)==0));
			System.out.println(String.format("k=%d => a ∈    [%d,%d] (diff=%d) => %s.",k,minA,maxA,maxA-minA,factor.toString()));
			result=result.add(factor.multiply(singleCakeValue(BigInteger.valueOf(k))));
		}
		System.out.println(result);
	}
}
