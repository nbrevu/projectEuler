package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Euler750_10 {
	private final static int N=976;
	
	private static class Cost implements Comparable<Cost>	{
		public final int chosenTarget;
		public final int realCost;
		public final int virtualCost;
		public Cost(int chosenTarget,int realCost,int virtualCost)	{
			this.chosenTarget=chosenTarget;
			this.realCost=realCost;
			this.virtualCost=virtualCost;
		}
		@Override
		public int compareTo(Cost other)	{
			int result=Long.compare(virtualCost,other.virtualCost);
			if (result!=0) return result;
			else return Integer.compare(chosenTarget,other.chosenTarget);	// This can never result in 0.
		}
	}
	
	private static class VirtualCostStorage	{
		private static int[][] createDirectCostMatrix(int size)	{
			int[] reverseMap=new int[size];	// reverseMap[n] is the position of card n.
			int p=1;
			int mod=size+1;
			for (int i=0;i<size;++i)	{
				p*=3;
				p%=mod;
				reverseMap[p-1]=i;
			}
			/*
			 * To make index more logical while still zero-based, the first index is the target, and the second one is the source.
			 * Therefore each target admits sources that are less to it, so every index in subarrays starts from 0.
			 */
			int[][] result=new int[size][size];
			for (int target=0;target<size;++target)	{
				result[target]=new int[target];
				for (int source=0;source<target;++source) result[target][source]=Math.abs(reverseMap[target]-reverseMap[source]);
			}
			return result;
		}
		private final List<List<Cost>> sortedCostsBySource;
		public VirtualCostStorage(int size)	{
			sortedCostsBySource=new ArrayList<>(size-1);
			int[][] directCosts=createDirectCostMatrix(size);
			for (int s=0;s<size-1;++s)	{
				List<Cost> sCosts=new ArrayList<>(size-s);
				for (int t=s+1;t<size;++t)	{
					int realCost=directCosts[t][s];
					int virtualCost=realCost;
					if (t!=s+1) for (int a=0;a<s;++a)	{
						List<Cost> aCosts=sortedCostsBySource.get(a);
						int bestCost=aCosts.get(0).virtualCost;
						int newBestCost=Integer.MAX_VALUE;
						for (Cost c:aCosts) if ((c.chosenTarget<=s)||(c.chosenTarget>=t))	{
							newBestCost=c.virtualCost;
							break;
						}
						virtualCost+=newBestCost-bestCost;
					}
					sCosts.add(new Cost(t,realCost,virtualCost));
				}
				sCosts.sort(null);
				sortedCostsBySource.add(sCosts);
			}
		}
		public int getBestCost()	{
			int size=sortedCostsBySource.size();
			BitSet availablePositions=new BitSet(1+size);
			availablePositions.set(0,2+size);
			int result=0;
			for (int i=size-1;i>=0;--i)	{
				Cost bestCost=null;
				for (Cost c:sortedCostsBySource.get(i)) if (availablePositions.get(c.chosenTarget))	{
					bestCost=c;
					break;
				}
				result+=bestCost.realCost;
				availablePositions.set(i+1,bestCost.chosenTarget,false);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		// Shit, still doesn't work :(.
		long tic=System.nanoTime();
		int result=new VirtualCostStorage(N).getBestCost();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
