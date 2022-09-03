package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.PartitionGenerator;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCollection;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler452_4 {
	private final static int N=IntMath.pow(10,9);
	private final static long MOD=1234567891l;
	
	private static class PowerHolder	{
		private final int[] contents;
		private final int hashCode;
		public PowerHolder(IntCollection contents)	{
			this.contents=contents.toArray(new int[contents.size()]);
			Arrays.sort(this.contents);
			hashCode=Arrays.hashCode(this.contents);
		}
		@Override
		public boolean equals(Object other)	{
			PowerHolder pphOther=(PowerHolder)other;
			return Arrays.equals(contents,pphOther.contents);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
	}
	
	private static class PrimePowerCounter	{
		private final DivisorHolder prototype;
		private int counter;
		public PrimePowerCounter(DivisorHolder prototype)	{
			this.prototype=prototype;
			counter=1;
		}
	}
	
	private static class VariationCalculator	{
		private final List<Long> values;
		private long next;
		public VariationCalculator(long initialValue)	{
			values=new ArrayList<>();
			values.add(1l);
			next=initialValue;
		}
		public long get(int index)	{
			while (values.size()<=index)	{
				long lastValue=values.get(values.size()-1);
				long nextValue=(lastValue*next)%MOD;
				values.add(nextValue);
				--next;
			}
			return values.get(index);
		}
	}
	
	private static class InverseFactorialCalculator	{
		private final List<Long> values;
		private long next;
		public InverseFactorialCalculator()	{
			values=new ArrayList<>();
			values.add(1l);
			next=1l;
		}
		public long get(int index)	{
			while (values.size()<=index)	{
				long lastValue=values.get(values.size()-1);
				long nextFactor=EulerUtils.modulusInverse(next,MOD);
				long nextValue=(lastValue*nextFactor)%MOD;
				values.add(nextValue);
				++next;
			}
			return values.get(index);
		}
	}
	
	private static class MultipleFactorDecompositionCache	{
		private final LongObjMap<Set<LongIntMap>> cache;
		public MultipleFactorDecompositionCache()	{
			cache=HashLongObjMaps.newMutableMap();
			LongIntMap emptyDecomp=HashLongIntMaps.newMutableMap();
			cache.put(1l,Collections.singleton(emptyDecomp));
		}
		public Set<LongIntMap> getFor(PrimePowerCounter counter,long number)	{
			Set<LongIntMap> result=cache.get(number);
			if (result!=null) return result;
			LongIntMap factors=counter.prototype.getFactorMap();
			long factor=-1;
			Set<LongIntMap> previous=null;
			for (LongIntCursor cursor=factors.cursor();cursor.moveNext();)	{
				factor=cursor.key();
				previous=cache.get(number/factor);
				if (previous!=null) break;
			}
			if (previous==null) throw new IllegalStateException();
			result=new HashSet<>();
			/*
			 * This is, unfortunately, horribly inefficient.
			 */
			for (LongIntMap set:previous)	{
				LongIntMap toAdd=HashLongIntMaps.newMutableMap(set);
				toAdd.addValue(factor,1);
				result.add(toAdd);
				for (LongIntCursor cursor=set.cursor();cursor.moveNext();)	{
					toAdd=HashLongIntMaps.newMutableMap(set);
					if (cursor.value()==1) toAdd.remove(cursor.key());
					else toAdd.addValue(cursor.key(),-1);
					toAdd.addValue(cursor.key()*factor,1);
					result.add(toAdd);
				}
			}
			cache.put(number,result);
			return result;
		}
	}
	
	private static long getNumber(DivisorHolder div)	{
		long result=1l;
		for (LongIntCursor cursor=div.getFactorMap().cursor();cursor.moveNext();) result*=LongMath.pow(cursor.key(),cursor.value());
		return result;
	}
	
	private static Map<PowerHolder,PrimePowerCounter> getPrimePowerSummary()	{
		int[] firstPrimes=Primes.firstPrimeSieve(N);
		Map<PowerHolder,PrimePowerCounter> result=new HashMap<>();
		for (int i=1;i<=N;++i)	{
			DivisorHolder factors=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			PowerHolder powers=new PowerHolder(factors.getFactorMap().values());
			result.compute(powers,(PowerHolder holder,PrimePowerCounter counters)->	{
				if (counters==null) return new PrimePowerCounter(factors);
				++counters.counter;
				return counters;
			});
		}
		return result;
	}
	
	private static SortedMap<Long,PrimePowerCounter> sortByPrototype(Collection<PrimePowerCounter> counters)	{
		SortedMap<Long,PrimePowerCounter> result=new TreeMap<>();
		for (PrimePowerCounter counter:counters) result.put(getNumber(counter.prototype),counter);
		return result;
	}
	
	private static ObjLongMap<PowerHolder> getFinalDecompositions(SortedMap<Long,PrimePowerCounter> sortedSummary)	{
		MultipleFactorDecompositionCache cache=new MultipleFactorDecompositionCache();
		ObjLongMap<PowerHolder> result=HashObjLongMaps.newMutableMap();
		for (Map.Entry<Long,PrimePowerCounter> entry:sortedSummary.entrySet())	{
			long counter=entry.getValue().counter;
			for (LongIntMap decomposition:cache.getFor(entry.getValue(),entry.getKey())) result.addValue(new PowerHolder(decomposition.values()),counter);
		}
		return result;
	}
	
	// This works, but fails for 1e9 because of memory issues :(.
	public static void main(String[] args)	{
		/*
		long tic=System.nanoTime();
		Map<PowerHolder,PrimePowerCounter> summary=getPrimePowerSummary();
		SortedMap<Long,PrimePowerCounter> sortedSummary=sortByPrototype(summary.values());
		summary.clear();
		ObjLongMap<PowerHolder> lowestLevelDecompositions=getFinalDecompositions(sortedSummary);
		VariationCalculator variations=new VariationCalculator(N);
		InverseFactorialCalculator factorials=new InverseFactorialCalculator();
		long result=0l;
		for (ObjLongCursor<PowerHolder> cursor=lowestLevelDecompositions.cursor();cursor.moveNext();)	{
			PowerHolder object=cursor.key();
			long counter=cursor.value()%MOD;
			int totalExps=0;
			for (int exp:object.contents) totalExps+=exp;
			long thisResult=variations.get(totalExps);
			for (int exp:object.contents) thisResult=(thisResult*factorials.get(exp))%MOD;
			thisResult=(thisResult*counter)%MOD;
			result=(result+thisResult)%MOD;
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		*/
		// Mañana quiero intentar hacer ESTO:
		// https://stackoverflow.com/questions/8558292/how-to-find-multiplicative-partitions-of-any-integer
		// Probablemente no me salga, though.
		for (int i=1;i<=29;++i)	{
			List<int[]> partitions=PartitionGenerator.generatePartitions(i);
			System.out.println("Pues tengo "+partitions.size()+" particiones de tamaño "+i+", ODER?????");
		}
	}
}
