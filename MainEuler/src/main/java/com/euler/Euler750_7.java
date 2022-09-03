package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Euler750_7 {
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
	
	private static class BaseConfiguration	{
		public final int[] reverseArray;
		public BaseConfiguration(int[] reverseArray)	{
			this.reverseArray=reverseArray;
		}
	}
	
	private static BaseConfiguration getInitialConfiguration(int size)	{
		int p=1;
		int mod=size+1;
		int[] reverseArray=new int[size];
		for (int i=0;i<size;++i)	{
			p*=3;
			p%=mod;
			reverseArray[p-1]=i;
		}
		return new BaseConfiguration(reverseArray);
	}
	
	private static class PartialSolution	{
		public final int cost;
		public final int heuristicCost;
		private final int[] visitedPositions;
		private PartialSolution(int cost,int additionalHeuristic,int[] visitedPositions)	{
			this.cost=cost;
			this.heuristicCost=additionalHeuristic+cost;
			this.visitedPositions=visitedPositions;
		}
		public static PartialSolution getInitial(int lastCardPosition)	{
			return new PartialSolution(0,Integer.MAX_VALUE,new int[] {lastCardPosition});
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
		private final BaseConfiguration conf;
		public SolutionEvolver(BaseConfiguration conf,int maxSize,Comparator<PartialSolution> comparator)	{
			this.conf=conf;
			this.maxSize=maxSize;
			this.comparator=comparator;
		}
		public int heuristicCost(int[] array,int nextIndex)	{
			// This is cacheable, but the memory cost is prohibitive.
			NavigableSet<Integer> availableIndices=new TreeSet<>();
			for (int i:array) availableIndices.add(i);
			int result=0;
			for (int i=nextIndex;i>=0;--i)	{
				Integer myIndex=conf.reverseArray[i];
				Integer lastBefore=availableIndices.lower(myIndex);
				Integer firstAfter=availableIndices.higher(myIndex);
				int diff1=(lastBefore==null)?Integer.MAX_VALUE:(myIndex-lastBefore);
				int diff2=(firstAfter==null)?Integer.MAX_VALUE:(firstAfter-myIndex);
				result+=Math.min(diff1,diff2);
				availableIndices.add(myIndex);
			}
			return result;
		}
		public List<PartialSolution> advance(Collection<PartialSolution> currentGeneration,int index)	{
			int nextPosition=conf.reverseArray[index];
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
			int nextIndex=index-1;
			for (Map.Entry<ArrayWrapper,Integer> entry:bestSolutions.entrySet())	{
				int cost=entry.getValue();
				int[] positions=entry.getKey().array;
				int heuristic=heuristicCost(positions,nextIndex);
				allSolutions.add(new PartialSolution(cost,heuristic,positions));
			}
			allSolutions.sort(comparator);
			if (allSolutions.size()<=maxSize) return allSolutions;
			int finalSize=maxSize;
			while (finalSize<maxSize-1) if (comparator.compare(allSolutions.get(finalSize),allSolutions.get(finalSize+1))==0) ++finalSize;
			else break;
			return allSolutions.subList(0,finalSize);
		}
	}
	
	public static void main(String[] args)	{
		BaseConfiguration conf=getInitialConfiguration(N);
		List<PartialSolution> sols=List.of(PartialSolution.getInitial(conf.reverseArray[N-1]));
		SolutionEvolver evolver=new SolutionEvolver(conf,20000,Comparator.comparingInt((PartialSolution s)->s.heuristicCost));
		for (int i=N-2;i>=0;--i)	{
			sols=evolver.advance(sols,i);
			System.out.println("Voy por "+i+" y tengo "+sols.size()+" soluciones.");
		}
		int minCost=Integer.MAX_VALUE;
		for (PartialSolution p:sols) minCost=Math.min(minCost,p.cost);
		System.out.println(minCost);
	}
}
