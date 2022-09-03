package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler580_6 {
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
		public boolean shouldBeCounted()	{
			for (int e:primes1) if (e>=2) return false;
			boolean strike=false;
			for (int e:primes3) if (e>=4) return false;
			else if (e>=2)	{
				if (strike) return false;
				else strike=true;
			}
			return true;
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
		LongIntMap counters=HashLongIntMaps.newMutableMap(2500000);
		counters.addValue(1l,1);
		for (long i=5;i<=LIMIT;i+=4) counters.addValue(i,1);
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
			for (long j=i2;j<=LIMIT;j+=4*i2) counters.addValue(j,möbius);
		}
		// Still wrong, but better than the previous result.
		System.out.println(result);
		System.out.println(calculator.cache);
		IntObjMap<LongSet> shouldHaveBeenZero=HashIntObjMaps.newMutableMap();
		IntObjMap<LongSet> shouldHaveBeenOne=HashIntObjMaps.newMutableMap();
		Map<PowerType,IntIntMap> appearances=new HashMap<>();
		int resultUsingDivisors=0;
		long[] primes=Primes.lastPrimeSieve(LIMIT);
		for (long i=1;i<=LIMIT;i+=4)	{
			DivisorHolder divisors=DivisorHolder.getFromFirstPrimes(i,primes);
			int foundTimes=counters.get(i);
			IntIntMap appearancesTarget=appearances.computeIfAbsent(PowerType.getFromDivisors(divisors),(PowerType unused)->HashIntIntMaps.newMutableMap());
			appearancesTarget.addValue(foundTimes,1);
			boolean isExpected=true;
			boolean hasStrikeOne=false;
			for (LongIntCursor cursor=divisors.getFactorMap().cursor();cursor.moveNext();)	{
				long m=(cursor.key())%4;
				int exp=cursor.value();
				if (m==1)	{
					if (exp>1) isExpected=false;
				}	else	{
					if (exp>=4) isExpected=false;
					else if (exp>=2)	{
						if (hasStrikeOne) isExpected=false;
						else hasStrikeOne=true;
					}
				}
				if (!isExpected) break;
			}
			if (isExpected) ++resultUsingDivisors;
			if ((isExpected&&(foundTimes!=1))||(!isExpected&&(foundTimes!=0)))	{
				IntObjMap<LongSet> workingCounter=isExpected?shouldHaveBeenOne:shouldHaveBeenZero;
				LongSet toAdd=workingCounter.computeIfAbsent(foundTimes,(int unused)->HashLongSets.newMutableSet());
				toAdd.add(i);
			}
		}
		System.out.println("Divisors: "+resultUsingDivisors+".");
		System.out.println("Incl. excl.: "+result+".");
		System.out.println("Should have been zero, but wasn't:");
		System.out.println(shouldHaveBeenZero);
		for (IntObjCursor<LongSet> cursor=shouldHaveBeenZero.cursor();cursor.moveNext();) System.out.println("\t"+cursor.key()+" => "+cursor.value().size()+" DINGE!!!!!");
		System.out.println("Should have been one, but wasn't:");
		System.out.println(shouldHaveBeenOne);
		for (IntObjCursor<LongSet> cursor=shouldHaveBeenOne.cursor();cursor.moveNext();) System.out.println("\t"+cursor.key()+" => "+cursor.value().size()+" DINGE!!!!!");
		System.out.println();
		for (Map.Entry<PowerType,IntIntMap> entry:appearances.entrySet()) if (entry.getValue().size()!=1) System.out.println("Inconsistent entry!: "+entry);
		else	{
			@SuppressWarnings("deprecation")
			int times=entry.getValue().keySet().iterator().nextInt();
			PowerType type=entry.getKey();
			int expectedTimes=type.shouldBeCounted()?1:0;
			// if (times!=expectedTimes) System.out.println(type+" is counted "+times+" times, but the correct result is "+expectedTimes+".");
			if (times==expectedTimes) System.out.println(type+" is correctly counted "+times+" times.");
		}
	}
}
