package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.koloboke.collect.map.LongIntCursor;

public class Euler446 {
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
	
	private static class RetractionCalculator	{
		public static long calculateF(DivisorHolder divs)	{
			long prod1=1;
			long prod2=1;
			for (LongIntCursor cursor=divs.getFactorMap().cursor();cursor.moveNext();)	{
				long q=pow(cursor.key(),cursor.value());
				prod1*=q+1;
				prod2*=q;
			}
			return prod1-prod2;
		}
		private final PrimeDecomposer decomposer;
		public RetractionCalculator(int limit)	{
			decomposer=new StandardPrimeDecomposer(limit);
		}
		public long calculateFor(long n)	{
			long nm=n-1;
			long np=n+1;
			DivisorHolder divs=decomposer.decompose(nm*nm+1);
			divs.combineDestructive(decomposer.decompose(np*np+1));
			return calculateF(divs);
		}
	}

	public static void main(String[] args)	{
		int N=1024;
		RetractionCalculator calculator=new RetractionCalculator(N);
		long result=0l;
		for (int i=1;i<=N;++i) result+=calculator.calculateFor(i);
		System.out.println(result);
	}
}
