package com.euler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Euler767 {
	/*
	 * If SIZE=2, K=10^8 and N=10^16, this is exactly problem 743.
	 * If SIZE=16, K=10^5 and N=10^16, this is problem 767.
	 */
	/*-
	private final static int SIZE=2;
	private final static int K=IntMath.pow(10,8);
	private final static long N=LongMath.pow(10l,16);
	private final static long MOD=1_000_000_007l;
	*/
	
	private static class Solution	{
		private final int[] values;
		private Solution(int[] values)	{
			this.values=values;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(values);
		}
		@Override
		public boolean equals(Object other)	{
			Solution sOther=(Solution)other;
			return Arrays.equals(values,sOther.values);
		}
		public static Solution getInitialSolution(int size)	{
			return new Solution(new int[1+size]);
		}
		public Solution add(int toAdd)	{
			/*
			 * We take a solution for certain K, and we generate a new one for K+toAdd by adding one element in the "toAdd" bin and (toAdd-1) in
			 * the "0" bin. 
			 */
			int[] copy=Arrays.copyOf(values,values.length);
			++copy[toAdd];
			copy[0]+=toAdd-1;
			return new Solution(copy);
		}
	}
	
	/*
	 * Dynamic programming. We use a collection where, in index I, we have all the solutions to the equation system for size N.
	 * To save space, we use a queue and only the last SIZE elements (the ones needed for the dynamic programming approach) are saved.
	 * There are an awful lot of duplicates, so there probably is a much better way to do this.
	 */
	private static Set<Solution> findAllSolutions(int size,int k)	{
		LinkedList<Set<Solution>> solutions=new LinkedList<>();
		// Initial set.
		Set<Solution> zeroSol=Set.of(Solution.getInitialSolution(size));
		solutions.add(zeroSol);
		for (int i=1;i<size;++i)	{
			Set<Solution> nSols=new HashSet<>();
			for (int j=0;j<i;++j) for (Solution s:solutions.get(j)) nSols.add(s.add(i-j));
			solutions.addLast(nSols);
		}
		// Right now we have the solutions from 0 to SIZE-1. We can now use the generic iterative procedure.
		for (int i=size;i<=k;++i)	{
			Set<Solution> nSols=new HashSet<>();
			for (int j=0;j<size;++j) for (Solution s:solutions.get(j)) nSols.add(s.add(size-j));
			solutions.remove();
			solutions.addLast(nSols);
		}
		return solutions.getLast();
	}
	
	public static void main(String[] args)	{
		Set<Solution> allSolutions=findAllSolutions(16,60);
		System.out.println(allSolutions.size());
	}
}
