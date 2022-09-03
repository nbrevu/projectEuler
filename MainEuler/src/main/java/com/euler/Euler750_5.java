package com.euler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Euler750_5 {
	private final static int N=976;
	
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
		public List<PartialSolution> getChildren(int nextPosition)	{
			List<PartialSolution> result=new ArrayList<>(visitedPositions.length);
			for (int i=0;i<visitedPositions.length;++i)	{
				int[] newVisitedPositions=new int[i+2];
				System.arraycopy(visitedPositions,0,newVisitedPositions,0,i+1);
				newVisitedPositions[i+1]=nextPosition;
				result.add(new PartialSolution(cost+Math.abs(nextPosition-newVisitedPositions[i]),newVisitedPositions));
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
			List<PartialSolution> allSolutions=new ArrayList<>();
			for (PartialSolution sol:currentGeneration) allSolutions.addAll(sol.getChildren(nextPosition));
			allSolutions.sort(comparator);
			if (allSolutions.size()<=maxSize) return allSolutions;
			int finalSize=maxSize;
			while (finalSize<maxSize-1) if (comparator.compare(allSolutions.get(finalSize),allSolutions.get(finalSize+1))==0) ++finalSize;
			else break;
			return allSolutions.subList(0,finalSize);
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
		/*
		 * Still doesn't work :(. For X=1000000 the result is 173950 (after 10 minutes!!).
		 * For X=50000 the result is... 173940?????? I have some idea about how this would be possible: greediness regarding the cost is just a bad idea.
		 */
		int[] reverseArray=getPositionsArray(N);
		List<PartialSolution> sols=List.of(PartialSolution.getInitial(reverseArray[N-1]));
		//SolutionEvolver evolver=new SolutionEvolver(1000,Comparator.comparingDouble((PartialSolution s)->s.cost/Math.log(s.visitedPositions.length)));
		SolutionEvolver evolver=new SolutionEvolver(1000,Comparator.comparingInt((PartialSolution s)->s.cost));
		for (int i=N-2;i>=0;--i)	{
			sols=evolver.advance(sols,reverseArray[i]);
			System.out.println("Voy por "+i+" y tengo "+sols.size()+" soluciones.");
		}
		int minCost=Integer.MAX_VALUE;
		for (PartialSolution p:sols) minCost=Math.min(minCost,p.cost);
		System.out.println(minCost);
	}
}
