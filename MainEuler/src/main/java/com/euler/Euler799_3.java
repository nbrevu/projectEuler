package com.euler;

import java.util.stream.LongStream;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;

public class Euler799_3 {
	private static class DivisorDecomposer	{
		private final long[] firstPrimes;
		private final long[] primes;
		public DivisorDecomposer(long limit)	{
			firstPrimes=Primes.firstPrimeSieve(limit);
			LongStream.Builder builder=LongStream.builder();
			boolean add4=false;
			for (int i=5;i<limit;i+=(add4?4:2),add4=!add4) if (firstPrimes[i]==0) builder.accept(i);
			primes=builder.build().toArray();
		}
		public DivisorHolder decompose(long c)	{
			long n=1+c*(18*c-6);
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
			if (n>1) result.addFactor(n,1);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		DivisorDecomposer decomposer1=new DivisorDecomposer(100_000_000);
		SumSquareDecomposer decomposer2=new SumSquareDecomposer();
		for (int i=1;i<50;++i)	{
			int ways=0;
			DivisorHolder divs=decomposer1.decompose(i);
			BaseSquareDecomposition baseSums=decomposer2.getFor(divs);
			for (LongPair p:baseSums.getBaseCombinations())	{
				long a=p.x+p.y;
				long b=p.y-p.x;
				if ((a%6==5)&&(b%6==5)) ++ways;
			}
			if (ways>0) System.out.println(String.format("P(%d) can be expresed in %d way(s).",i,ways));
		}
	}
}
