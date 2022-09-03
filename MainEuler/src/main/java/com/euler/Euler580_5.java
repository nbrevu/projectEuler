package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler580_5 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	private static class PowerType	{
		private final int[] primes1;
		private final int[] primes3;
		private PowerType(int[] primes1,int[] primes3)	{
			this.primes1=primes1;
			this.primes3=primes3;
		}
		private static int[] toArray(IntStream.Builder streamBuilder)	{
			return streamBuilder.build().sorted().toArray();
		}
		public static PowerType getFromDivisors(DivisorHolder holder)	{
			IntStream.Builder primes1=IntStream.builder();
			IntStream.Builder primes3=IntStream.builder();
			for (LongIntCursor cursor=holder.getFactorMap().cursor();cursor.moveNext();)	{
				long m=(cursor.key())%4;
				IntStream.Builder target=(m==1)?primes1:primes3;
				target.accept(cursor.value());
			}
			return new PowerType(toArray(primes1),toArray(primes3));
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(primes1)+Arrays.hashCode(primes3);
		}
		@Override
		public boolean equals(Object other)	{
			PowerType ptOther=(PowerType)other;
			return Arrays.equals(primes1,ptOther.primes1)&&Arrays.equals(primes3,ptOther.primes3);
		}
		@Override
		public String toString()	{
			return "(4k+1: "+Arrays.toString(primes1)+"; 4k+3: "+Arrays.toString(primes3)+")";
		}
		public ObjIntMap<PowerType> getChildren()	{
			ObjIntMap<PowerType> result=HashObjIntMaps.newMutableMap();
			int[] maxValues=new int[primes1.length+primes3.length];
			System.arraycopy(primes1,0,maxValues,0,primes1.length);
			System.arraycopy(primes3,0,maxValues,primes1.length,primes3.length);
			int[] currentValues=new int[maxValues.length];
			for (;;)	{
				int idx=currentValues.length-1;
				while (currentValues[idx]==maxValues[idx])	{
					currentValues[idx]=0;
					--idx;
				}
				++currentValues[idx];
				if (Arrays.equals(maxValues,currentValues)) return result;
				int p3Sum=0;
				for (int i=primes1.length;i<maxValues.length;++i) p3Sum+=currentValues[i];
				if ((p3Sum%2)!=0) continue;
				IntStream.Builder newPrimes1=IntStream.builder();
				IntStream.Builder newPrimes3=IntStream.builder();
				for (int i=0;i<primes1.length;++i) if (currentValues[i]!=0) newPrimes1.accept(currentValues[i]);
				for (int i=primes1.length;i<currentValues.length;++i) if (currentValues[i]!=0) newPrimes3.accept(currentValues[i]);
				PowerType child=new PowerType(toArray(newPrimes1),toArray(newPrimes3));
				result.addValue(child,1);
			}
		}
	}
	
	private static class ModifiedMöbiusCoefficientCalculator	{
		private final long[] lastPrimes;
		private final ObjIntMap<PowerType> cache;
		public ModifiedMöbiusCoefficientCalculator(long limit)	{
			lastPrimes=Primes.lastPrimeSieve(limit);
			cache=HashObjIntMaps.newMutableMap();
		}
		public int getMöbiusCoefficient(long num)	{
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(num,lastPrimes);
			PowerType type=PowerType.getFromDivisors(decomposition);
			return getMöbiusCoefficient(type);
		}
		private int getMöbiusCoefficient(PowerType type)	{
			if (cache.containsKey(type)) return cache.getInt(type);
			int result=calculateMöbiusCoefficient(type);
			cache.put(type,result);
			return result;
		}
		private int calculateMöbiusCoefficient(PowerType type)	{
			int result=-1;
			for (ObjIntCursor<PowerType> cursor=type.getChildren().cursor();cursor.moveNext();) result-=getMöbiusCoefficient(cursor.key())*cursor.value();
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long s=LongMath.sqrt(LIMIT,RoundingMode.DOWN);
		ModifiedMöbiusCoefficientCalculator calculator=new ModifiedMöbiusCoefficientCalculator(s);
		long result=LIMIT/4;
		if ((LIMIT%4)!=0) ++result;
		for (long i=5;i<s;i+=4)	{
			int möbius=calculator.getMöbiusCoefficient(i);
			if (möbius==0) continue;
			long i2=i*i;
			long howMany=LIMIT/(4*i2);
			long rem=LIMIT%(4*i2);
			if (rem>=i2) ++howMany;
			result+=howMany*möbius;
		}
		// Still wrong.
		System.out.println(result);
		System.out.println(calculator.cache);
	}
}
