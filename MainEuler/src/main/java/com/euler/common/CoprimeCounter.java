package com.euler.common;

import java.util.BitSet;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class CoprimeCounter	{
	private final static BitSet[] BITSET_CACHE=createBitSetCache(10);	// I believe that 7 would be enough.
	private static BitSet[] createBitSetCache(int maxBits)	{
		int size=1<<maxBits;
		long[] buffer=new long[1];
		BitSet[] result=new BitSet[size];
		for (int i=0;i<size;++i)	{
			buffer[0]=i;
			result[i]=BitSet.valueOf(buffer);
		}
		return result;
	}
	private final int[] firstPrimes;
	public CoprimeCounter(int limit)	{
		firstPrimes=Primes.firstPrimeSieve(limit);
	}
	private int[] getPrimeFactors(int n)	{
		// if (n==1) return new int[0];
		IntSet result=HashIntSets.newMutableSet();
		for (;;)	{
			int prime=firstPrimes[n];
			if (prime==0)	{
				result.add(n);
				break;
			}	else	{
				result.add(prime);
				n/=prime;
			}
		}
		return result.toIntArray();
	}
	private int compose(int[] primes,BitSet indices)	{
		int result=1;
		for (int i=indices.nextSetBit(0);i>=0;i=indices.nextSetBit(i+1)) result*=primes[i];
		return result;
	}
	public long countCoprimes(int n,long infLimit,long supLimit)	{
		if (n==1) return supLimit-infLimit;
		if (infLimit>=supLimit) return 0;
		int[] primes=getPrimeFactors(n);
		int cases=1<<primes.length;
		int result=0;
		for (int i=0;i<cases;++i)	{
			BitSet primesToTake=BITSET_CACHE[i];
			int divisor=compose(primes,primesToTake);
			long count=supLimit/divisor-infLimit/divisor;
			if ((primesToTake.cardinality()%2)==0) result+=count;
			else result-=count;
		}
		return result;
	}
}