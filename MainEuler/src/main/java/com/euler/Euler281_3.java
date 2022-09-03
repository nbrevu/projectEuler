package com.euler;

import static com.euler.common.BigIntegerUtils.pow;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.BigIntegerUtils.BigFactorialCache;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler281_3 {
	private final static BigInteger LIMIT=pow(BigInteger.valueOf(10l),15);
	
	private static class InclusionExclusionTerms	{
		public final int[] toAdd;
		public final int[] toSubtract;
		public InclusionExclusionTerms(int[] toAdd,int[] toSubtract)	{
			this.toAdd=toAdd;
			this.toSubtract=toSubtract;
		}
	}
	
	private static IntObjMap<IntIntMap> getDivisorsLattice(int n,int[] firstPrimes)	{
		IntObjMap<IntIntMap> result=HashIntObjMaps.newMutableMap();
		LongIntMap factors=DivisorHolder.getFromFirstPrimes(n,firstPrimes).getFactorMap();
		result.put(1,HashIntIntMaps.newMutableMap());
		factors.forEach((long prime,int power)->	{
			Map<Integer,IntIntMap> toAdd=new HashMap<>();
			result.forEach((int number,IntIntMap existingFactors)->	{
				int currentValue=number;
				for (int i=1;i<=power;++i)	{
					currentValue*=prime;
					IntIntMap newFactors=HashIntIntMaps.newMutableMap(existingFactors);
					newFactors.put((int)prime,i);
					toAdd.put(currentValue,newFactors);
				}
			});
			result.putAll(toAdd);
		});
		return result;
	}
	
	private static int[] toArray(SortedSet<Integer> set)	{
		return set.stream().mapToInt(Integer::intValue).toArray();
	}
	
	private static SortedMap<Integer,InclusionExclusionTerms> getInclExclMap(int n,int[] firstPrimes)	{
		IntObjMap<IntIntMap> divisors=getDivisorsLattice(n,firstPrimes);
		SortedMap<Integer,InclusionExclusionTerms> result=new TreeMap<>(Collections.reverseOrder());
		divisors.forEach((int key,IntIntMap factors)->	{
			int[] primes=factors.keySet().toIntArray();
			// Inclusion-exclusion using some binary magic.
			int toIterate=1<<(primes.length);
			SortedSet<Integer> toAdd=new TreeSet<>(Collections.reverseOrder());
			SortedSet<Integer> toSubtract=new TreeSet<>(Collections.reverseOrder());
			for (int i=0;i<toIterate;++i)	{
				boolean isEvenWeight=true;
				int newValue=key;
				for (int j=0;j<primes.length;++j) if ((i&(1<<j))!=0)	{
					isEvenWeight=!isEvenWeight;
					newValue/=primes[j];
				}
				(isEvenWeight?toAdd:toSubtract).add(newValue);
			}
			result.put(key,new InclusionExclusionTerms(toArray(toAdd),toArray(toSubtract)));
		});
		return result;
	}
	
	private static class FCalculator	{
		private final BigFactorialCache factCache;
		private final int[] firstPrimes;
		private final Map<Pair<Integer,Integer>,BigInteger> opsCache;
		
		public FCalculator(int max)	{
			factCache=new BigFactorialCache(max);
			firstPrimes=Primes.firstPrimeSieve(max);
			opsCache=new HashMap<>();
		}
		private BigInteger computeFactorialFunction(Pair<Integer,Integer> pair)	{
			int m=pair.first;
			int n=pair.second;
			return factCache.get(m*n).divide(pow(factCache.get(n),m));
		}
		private BigInteger getFactorialFunction(int m,int n)	{
			return opsCache.computeIfAbsent(new Pair<>(m,n),this::computeFactorialFunction);
		}
		public BigInteger getF(int m,int n)	{
			BigInteger result=BigInteger.ZERO;
			for (Map.Entry<Integer,InclusionExclusionTerms> entry:getInclExclMap(n,firstPrimes).entrySet())	{
				BigInteger denominator=BigInteger.valueOf(m*entry.getKey());
				BigInteger numerator=BigInteger.ZERO;
				for (int toAdd:entry.getValue().toAdd) numerator=numerator.add(getFactorialFunction(m,toAdd));
				for (int toSubtract:entry.getValue().toSubtract) numerator=numerator.subtract(getFactorialFunction(m,toSubtract));
				result=result.add(numerator.divide(denominator));
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		// The argument should be about twice the logarithm of the limit, maybe a bit more.
		FCalculator calculator=new FCalculator(40);
		int howManyAddends=0;
		for (int n=1;;++n)	{
			int m=2;
			for (;;++m)	{
				BigInteger addend=calculator.getF(m,n);
				if (addend.compareTo(LIMIT)>0) break;
				++howManyAddends;
				result=result.add(addend);
			}
			if (m==2) break;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println(howManyAddends+" addends.");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
