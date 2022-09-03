package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;

public class Euler659_5 {
	private final static int LIMIT=IntMath.pow(10,3);
	
	private final static int PRIME_LIMIT=IntMath.pow(10,7);
	
	private static long[] calculateBruteForce(int limit)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(PRIME_LIMIT);
		long[] result=new long[1+limit];
		for (int i=1;i<=limit;++i)	{
			DivisorHolder divisors=decomposer.decompose(4*i*i+1);
			long maxPrime=1l;
			for (LongCursor cursor=divisors.getFactorMap().keySet().cursor();cursor.moveNext();) maxPrime=Math.max(maxPrime,cursor.elem());
			result[i]=maxPrime;
		}
		return result;
	}
	
	private static long[] calculateWithAlgorithm(int limit)	{
		long[] result=new long[1+limit];
		for (int i=1;i<=limit;++i) if (result[i]==0)	{
			long p=4*i*i+1;
			for (int j=i;j<=limit;j+=p) result[j]=Math.max(result[j],p);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] bf=calculateBruteForce(LIMIT);
		long[] finesse=calculateWithAlgorithm(LIMIT);
		for (int i=1;i<=LIMIT;++i) if (bf[i]!=finesse[i]) System.out.println("Para "+i+" me sale "+finesse[i]+", pero es "+bf[i]+".");
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
