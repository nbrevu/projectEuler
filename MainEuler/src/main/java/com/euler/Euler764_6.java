package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler764_6 {
	private final static BigInteger LIMIT=BigInteger.TEN.pow(7);
	
	private final static int LIMIT_Y=LIMIT.sqrt().intValueExact();
	
	private static class DecompositionInfo	{
		private final BigInteger commonFactor;
		private final List<BigInteger> alternatingFactors;
		public DecompositionInfo(BigInteger commonFactor,List<BigInteger> alternatingFactors)	{
			this.commonFactor=commonFactor;
			this.alternatingFactors=alternatingFactors;
		}
	}
	
	private static class SimplifiedTuple	{
		private final BigInteger x;
		private final BigInteger z;
		public SimplifiedTuple(BigInteger x,BigInteger z)	{
			this.x=x;
			this.z=z;
		}
	}
	
	private static class Decomposer	{
		private final int[] lastPrimes;
		private final LongObjMap<IntObjMap<BigInteger>> powerCache;
		private final LongObjMap<BigInteger> primeCache;
		public Decomposer(int maxValue)	{
			lastPrimes=Primes.lastPrimeSieve(maxValue);
			powerCache=HashLongObjMaps.newMutableMap();
			primeCache=HashLongObjMaps.newMutableMap();
		}
		private BigInteger getPrime(long value)	{
			return primeCache.computeIfAbsent(value,BigInteger::valueOf);
		}
		private BigInteger getPower(long prime,int exponent)	{
			IntObjMap<BigInteger> innerMap=powerCache.computeIfAbsent(prime,(long unused)->HashIntObjMaps.newMutableMap());
			return innerMap.computeIfAbsent(exponent,(int exp)->getPrime(prime).pow(exp));
		}
		private DecompositionInfo decompose(int value)	{
			DivisorHolder baseDecomposition=DivisorHolder.getFromFirstPrimes(value,lastPrimes);
			BigInteger commonFactor=BigInteger.ONE;
			List<BigInteger> alternatingFactors=new ArrayList<>();
			for (LongIntCursor cursor=baseDecomposition.getFactorMap().cursor();cursor.moveNext();)	{
				long prime=cursor.key();
				int exponent=cursor.value();
				if (prime==2)	{
					if (exponent==1) commonFactor=getPower(prime,2);
					else	{
						commonFactor=getPower(prime,3);
						int remainder=4*exponent-6;
						alternatingFactors.add(getPower(prime,remainder));
					}
				}	else alternatingFactors.add(getPower(prime,4*exponent));
			}
			return new DecompositionInfo(commonFactor,alternatingFactors);
		}
		public List<SimplifiedTuple> getAllXAndZ(int y)	{
			DecompositionInfo decompData=decompose(y);
			if (decompData.alternatingFactors.isEmpty()) return Collections.emptyList();
			List<Pair<BigInteger,BigInteger>> currentPairs=List.of(new Pair<>(decompData.commonFactor,decompData.commonFactor));
			/*
			 * This is possibly the slowest portion of code in all this file, but I don't expect decompData.alternatingFactors to have more than 5
			 * or 6 elements, so this is should be reasonable in terms of time.
			 */
			for (BigInteger newFactor:decompData.alternatingFactors)	{
				List<Pair<BigInteger,BigInteger>> newPairs=new ArrayList<>();
				for (Pair<BigInteger,BigInteger> oldPair:currentPairs)	{
					BigInteger n1=oldPair.first;
					BigInteger n2=oldPair.second;
					if (!n1.equals(n2)) newPairs.add(new Pair<>(n1.multiply(newFactor),n2));
					newPairs.add(new Pair<>(n1,n2.multiply(newFactor)));
				}
				currentPairs=newPairs;
			}
			List<SimplifiedTuple> result=new ArrayList<>();
			for (Pair<BigInteger,BigInteger> pair:currentPairs)	{
				BigInteger z=pair.second.add(pair.first).shiftRight(1);
				BigInteger x=pair.second.subtract(pair.first).abs().shiftRight(3);
				result.add(new SimplifiedTuple(x,z));
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		BigInteger result=BigInteger.ZERO;
		Decomposer decomposer=new Decomposer(LIMIT_Y);
		int totalCounter=0;
		for (int y=3;y<=LIMIT_Y;++y)	{
			int counter=0;
			for (SimplifiedTuple tuple:decomposer.getAllXAndZ(y)) if ((tuple.z.compareTo(LIMIT)<=0)&&(tuple.x.testBit(0)||tuple.z.testBit(0)))	{
				++counter;
				result=result.add(tuple.x.add(tuple.z));
			}
			if (counter>0) result=result.add(BigInteger.valueOf(counter*y));
			totalCounter+=counter;
		}
		System.out.println(result);
		System.out.println(totalCounter);
	}
}
