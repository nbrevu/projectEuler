package com.euler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils.LongPair;

/*
 * Let's try dynamic programming. I'm about 99% sure that this would take too much memory if done directly for 10^5, but...
 * yeah, let's try :).
 */
public class Euler806_3 {
	private static class HanoiCombination	{
		public final int left;
		public final int middle;
		public final int right;
		public HanoiCombination(int left,int middle,int right)	{
			this.left=left;
			this.middle=middle;
			this.right=right;
		}
		public boolean isLosing()	{
			return (left^middle^right)==0;
		}
	}
	
	private static class HanoiCombinationCache	{
		private final List<List<List<HanoiCombination>>> combis;
		public HanoiCombinationCache()	{
			combis=new ArrayList<>();
			HanoiCombination combi0=new HanoiCombination(0,0,0);	// Not actually used, but it "takes space", so to speak.
			List<HanoiCombination> list00=new ArrayList<>();
			list00.add(combi0);
			List<List<HanoiCombination>> list0=new ArrayList<>();
			list0.add(list00);
			combis.add(list0);
		}
		public HanoiCombination get(int left,int middle,int right)	{
			while (left>=combis.size()) combis.add(new ArrayList<>());
			List<List<HanoiCombination>> listA=combis.get(left);
			while (middle>=listA.size()) listA.add(new ArrayList<>());
			List<HanoiCombination> listB=listA.get(middle);
			while (right>=listB.size()) listB.add(new HanoiCombination(left,middle,listB.size()));
			return listB.get(right);
		}
	}
	
	private static class HanoiSequence	{
		private static LongPair combineSumAndCount(LongPair a,LongPair b)	{
			return new LongPair(a.x+b.x,a.y+b.y);
		}
		private final Map<HanoiCombination,LongPair> countAndSums;
		private HanoiSequence(Map<HanoiCombination,LongPair> countAndSums)	{
			this.countAndSums=countAndSums;
		}
		public static HanoiSequence getInitial(HanoiCombinationCache cache)	{
			HanoiCombination combi0=cache.get(1,0,0);
			HanoiCombination combi1=cache.get(0,0,1);
			LongPair sum0=new LongPair(1,0);
			LongPair sum1=new LongPair(1,1);
			Map<HanoiCombination,LongPair> countAndSums=Map.of(combi0,sum0,combi1,sum1);
			return new HanoiSequence(countAndSums);
		}
		public HanoiSequence evolve(HanoiCombinationCache cache,long power2)	{
			Map<HanoiCombination,LongPair> result=new HashMap<>();
			for (Map.Entry<HanoiCombination,LongPair> entry:countAndSums.entrySet())	{
				HanoiCombination oldKey=entry.getKey();
				LongPair oldValue=entry.getValue();
				{
					// First half.
					HanoiCombination newKey=cache.get(oldKey.left+1,oldKey.right,oldKey.middle);
					result.merge(newKey,oldValue,HanoiSequence::combineSumAndCount);
				}
				{
					// Second half (displaced).
					long newSum=oldValue.x*power2+oldValue.y;
					LongPair newValue=new LongPair(oldValue.x,newSum);
					HanoiCombination newKey=cache.get(oldKey.middle,oldKey.left,oldKey.right+1);
					result.merge(newKey,newValue,HanoiSequence::combineSumAndCount);
				}
			}
			return new HanoiSequence(result);
		}
		public LongPair accumulateValidCases()	{
			long count=0;
			long sum=0;
			for (Map.Entry<HanoiCombination,LongPair> entry:countAndSums.entrySet())	{
				HanoiCombination key=entry.getKey();
				if (key.isLosing())	{
					LongPair value=entry.getValue();
					count+=value.x;
					sum+=value.y;
				}
			}
			return new LongPair(count,sum);
		}
		public int size()	{
			return countAndSums.size();
		}
	}
	
	public static void main(String[] args)	{
		HanoiCombinationCache cache=new HanoiCombinationCache();
		HanoiSequence current=HanoiSequence.getInitial(cache);
		long pow2=1l;
		for (int i=2;i<=30;++i)	{
			pow2*=2l;
			current=current.evolve(cache,pow2);
			if ((i%2)==0)	{
				LongPair results=current.accumulateValidCases();
				System.out.println(String.format("For N=%d there are %d solutions adding up to %d.",i,results.x,results.y));
				System.out.println(String.format("\tThe map is growing up to %d elements, though.",current.size()));
			}
		}
	}
}
