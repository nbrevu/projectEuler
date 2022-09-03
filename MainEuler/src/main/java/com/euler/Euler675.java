package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler675 {
	private final static long MOD=LongMath.pow(10l,9)+87;
	private final static long LIMIT=10;
	
	private static class PowerOf2Storage	{
		public long lastPower;
		public long lastSum;
		public PowerOf2Storage()	{
			this(1l,1l);
		}
		public PowerOf2Storage(long lastPower,long lastSum)	{
			this.lastPower=lastPower;
			this.lastSum=lastSum;
		}
	}
	
	private static class FactorialOmegaAccumulator	{
		private final long[] firstPrimes;
		private final LongObjMap<PowerOf2Storage> currentFactors;
		private final long mod;
		private long currentValue;
		public FactorialOmegaAccumulator(long limit,long mod)	{
			firstPrimes=Primes.firstPrimeSieve(limit);
			currentFactors=HashLongObjMaps.newMutableMap();
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
				PowerOf2Storage storage=currentFactors.computeIfAbsent(prime,(long unused)->new PowerOf2Storage());
				long newPower=storage.lastPower;
				long newSum=storage.lastSum;
				for (int i=0;i<power;++i)	{
					newPower+=newPower;
					newPower%=mod;
					newSum+=newPower;
					newSum%=mod;
				}
				long oldInverse=EulerUtils.modulusInverse(storage.lastSum,mod);
				long factor=(oldInverse*newSum)%mod;
				currentValue=(currentValue*factor)%mod;
				storage.lastPower=newPower;
				storage.lastSum=newSum;
			}
		}
	}
	
	public static void main(String[] args)	{
		// D'oh! This is "correct", but it calculates a different thing because I had misread!!!
		long sum=0l;
		FactorialOmegaAccumulator accumulator=new FactorialOmegaAccumulator(LIMIT,MOD);
		for (long i=2;i<=LIMIT;++i)	{
			accumulator.addNumber(i);
			sum=(sum+accumulator.getCurrentValue())%MOD;
		}
		System.out.println(sum);
	}
}
