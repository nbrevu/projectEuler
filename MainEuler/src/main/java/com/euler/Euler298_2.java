package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler298_2 {
	private final static int CACHE_SIZE=5;
	private final static int MAX_NUMBER=10;
	private final static int ROUNDS=50;
	
	private final static int[] ARRAY15=IntStream.range(1,1+CACHE_SIZE).toArray();
	
	private static int findValue(int[] array,int value)	{
		for (int i=0;i<array.length;++i) if (array[i]==value) return i;
		return -1;
	}
	
	private static class ArrayWrapper	{
		public final int[] array;
		public ArrayWrapper(int[] array)	{
			this.array=array;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			ArrayWrapper awOther=(ArrayWrapper)other;
			return Arrays.equals(array,awOther.array);
		}
	}
	
	private static class TransitionResult	{
		public final boolean hitLarry;
		public final boolean hitRobin;
		public final CacheStatus afterTransition;
		public TransitionResult(boolean hitLarry,boolean hitRobin,CacheStatus afterTransition)	{
			this.hitLarry=hitLarry;
			this.hitRobin=hitRobin;
			this.afterTransition=afterTransition;
		}
	}
	
	private static interface CacheStatus	{
		public TransitionResult transition(int number);
	}
	
	/*
	 * Status where only N<5 numbers are present. If any number between 1 and <available> appears (both included), it's a cache hit for both; if
	 * any other number appears, it's a miss for both. Larry's status still needs to be computed carefully.
	 */
	private static class IncompleteStatus implements CacheStatus	{
		private final int[] larryStatus;
		private final int hashCode;
		public IncompleteStatus(ArrayWrapper wrapper)	{
			this(wrapper.array);
		}
		public IncompleteStatus(int[] larryStatus)	{
			this.larryStatus=larryStatus;
			hashCode=Arrays.hashCode(larryStatus);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			if (!(other instanceof IncompleteStatus)) return false;
			IncompleteStatus isOther=(IncompleteStatus)other;
			return Arrays.equals(larryStatus,isOther.larryStatus);
		}
		@Override
		public TransitionResult transition(int number) {
			int available=larryStatus.length;
			if (number<=available)	{
				int[] result=new int[available];
				// No new number. larryStatus still needs to be reordered.
				int position=findValue(larryStatus,number);
				System.arraycopy(larryStatus,0,result,0,position);
				System.arraycopy(larryStatus,1+position,result,position,available-1-position);
				result[available-1]=number;
				CacheStatus transitionTarget=INCOMPLETE_STATES.getStatus(result);
				return new TransitionResult(true,true,transitionTarget);
			}	else	{
				int[] result=new int[1+available];
				System.arraycopy(larryStatus,0,result,0,available);
				result[available]=1+available;	// NOT "number". We must preserve the [1..available] permutation condition.
				CacheStatus transitionTarget=(result.length>=CACHE_SIZE)?FULL_STATES.getStatus(result):INCOMPLETE_STATES.getStatus(result);
				return new TransitionResult(false,false,transitionTarget);
			}
		}
	}
	
	/*
	 * Status where ROBIN's numbers are {1,2,3,4,5} and LARRY's numbers are represented by the array. Larry's numbers are in such order that the
	 * first element of the array is the one which hasn't been called in the longest time. Transitions need to be calculated very carefully.
	 * 
	 * 1) On miss, Robin gets {2,3,4,5,X} which is "translated" into {1,2,3,4,5}.
	 * 2) On miss, Larry must recalculate its array. Typically most numbers will match, but not always. Translation into a common numbering
	 * order, where Robin is {1,2,3,4,5}, must be done.
	 */
	private static class FullCacheStatus implements CacheStatus	{
		private final int[] larryStatus;
		private final int hashCode;
		public FullCacheStatus(ArrayWrapper wrapper)	{
			this(wrapper.array);
		}
		public FullCacheStatus(int[] larryStatus)	{
			this.larryStatus=larryStatus;
			hashCode=Arrays.hashCode(larryStatus);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			if (!(other instanceof FullCacheStatus)) return false;
			FullCacheStatus fcsOther=(FullCacheStatus)other;
			return Arrays.equals(larryStatus,fcsOther.larryStatus);
		}
		private int[] evolveRobin(int number)	{
			if (number<=CACHE_SIZE) return ARRAY15;
			int[] result=new int[CACHE_SIZE];
			result=new int[CACHE_SIZE];
			for (int i=0;i<CACHE_SIZE-1;++i) result[i]=i+2;
			result[CACHE_SIZE-1]=number;
			return result;
		}
		private int[] evolveLarry(int number)	{
			int position=findValue(larryStatus,number);
			int[] result=new int[CACHE_SIZE];
			if (position==-1) System.arraycopy(larryStatus,1,result,0,CACHE_SIZE-1);
			else	{
				System.arraycopy(larryStatus,0,result,0,position);
				System.arraycopy(larryStatus,1+position,result,position,CACHE_SIZE-1-position);
			}
			result[CACHE_SIZE-1]=number;
			return result;
		}
		private int[] getMapping(int[] newRobin,int[] newLarry)	{
			IntSet larrySet=HashIntSets.newMutableSet(newLarry);
			int[] result=new int[1+MAX_NUMBER];
			for (int i=0;i<CACHE_SIZE;++i)	{
				result[newRobin[i]]=i+1;
				larrySet.removeInt(newRobin[i]);
			}
			int[] remaining=larrySet.toIntArray();
			Arrays.sort(remaining);
			for (int i=0;i<remaining.length;++i) result[remaining[i]]=i+1+CACHE_SIZE;
			return result;
		}
		private int[] remap(int[] toRemap,int[] mapping)	{
			int[] result=new int[CACHE_SIZE];
			for (int i=0;i<CACHE_SIZE;++i) result[i]=mapping[toRemap[i]];
			return result;
		}
		@Override
		public TransitionResult transition(int number) {
			boolean larryHit=findValue(larryStatus,number)!=-1;
			boolean robinHit=number<=CACHE_SIZE;
			int[] newRobin=evolveRobin(number);
			int[] newLarry=evolveLarry(number);
			int[] mapping=getMapping(newRobin,newLarry);
			int[] remapped=remap(newLarry,mapping);
			CacheStatus newStatus=FULL_STATES.getStatus(remapped);
			return new TransitionResult(larryHit,robinHit,newStatus);
		}
	}
	
	private static class IncompleteStatusCache	{
		private final Map<ArrayWrapper,IncompleteStatus> states;
		public IncompleteStatusCache()	{
			states=new HashMap<>();
		}
		public IncompleteStatus getStatus(int[] larryStatus)	{
			ArrayWrapper wrapper=new ArrayWrapper(larryStatus);
			return states.computeIfAbsent(wrapper,IncompleteStatus::new);
		}
	}
	
	private static class FullCacheStatusCache	{
		private final Map<ArrayWrapper,FullCacheStatus> states;
		public FullCacheStatusCache()	{
			states=new HashMap<>();
		}
		public FullCacheStatus getStatus(int[] larryStatus)	{
			ArrayWrapper wrapper=new ArrayWrapper(larryStatus);
			return states.computeIfAbsent(wrapper,FullCacheStatus::new);
		}
	}
	
	private static class TransitionCache	{
		private final Map<CacheStatus,TransitionResult[]> transitions;
		public TransitionCache()	{
			transitions=new HashMap<>();
		}
		private TransitionResult[] computeTransitions(CacheStatus status)	{
			TransitionResult[] result=new TransitionResult[MAX_NUMBER];
			for (int i=1;i<=MAX_NUMBER;++i) result[i-1]=status.transition(i);
			return result;
		}
		public TransitionResult[] getTransitions(CacheStatus status)	{
			return transitions.computeIfAbsent(status,this::computeTransitions);
		}
	}
	
	private final static IncompleteStatusCache INCOMPLETE_STATES=new IncompleteStatusCache();
	private final static FullCacheStatusCache FULL_STATES=new FullCacheStatusCache();
	private final static TransitionCache TRANSITIONS=new TransitionCache();
	
	private static class FullStatus	{
		public final CacheStatus cache;
		public final int larryHits;
		public final int robinHits;
		private final int hashCode;
		public FullStatus(CacheStatus cache,int larryHits,int robinHits)	{
			this.cache=cache;
			this.larryHits=larryHits;
			this.robinHits=robinHits;
			hashCode=Objects.hash(cache,larryHits,robinHits);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			FullStatus fsOther=(FullStatus)other;
			return cache.equals(fsOther.cache)&&(larryHits==fsOther.larryHits)&&(robinHits==fsOther.robinHits);
		}
	}
	
	private static Map<FullStatus,BigInteger> getNextGeneration(Map<FullStatus,BigInteger> currentGeneration)	{
		Map<FullStatus,BigInteger> result=new HashMap<>();
		for (Map.Entry<FullStatus,BigInteger> entry:currentGeneration.entrySet())	{
			FullStatus sourceStatus=entry.getKey();
			BigInteger cardinality=entry.getValue();
			TransitionResult[] transitions=TRANSITIONS.getTransitions(entry.getKey().cache);
			for (int i=0;i<MAX_NUMBER;++i)	{
				int newLarryHits=transitions[i].hitLarry?(1+sourceStatus.larryHits):sourceStatus.larryHits;
				int newRobinHits=transitions[i].hitRobin?(1+sourceStatus.robinHits):sourceStatus.robinHits;
				FullStatus targetStatus=new FullStatus(transitions[i].afterTransition,newLarryHits,newRobinHits);
				result.compute(targetStatus,(FullStatus unused,BigInteger previousCardinality)->	{
					if (previousCardinality==null) return cardinality;
					else return previousCardinality.add(cardinality);
				});
			}
		}
		return result;
	}
	
	private static BigDecimal getResult(Map<FullStatus,BigInteger> lastGeneration)	{
		BigInteger numerator=BigInteger.ZERO;
		BigInteger denominator=BigInteger.ZERO;
		for (Map.Entry<FullStatus,BigInteger> entry:lastGeneration.entrySet())	{
			FullStatus status=entry.getKey();
			BigInteger cardinality=entry.getValue();
			BigInteger diff=BigInteger.valueOf(Math.abs(status.larryHits-status.robinHits));
			numerator=numerator.add(cardinality.multiply(diff));
			denominator=denominator.add(cardinality);
		}
		return new BigDecimal(numerator).divide(new BigDecimal(denominator));
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		FullStatus turn1Status=new FullStatus(INCOMPLETE_STATES.getStatus(new int[] {1}),0,0);
		Map<FullStatus,BigInteger> currentGeneration=new HashMap<>();
		currentGeneration.put(turn1Status,BigInteger.TEN);	// Would actually work just as well with BigInteger.ONE, but this is more precise.
		for (int i=2;i<=ROUNDS;++i) currentGeneration=getNextGeneration(currentGeneration);
		String result=getResult(currentGeneration).toString();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(INCOMPLETE_STATES.states.size()+" small states used.");
		System.out.println(FULL_STATES.states.size()+" big states used.");
	}
}
