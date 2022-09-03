package com.euler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler491 {
	private final static long NINE_FACTORIAL=362880l;
	private final static long TEN_FACTORIAL=3628800l;
	
	private static class Combination	{
		private final int[] occurrences;
		private final int last;
		public Combination()	{
			occurrences=new int[10];
			last=0;
		}
		private Combination(int[] occurrences,int last)	{
			this.occurrences=occurrences.clone();
			this.last=last;
		}
		public List<Combination> getChildren()	{
			if (isComplete()) System.out.println("T'has pasao, macho t'has paseo.");
			List<Combination> result=new ArrayList<>();
			for (int i=last;i<10;++i)	{
				++occurrences[i];
				int last=(occurrences[i]==2)?(i+1):i;
				result.add(new Combination(occurrences,last));
				--occurrences[i];
			}
			return result;
		}
		public boolean isComplete()	{
			int sum=0;
			for (int i=0;i<10;++i) sum+=occurrences[i];
			return sum==10;
		}
		public boolean isValid()	{
			int sum=0;
			for (int i=0;i<10;++i)	{
				if (occurrences[i]==0) sum+=i;
				else if (occurrences[i]==2) sum-=i;
			}
			return (sum%11)==0;
		}
		public long getCombinations()	{
			long result=NINE_FACTORIAL*TEN_FACTORIAL*(10-occurrences[0]);
			for (int i=0;i<10;++i) if (occurrences[i]!=1) result/=2l;
			return result;
		}
		@Override
		public boolean equals(Object other)	{
			return occurrences.equals(((Combination)other).occurrences);
		}
		@Override
		public int hashCode()	{
			return occurrences.hashCode();
		}
	}
	
	public static Set<Combination> getAllCombinations()	{
		Set<Combination> currGen=new HashSet<>();
		currGen.add(new Combination());
		for (int i=1;i<=10;++i)	{
			// System.out.println(""+(i-1)+": "+currGen.size());
			Set<Combination> nextGen=new HashSet<>();
			for (Combination c:currGen) nextGen.addAll(c.getChildren());
			currGen=nextGen;
		}
		for (Combination c:currGen) if (!c.isComplete()) System.out.println("No llegas.");
		return currGen;
	}
	
	public static void main(String[] args)	{
		Set<Combination> combinations=getAllCombinations();
		long sum=0l;
		for (Combination c:combinations) if (c.isValid()) sum+=c.getCombinations();
		System.out.println(sum);
	}
}
