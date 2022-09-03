package com.euler;

import java.math.BigInteger;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.LongIntCursor;

public class Euler446_2 {
	private final static int N=IntMath.pow(10,7);
	private final static BigInteger MOD=BigInteger.valueOf(1_000_000_007l);
	
	/*
	 * n^4+4 = (n^4+4n^2+4) - 4n^2 = (n^2+2)^2 - (2n)^2 = (n^2-2n+2) * (n^2+2n+2) = ((n-1)^2+1) * ((n+1)^2+1).
	 * R(n) = (q1+1)*(q2+1)*...*(qk+1) - q1*q2*...qk	
	 */
	private static long pow(long base,int exp)	{
		long current=base;
		long prod=1;
		while (exp>0)	{
			if ((exp%2)==1) prod=(prod*current);
			current=(current*current);
			exp/=2;
		}
		return prod;
	}
	
	/*
	 * There is room for improvement here...
	6863172248191475192280817446455348
	907803852
	Elapsed 1437.8037833 seconds.
	 */
	private static class RetractionCalculator	{
		public static BigInteger calculateF(DivisorHolder divs)	{
			BigInteger prod1=BigInteger.ONE;
			BigInteger prod2=BigInteger.ONE;
			for (LongIntCursor cursor=divs.getFactorMap().cursor();cursor.moveNext();)	{
				long q=pow(cursor.key(),cursor.value());
				prod1=prod1.multiply(BigInteger.valueOf(q+1));
				prod2=prod2.multiply(BigInteger.valueOf(q));
			}
			return prod1.subtract(prod2);
		}
		private final PrimeDecomposer decomposer;
		public RetractionCalculator(int limit)	{
			decomposer=new StandardPrimeDecomposer(limit);
		}
		public BigInteger calculateFor(long n)	{
			long nm=n-1;
			long np=n+1;
			DivisorHolder divs=decomposer.decompose(nm*nm+1);
			divs.combineDestructive(decomposer.decompose(np*np+1));
			return calculateF(divs);
		}
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		RetractionCalculator calculator=new RetractionCalculator(N);
		BigInteger fullResult=BigInteger.ZERO;
		for (int i=1;i<=N;++i) fullResult=fullResult.add(calculator.calculateFor(i));
		BigInteger result=fullResult.mod(MOD);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
