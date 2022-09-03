package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Euler750_6 {
	private final static int N=976;
	
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
	
	private static class PartialSolution	{
		public final int cost;
		private final int[] visitedPositions;
		private PartialSolution(int cost,int[] visitedPositions)	{
			this.cost=cost;
			this.visitedPositions=visitedPositions;
		}
		public static PartialSolution getInitial(int lastCardPosition)	{
			return new PartialSolution(0,new int[] {lastCardPosition});
		}
		public Map<ArrayWrapper,Integer> getChildren(int nextPosition)	{
			Map<ArrayWrapper,Integer> result=new HashMap<>();
			for (int i=0;i<visitedPositions.length;++i)	{
				int[] newVisitedPositions=new int[i+2];
				System.arraycopy(visitedPositions,0,newVisitedPositions,0,i+1);
				newVisitedPositions[i+1]=nextPosition;
				result.put(new ArrayWrapper(newVisitedPositions),cost+Math.abs(nextPosition-newVisitedPositions[i]));
			}
			return result;
		}
	}
	
	private static class SolutionEvolver	{
		private final int maxSize;
		private final Comparator<PartialSolution> comparator;
		public SolutionEvolver(int maxSize,Comparator<PartialSolution> comparator)	{
			this.maxSize=maxSize;
			this.comparator=comparator;
		}
		public List<PartialSolution> advance(Collection<PartialSolution> currentGeneration,int nextPosition)	{
			Map<ArrayWrapper,Integer> bestSolutions=new HashMap<>();
			for (PartialSolution sol:currentGeneration)	{
				Map<ArrayWrapper,Integer> newSolutions=sol.getChildren(nextPosition);
				for (Map.Entry<ArrayWrapper,Integer> entry:newSolutions.entrySet())	{
					ArrayWrapper sequence=entry.getKey();
					Integer cost=entry.getValue();
					bestSolutions.compute(sequence,(ArrayWrapper unused,Integer existingCost)->(existingCost==null)?cost:Math.min(cost,existingCost));
				}
			}
			List<PartialSolution> allSolutions=new ArrayList<>(bestSolutions.size());
			for (Map.Entry<ArrayWrapper,Integer> entry:bestSolutions.entrySet()) allSolutions.add(new PartialSolution(entry.getValue(),entry.getKey().array));
			allSolutions.sort(comparator);
			if (allSolutions.size()<=maxSize) return allSolutions;
			else return allSolutions.subList(0,maxSize);
		}
	}
	
	private static int[] getPositionsArray(int size)	{
		int p=1;
		int mod=size+1;
		int[] result=new int[size];
		for (int i=0;i<size;++i)	{
			p*=3;
			p%=mod;
			result[p-1]=i;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		// After hours and hours, "+57" heuristic yields 161930, which is not the correct solution.
		// Without the "+57" (and with 4e6 instead of 6e6) the result is even worse, 162814.
		int[] reverseArray=getPositionsArray(N);
		List<PartialSolution> sols=List.of(PartialSolution.getInitial(reverseArray[N-1]));
		SolutionEvolver evolver=new SolutionEvolver(4_000_000,Comparator.comparingInt((PartialSolution s)->s.cost));
		for (int i=N-2;i>=0;--i)	{
			sols=evolver.advance(sols,reverseArray[i]);
			System.out.println("Voy por "+i+" y tengo "+sols.size()+" soluciones.");
		}
		int minCost=Integer.MAX_VALUE;
		for (PartialSolution p:sols) minCost=Math.min(minCost,p.cost);
		System.out.println(minCost);
	}
}
