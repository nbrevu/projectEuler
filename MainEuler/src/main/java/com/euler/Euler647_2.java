package com.euler;

import java.math.BigInteger;

import com.google.common.math.LongMath;

public class Euler647_2 {
	private final static BigInteger LIMIT=BigInteger.TEN.multiply(square(BigInteger.valueOf(LongMath.pow(10l,6))));
	
	private final static BigInteger TWO=BigInteger.ONE.add(BigInteger.ONE);
	
	private static BigInteger square(BigInteger in)	{
		return in.multiply(in);
	}
	
	private static BigInteger sum(int k,BigInteger limit)	{
		BigInteger result=BigInteger.ZERO;
		/*
		 * A = ((2k-4)n+1)^2
		 * B = ((k-4)^2*((k-2)*n^2+n))/2
		 */
		BigInteger coefA=BigInteger.valueOf(2*k-4);
		BigInteger coefB1=square(BigInteger.valueOf(k-4));
		BigInteger coefB2=BigInteger.valueOf(k-2);
		for (BigInteger n=BigInteger.ONE;;n=n.add(BigInteger.ONE))	{
			BigInteger A=square(coefA.multiply(n).add(BigInteger.ONE));
			BigInteger B=coefB1.multiply(coefB2.multiply(square(n)).add(n).divide(TWO));
			if ((A.compareTo(limit)<=0)&&(B.compareTo(limit)<=0)) result=result.add(A.add(B));
			else break;
		}
		return result;
	}
	
	private static BigInteger sumAll(BigInteger limit)	{
		BigInteger result=BigInteger.ZERO;
		for (int k=3;;k+=2)	{
			BigInteger thisSum=sum(k,limit);
			if (thisSum.equals(BigInteger.ZERO)) break;
			result=result.add(thisSum);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=sumAll(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
