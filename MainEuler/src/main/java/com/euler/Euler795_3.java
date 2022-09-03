package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;

public class Euler795_3 {
	private final static long N=12345678;
	
	private static class GcdCounter implements Comparable<GcdCounter>	{
		public final long n;
		public long divisors;
		public GcdCounter(long n)	{
			this.n=n;
			divisors=0;
		}
		@Override
		public int compareTo(GcdCounter other)	{
			return Long.compare(n,other.n);
		}
	}
	
	private static class GcdIntervalCalculator	{
		private final long[] firstPrimes;
		private final long limit;
		public GcdIntervalCalculator(long limit)	{
			firstPrimes=Primes.firstPrimeSieve(limit);
			this.limit=limit;
		}
		public final long getSquareGcdSum(long n)	{
			long result=0;
			DivisorHolder primeDecomp=DivisorHolder.getFromFirstPrimes(n,firstPrimes);
			primeDecomp.powInPlace(2);
			List<GcdCounter> divisors=new ArrayList<>();
			for (LongCursor cursor=primeDecomp.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				if (d<=limit) divisors.add(new GcdCounter(d));
			}
			divisors.sort(null);
			long n_=n-1;
			for (int i=divisors.size()-1;i>=0;--i)	{
				GcdCounter elem=divisors.get(i);
				long exactDivisors=limit/elem.n-n_/elem.n;
				if (exactDivisors>0) for (int j=i+1;j<divisors.size();++j)	{
					GcdCounter other=divisors.get(j);
					if ((other.n%elem.n)==0) exactDivisors-=other.divisors;
				}
				elem.divisors=exactDivisors;
				result+=elem.n*exactDivisors;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0l;
		GcdIntervalCalculator calculator=new GcdIntervalCalculator(N);
		for (int i=1;i<=N;++i)	{
			long g=calculator.getSquareGcdSum(i);
			if ((i%2)==0) result+=g;
			else result-=g;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
