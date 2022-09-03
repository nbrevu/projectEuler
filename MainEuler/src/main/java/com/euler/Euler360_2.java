package com.euler;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler360_2 {
	private final static long N=LongMath.pow(10l,10);
	
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

	private static class SquareSumDecompositionCoordinateFinder	{
		private final long n;
		private final long[] firstPrimes;
		private final SumSquareDecomposer decomposer;
		private final LongSet allowed4k3Primes;
		public SquareSumDecompositionCoordinateFinder(long n)	{
			this.n=n;
			firstPrimes=Primes.firstPrimeSieve(2*n);
			DivisorHolder divsN=DivisorHolder.getFromFirstPrimes(n,firstPrimes);
			allowed4k3Primes=HashLongSets.newImmutableSet(divsN.getFactorMap().keySet().stream().filter((Long x)->(x%4)==3l).toArray(Long[]::new));
			decomposer=new SumSquareDecomposer();
		}
		public long getForDisk(long z)	{
			LongIntMap primes4k3=HashLongIntMaps.newMutableMap();
			LongIntMap primes4k1=HashLongIntMaps.newMutableMap();
			int p2=0;
			for (long nz:new long[] {n-z,n+z})	{
				DivisorHolder a=DivisorHolder.getFromFirstPrimes(nz,firstPrimes);
				for (LongIntCursor cursor=a.getFactorMap().cursor();cursor.moveNext();)	{
					long prime=cursor.key();
					int exp=cursor.value();
					if (prime==2l) p2+=exp;
					else if ((prime%4)==3)	{
						if (((exp%2)==0)||allowed4k3Primes.contains(prime)) primes4k3.addValue(prime,exp);
						else return 0l;	// Return early if we find a problematic prime and we can guarantee that it doesn't appear in the other number.
					}	else primes4k1.addValue(prime,exp);
				}
			}
			boolean doScramble=((p2%2)==1);  
			long extraFactor=1l<<(p2/2);
			for (LongIntCursor cursor=primes4k3.cursor();cursor.moveNext();)	{
				long prime=cursor.key();
				int exp=cursor.value();
				if ((exp%2)!=0) return 0;	// This can still happen if N is a multiple of a 4k+3 prime. Consider N=45, i=18.
				else extraFactor*=pow(prime,exp/2);
			}
			BaseSquareDecomposition decomp=decomposer.getFor(primes4k1);
			if (doScramble) decomp=decomp.scramble();
			long count=0;
			long horizontalValues=0;
			for (LongPair pair:decomp.getBaseCombinations())	{
				long sum=pair.x+pair.y;
				long repetitions=((pair.x==0)||(pair.x==pair.y))?4:8;
				count+=repetitions;
				horizontalValues+=sum*repetitions;
			}
			return 2*(horizontalValues*extraFactor+count*z);
		}
		public long getPoles()	{
			return 2*n;
		}
		public long getBaseDiskCases()	{
			DivisorHolder divsN=DivisorHolder.getFromFirstPrimes(n,firstPrimes);
			LongIntMap goodPrimes=HashLongIntMaps.newMutableMap();
			long extraFactor=1l;
			for (LongIntCursor cursor=divsN.getFactorMap().cursor();cursor.moveNext();)	{
				long prime=cursor.key();
				int exp=cursor.value();
				if ((prime%4)==1) goodPrimes.put(prime,exp*2);
				else extraFactor*=pow(prime,exp);
			}
			BaseSquareDecomposition decomp=decomposer.getFor(goodPrimes);
			long horizontalValues=0;
			// In the "base" disc z=0 so we don't need to count the occurrences.
			for (LongPair pair:decomp.getBaseCombinations())	{
				long sum=pair.x+pair.y;
				long repetitions=((pair.x==0)||(pair.x==pair.y))?4:8;
				horizontalValues+=sum*repetitions;
			}
			return horizontalValues*extraFactor;
		}
	}
	
	// It's SO NICE when I get a relatively complex problem right at the first try :). Also: this was my 600th!!!!!!
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long factor=1;
		long n=N;
		while ((n%4)==0)	{
			factor*=2;
			n/=2;
		}
		SquareSumDecompositionCoordinateFinder finder=new SquareSumDecompositionCoordinateFinder(n);
		long result=0l;
		for (long i=1;i<n;++i) result+=finder.getForDisk(i);
		result+=finder.getBaseDiskCases()+finder.getPoles();
		result*=factor;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
