package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import com.koloboke.collect.IntCursor;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler703_2 {
	private final static int N=20;
	
	private static class CycleData	{
		public final IntSet allElements;
		public final int[] sortedCycle;
		public final IntSet entryPoints;
		private final IntSet cycleSet;
		public CycleData(IntSet allElements,int[] sortedCycle)	{
			this.allElements=allElements;
			this.sortedCycle=sortedCycle;
			entryPoints=HashIntSets.newMutableSet();
			cycleSet=HashIntSets.newImmutableSet(sortedCycle);
		}
		public boolean isInCycle(int element)	{
			return cycleSet.contains(element);
		}
	}
	
	private static int nextValue(int x)	{
		int b1=x&1;
		int b2=(x>>1)&1;
		int b3=(x>>2)&1;
		int y=x>>1;
		int lastBit=b1&(b2^b3);
		y+=(lastBit<<(N-1));
		return y;
	}
	
	public static void main(String[] args)	{
		int total=1<<N;
		CycleData[] assignedSets=new CycleData[total];
		List<CycleData> allSets=new ArrayList<>();
		for (int i=0;i<total;++i)	{
			/*
			 * OK. This looks good as a first approximation, but I need to be very careful detecting cycles and entry points.
			 * 1) Look for "pure" cycles.
			 * 2) Look for entry points of "pure" cycles. The entry points are likely to be ramified!
			 * 3) Use dynamic programming or whatever to do the actual calculations.
			 * 4) Multiply the results of each set!
			 * 
			 * ACHTUNG! Beware the effect of multiple entry points. There is a correlation between patterns, they are not independent-
			 * If this goes the way I think, it will be moderately challenging and very fun. If the actual cycles are super long, it might not work.
			 */
			if (assignedSets[i]!=null) continue;
			IntSet currentSet=HashIntSets.newMutableSet();
			int x=i;
			for (;;)	{
				currentSet.add(x);
				x=nextValue(x);
				if (currentSet.contains(x))	{
					// Since "x" is a cycle, we first build the actual cycle.
					IntStream.Builder builder=IntStream.builder();
					int y=x;
					do	{
						builder.add(y);
						y=nextValue(y);
					}	while (y!=x);
					int[] pureCycle=builder.build().toArray();
					CycleData data=new CycleData(currentSet,pureCycle);
					for (IntCursor cursor=currentSet.cursor();cursor.moveNext();)	{
						int elem=cursor.elem();
						if (!data.isInCycle(elem))	{
							y=nextValue(elem);
							if (data.isInCycle(y)) data.entryPoints.add(y);
						}
					}
					allSets.add(data);
					for (IntCursor cursor=currentSet.cursor();cursor.moveNext();) assignedSets[cursor.elem()]=data;
					break;
				}
				CycleData existingSet=assignedSets[x];
				if (existingSet!=null)	{
					for (IntCursor cursor=currentSet.cursor();cursor.moveNext();) assignedSets[cursor.elem()]=existingSet;
					currentSet.forEach((IntConsumer)existingSet.allElements::add);
					if (existingSet.isInCycle(x)) existingSet.entryPoints.add(x);
					break;
				}
			}
		}
		int totalCount=0;
		System.out.println("Total sets found: "+allSets.size()+".");
		for (CycleData set:allSets)	{
			int count=set.allElements.size();
			int cycleCount=set.sortedCycle.length;
			int entryCount=set.entryPoints.size();
			totalCount+=count;
			System.out.println("\tFull size:"+count+". Cycle length: "+cycleCount+". Entry points: "+entryCount+".");
			System.out.println("\tCycle elements: "+Arrays.toString(set.sortedCycle)+".");
		}
		System.out.println(totalCount);
	}
}
