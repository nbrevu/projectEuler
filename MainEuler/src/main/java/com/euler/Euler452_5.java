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
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCollection;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongLongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import com.koloboke.collect.map.hash.HashObjLongMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler452_5 {
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
	
	private static class DecompositionPlanElement	{
		public final long factor;
		public final long other;
		private final LongSet toRemove;
		public DecompositionPlanElement(long factor,long other)	{
			this.factor=factor;
			this.other=other;
			toRemove=HashLongSets.newMutableSet();
		}
		public void signalLastUsage(long element)	{
			toRemove.add(element);
		}
		public LongSet canRemoveAfterThis()	{
			return toRemove;
		}
	}
	
	private static LongObjMap<DecompositionPlanElement> getDecompositionPlan(SortedMap<Long,PrimePowerCounter> numbers)	{
		LongObjMap<DecompositionPlanElement> result=HashLongObjMaps.newMutableMap();
		LongLongMap lastSeen=HashLongLongMaps.newMutableMap();
		for (Map.Entry<Long,PrimePowerCounter> entry:numbers.entrySet())	{
			long number=entry.getKey();
			long factor=0;
			if (number==1) factor=1l;
			else	{
				DecompositionPlanElement previous=null;
				for (LongIntCursor cursor=entry.getValue().prototype.getFactorMap().cursor();cursor.moveNext();)	{
					factor=cursor.key();
					previous=result.get(number/factor);
					if (previous!=null) break;
				}
				if (previous==null) throw new IllegalStateException();
			}
			long other=number/factor;
			lastSeen.put(other,number);
			lastSeen.put(number,number);
			DecompositionPlanElement decomp=new DecompositionPlanElement(factor,other);
			result.put(number,decomp);
		}
		for (LongLongCursor cursor=lastSeen.cursor();cursor.moveNext();) result.get(cursor.value()).signalLastUsage(cursor.key());
		return result;
	}
	
	private static class MultipleFactorDecompositionCache	{
		private final LongObjMap<Set<LongIntMap>> cache;
		public MultipleFactorDecompositionCache()	{
			cache=HashLongObjMaps.newMutableMap();
			LongIntMap emptyDecomp=HashLongIntMaps.newMutableMap();
			cache.put(1l,Collections.singleton(emptyDecomp));
		}
		public Set<LongIntMap> getFor(long number,DecompositionPlanElement element)	{
			Set<LongIntMap> result;
			if (number==1)	{
				LongIntMap emptyDecomp=HashLongIntMaps.newMutableMap();
				result=Collections.singleton(emptyDecomp);
			}	else	{
				/*
				 * This is, unfortunately, horribly inefficient.
				 */
				result=new HashSet<>();
				Set<LongIntMap> previous=cache.get(element.other);
				long factor=element.factor;
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
			}
			cache.put(number,result);
			for (LongCursor cursor=element.canRemoveAfterThis().cursor();cursor.moveNext();) cache.remove(cursor.elem());
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
		LongObjMap<DecompositionPlanElement> decompositionPlan=getDecompositionPlan(sortedSummary);
		ObjLongMap<PowerHolder> result=HashObjLongMaps.newMutableMap();
		int index=0;
		for (Map.Entry<Long,PrimePowerCounter> entry:sortedSummary.entrySet())	{
			long number=entry.getKey();
			long counter=entry.getValue().counter;
			Set<LongIntMap> cachedResults=cache.getFor(number,decompositionPlan.get(number));
			++index;
			System.out.println(index+": "+entry.getKey()+" ("+cachedResults.size()+" resultados)...");
			for (LongIntMap decomposition:cachedResults) result.addValue(new PowerHolder(decomposition.values()),counter);
		}
		return result;
	}
	
	// This works, but fails for 1e9 because of memory issues :(.
	// Elapsed 21472.832393200002 seconds. JAJA SI.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Map<PowerHolder,PrimePowerCounter> summary=getPrimePowerSummary();
		System.out.println("Summary created.");
		SortedMap<Long,PrimePowerCounter> sortedSummary=sortByPrototype(summary.values());
		System.out.println("Prototypes sorted ("+sortedSummary.size()+" elements).");
		summary.clear();
		ObjLongMap<PowerHolder> lowestLevelDecompositions=getFinalDecompositions(sortedSummary);
		System.out.println("Decompositions calculated.");
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
	}
}
