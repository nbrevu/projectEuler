package com.euler;

import java.util.stream.LongStream;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.koloboke.collect.IntCursor;

public class Euler799_5 {
	private final static int N=100;
	
	private final static long MAX_PRIME=1_000_000;
	
	private static class DivisorDecomposer	{
		private final long[] firstPrimes;
		private final long[] primes;
		public DivisorDecomposer(long limit)	{
			firstPrimes=Primes.firstPrimeSieve(limit);
			LongStream.Builder builder=LongStream.builder();
			for (int i=5;i<limit;i+=4) if (firstPrimes[i]==0) builder.accept(i);
			primes=builder.build().toArray();
		}
		public DivisorHolder decompose(long c)	{
			long n=1+c*(18*c-6);
			if ((n%32045)!=0) return null;
			DivisorHolder result=new DivisorHolder();
			/*
			result.addFactor(2l,3);
			result.addFactor(3l,2);
			*/
			if (n<firstPrimes.length)	{
				result.addFromFirstPrimes(n,firstPrimes);
				return result;
			}
			for (int i=0;i<primes.length;++i)	{
				long p=primes[i];
				if ((n%p)==0)	{
					int exp=0;
					do	{
						n/=p;
						++exp;
					}	while ((n%p)==0);
					result.addFactor(p,exp);
					if (n<firstPrimes.length)	{
						result.addFromFirstPrimes(n,firstPrimes);
						return result;
					}
				}
			}
			return null;	// We are not going to get anything from this if there is such a big prime.
		}
	}
	
	private static int estimateCases(DivisorHolder div)	{
		int pow=1;
		for (IntCursor cursor=div.getFactorMap().values().cursor();cursor.moveNext();) pow*=1+(cursor.elem()/2);
		pow*=1<<(div.getFactorMap().size()-1);
		return pow;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DivisorDecomposer decomposer1=new DivisorDecomposer(MAX_PRIME);
		SumSquareDecomposer decomposer2=new SumSquareDecomposer();
		for (int i=1;;++i)	{
			int ways=0;
			DivisorHolder divs=decomposer1.decompose(i);
			if ((divs==null)||(estimateCases(divs)<=N)) continue;
			BaseSquareDecomposition baseSums=decomposer2.getFor(divs);
			if (baseSums.getBaseCombinations().size()<=N) continue;
			for (LongPair p:baseSums.getBaseCombinations())	{
				long a=p.x+p.y;
				long b=p.y-p.x;
				if ((a%6==5)&&(b%6==5)) ++ways;
			}
			if (ways>N)	{
				long result=(i*(long)(3*i-1))/2;
				System.out.println(ways);
				System.out.println(i);
				System.out.println(result);
				break;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
