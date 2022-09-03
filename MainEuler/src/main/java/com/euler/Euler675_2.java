package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler675_2 {
	private final static long MOD=LongMath.pow(10l,9)+87;
	private final static long LIMIT=LongMath.pow(10l,7);
	
	private static class FactorialOmegaAccumulator	{
		private final long[] firstPrimes;
		private final LongLongMap currentFactors;
		private final long mod;
		private long currentValue;
		public FactorialOmegaAccumulator(long limit,long mod)	{
			firstPrimes=Primes.firstPrimeSieve(limit);
			currentFactors=HashLongLongMaps.newMutableMap();
			this.mod=mod;
			currentValue=1l;
		}
		public long getCurrentValue()	{
			return currentValue;
		}
		public void addNumber(long in)	{
			DivisorHolder holder=DivisorHolder.getFromFirstPrimes(in,firstPrimes);
			LongIntCursor newFactors=holder.getFactorMap().cursor();
			while (newFactors.moveNext())	{
				long prime=newFactors.key();
				int power=newFactors.value();
				long oldSum=currentFactors.getOrDefault(prime,1l);
				long newSum=oldSum+power*2l;
				long oldInverse=EulerUtils.modulusInverse(oldSum,mod);
				long factor=(oldInverse*newSum)%mod;
				currentValue=(currentValue*factor)%mod;
				currentFactors.put(prime,newSum);
			}
		}
	}
	
	public static void main(String[] args)	{
		long sum=0l;
		FactorialOmegaAccumulator accumulator=new FactorialOmegaAccumulator(LIMIT,MOD);
		for (long i=2;i<=LIMIT;++i)	{
			accumulator.addNumber(i);
			sum=(sum+accumulator.getCurrentValue())%MOD;
		}
		System.out.println(sum);
	}
}
