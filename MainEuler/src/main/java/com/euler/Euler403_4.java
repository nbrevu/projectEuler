package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler403_4 {
	private final static long N=LongMath.pow(10l,12);
	private final static long MOD=LongMath.pow(10l,8);
	
	private final static BigInteger B4=BigInteger.valueOf(4);
	private final static BigInteger B5=BigInteger.valueOf(5);
	private final static BigInteger B6=BigInteger.valueOf(6);
	
	private static BigInteger cakeSum(BigInteger x)	{
		BigInteger triangular=x.add(BigInteger.ONE).multiply(x).shiftRight(1);
		return triangular.multiply(triangular.add(B5)).divide(B6).add(x);
	}
	
	private static BigInteger cakeSum(long min,long max)	{
		return cakeSum(BigInteger.valueOf(max)).subtract(cakeSum(BigInteger.valueOf(min-1)));
	}
	
	private static BigInteger singleCakeValue(BigInteger x)	{
		BigInteger x3=x.multiply(x).multiply(x);
		return x3.add(B5.multiply(x)).add(B6).divide(B6);
	}
	
	private static long countEvenOccurrences(long min,long max)	{
		if ((min&1)==1) ++min;
		if ((max&1)==1) --max;
		if (min>max) return 0;
		else if (min==max) return (min==0)?1:2;
		return max-min+((min==0)?1:2);
	}
	
	private static long countOddOccurrences(long min,long max)	{
		if ((min&1)==0) ++min;
		if ((max&1)==0) --max;
		if (min>max) return 0;
		else if (min==max) return 2;
		return max-min+2;
	}
	
	private static long countOccurrencesWithParity(long min,long max,boolean even)	{
		if (even) return countEvenOccurrences(min,max);
		else return countOddOccurrences(min,max);
	}
	
	private static long countAllOccurrences(long deltaLeft,long deltaRight)	{
		return ((deltaLeft>>1)+(deltaRight>>1)+1)<<1;
	}
	
	/*
	 * 263720538952078758083690477915007981298118224771
	 * 18224771
	 * 
	 * Stupid overflows. But in the end I got it right :).
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger fullResult=BigInteger.ZERO;
		long lim1=LongMath.sqrt(4*N,RoundingMode.DOWN);
		BigInteger bigN=BigInteger.valueOf(N);
		long lim2=bigN.multiply(bigN.subtract(B4)).sqrt().longValueExact();
		long lim3=bigN.multiply(bigN.add(B4)).sqrt().longValueExact();
		// First portion.
		for (long k=0;k<=lim1;++k)	{
			long maxA=LongMath.sqrt(k*k+4*N,RoundingMode.DOWN);
			BigInteger factor=BigInteger.valueOf(countOccurrencesWithParity(0,maxA,(k&1)==0));
			fullResult=fullResult.add(factor.multiply(singleCakeValue(BigInteger.valueOf(k))));
		}
		// Second portion (ZUTUN, here be dragons).
		{
			long k0=1+lim1;
			long kk=k0*k0;
			long minA=LongMath.sqrt(kk-4*N,RoundingMode.UP);
			long maxA=LongMath.sqrt(kk+4*N,RoundingMode.DOWN);
			int deltaLeft=(int)(k0-minA);
			int deltaRight=(int)(maxA-k0);
			long[] leftLimits=new long[1+deltaLeft];
			long[] rightLimits=new long[1+deltaRight];
			for (int i=1;i<leftLimits.length;++i) leftLimits[i]=(4*N+i*(long)i)/(2*i);
			for (int i=1;i<rightLimits.length;++i) rightLimits[i]=(4*N-i*(long)i)/(2*i);
			while (k0<=lim2)	{
				long kF=Math.min(leftLimits[deltaLeft],rightLimits[deltaRight]);
				kF=Math.min(kF,lim2);
				long counter=countAllOccurrences(deltaLeft,deltaRight);
				BigInteger baseResult=cakeSum(k0,kF);
				fullResult=fullResult.add(baseResult.multiply(BigInteger.valueOf(counter)));
				k0=1+kF;
				while (leftLimits[deltaLeft]<=kF) --deltaLeft;
				while (rightLimits[deltaRight]<=kF) --deltaRight;
			}
		}
		// Last portion.
		for (long k=lim2+1;k<=lim3;++k)	{
			BigInteger bigK=BigInteger.valueOf(k);
			long minA=bigK.multiply(bigK).subtract(BigInteger.valueOf(4*N)).sqrt().longValueExact();
			long maxA=N;
			BigInteger factor=BigInteger.valueOf(countOccurrencesWithParity(minA,maxA,(k&1)==0));
			fullResult=fullResult.add(factor.multiply(singleCakeValue(BigInteger.valueOf(k))));
		}
		BigInteger result=fullResult.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
