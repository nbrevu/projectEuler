package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler354_2 {
	private final static long N=5*LongMath.pow(10l,11);
	
	private static void addPowers(SortedSet<Long> result,long value,long limit)	{
		// System.out.println("Añado el "+value+"...");
		List<Long> toAdd=new ArrayList<>();
		long maxValue=limit/value;
		for (long n:result) if (n>maxValue) break;
		else do	{
			n*=value;
			toAdd.add(n);
		} while (n<=maxValue);
		result.addAll(toAdd);
	}
	
	private static long[] getInertValues(boolean[] composites,long limit)	{
		SortedSet<Long> result=new TreeSet<>();
		result.add(1l);
		addPowers(result,3l,limit);
		addPowers(result,4l,limit);
		for (int i=5;i<composites.length;i+=6) if (!composites[i])	{
			long sq=i*(long)i;
			if (sq>limit) break;
			else addPowers(result,sq,limit);
		}
		return result.stream().mapToLong(Long::longValue).sorted().toArray();
	}
	
	private static long[] get6kPlus1Primes(boolean[] composites)	{
		List<Long> result=new ArrayList<>();
		for (int i=7;i<composites.length;i+=6) if (!composites[i]) result.add(Long.valueOf(i));
		return result.stream().mapToLong(Long::longValue).toArray();
	}
	
	private static class CombinationCounter	{
		private final static LongObjMap<IntObjMap<BigInteger>> POWERS_CACHE=HashLongObjMaps.newMutableMap();
		private final long[] inertValues;
		private final long[] relevantPrimes;
		private final int[] powerSeries;
		private final BigInteger limit;
		public CombinationCounter(long[] inertValues,long[] relevantPrimes,int[] powerSeries,BigInteger limit)	{
			this.inertValues=inertValues;
			this.relevantPrimes=relevantPrimes;
			this.powerSeries=powerSeries;
			this.limit=limit;
		}
		private static BigInteger getPower(long prime,int power)	{
			IntObjMap<BigInteger> thisPrimeCache=POWERS_CACHE.computeIfAbsent(prime,(long unused)->HashIntObjMaps.newMutableMap());
			return thisPrimeCache.computeIfAbsent(power,(int exp)->BigInteger.valueOf(prime).pow(exp));
		}
		public long count()	{
			LongSet usedPrimes=HashLongSets.newMutableSet();
			return countRecursive(limit,usedPrimes,0,0);
		}
		private long countRecursive(BigInteger upTo,LongSet usedPrimes,int powerIndex,int startIndex)	{
			int exponent=powerSeries[powerIndex];
			long result=0l;
			for (int i=startIndex;i<relevantPrimes.length;++i)	{
				long prime=relevantPrimes[i];
				if (usedPrimes.contains(prime)) continue;
				BigInteger power=getPower(prime,exponent);
				if (power.compareTo(upTo)>0) break;
				BigInteger remaining=upTo.divide(power);
				if (powerIndex==powerSeries.length-1) result+=countInertValues(remaining.longValueExact());
				else	{
					usedPrimes.add(prime);
					int nextIndex=(exponent==powerSeries[powerIndex+1])?(i+1):0;
					result+=countRecursive(remaining,usedPrimes,powerIndex+1,nextIndex);
					usedPrimes.removeLong(prime);
				}
			}
			return result;
		}
		public int countInertValues(long value)	{
			int position=Arrays.binarySearch(inertValues,value);
			return (position>=0)?(1+position):(-1-position);
		}
	}
	
	private static long sq(long in)	{
		return in*in;
	}
	private static long to4(long in)	{
		return sq(sq(in));
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger bigL=BigInteger.valueOf(N);
		BigInteger l2=bigL.multiply(bigL).divide(BigInteger.valueOf(3l));
		long limit1=to4(7*13);
		long limit2=limit1*sq(19);
		int primeLimit=l2.divide(BigInteger.valueOf(limit1)).sqrt().intValueExact();
		long inertLimit=l2.divide(BigInteger.valueOf(limit2)).longValueExact();
		boolean[] composites=Primes.sieve(primeLimit);
		long[] inertValues=getInertValues(composites,inertLimit);
		long[] relevantPrimes=get6kPlus1Primes(composites);
		CombinationCounter counter3=new CombinationCounter(inertValues,relevantPrimes,new int[] {4,4,2},l2);
		CombinationCounter counter2=new CombinationCounter(inertValues,relevantPrimes,new int[] {14,4},l2);
		CombinationCounter counter2x=new CombinationCounter(inertValues,relevantPrimes,new int[] {24,2},l2);
		long result=counter3.count()+counter2.count()+counter2x.count();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
