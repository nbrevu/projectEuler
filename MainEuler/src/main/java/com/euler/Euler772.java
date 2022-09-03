package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler772 {
	private static class Partition	{
		private final int[] bins;
		private Partition(int[] bins)	{
			this.bins=bins;
		}
		public static Partition of(int length)	{
			return new Partition(new int[length]);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(bins);
		}
		@Override
		public boolean equals(Object other)	{
			Partition pOther=(Partition)other;
			return Arrays.equals(bins,pOther.bins);
		}
		@Override
		public String toString()	{
			return Arrays.toString(bins);
		}
		// Returns true if this partition contains the smaller one.
		public boolean checkContains(Partition smaller)	{
			for (int i=0;i<bins.length;++i) if (bins[i]<smaller.bins[i]) return false;
			return true;
		}
		public boolean checkContains(Set<Partition> smaller)	{
			for (Partition p:smaller) if (checkContains(p)) return true;
			return false;
		}
		public Partition include(int toAdd)	{
			int[] newBins=Arrays.copyOf(bins,bins.length);
			++newBins[toAdd-1];
			return new Partition(newBins);
		}
	}
	
	public static int calculateF(int length)	{
		List<Set<Partition>> partitions=new ArrayList<>();
		partitions.add(Set.of(Partition.of(length)));
		for (int i=1;i<=length;++i)	{
			Set<Partition> newSet=new HashSet<>();
			for (int j=1;j<=i;++j)	{
				Set<Partition> source=partitions.get(partitions.size()-j);
				for (Partition p:source) newSet.add(p.include(j));
			}
			partitions.add(newSet);
		}
		for (int i=1+length;;++i)	{
			Set<Partition> newSet=new HashSet<>();
			for (int j=1;j<=length;++j)	{
				Set<Partition> source=partitions.get(partitions.size()-j);
				for (Partition p:source) newSet.add(p.include(j));
			}
			if ((i%2)==0)	{
				boolean isBalanced=true;
				Set<Partition> half=partitions.get(i/2);
				for (Partition p:newSet) if (!p.checkContains(half))	{
					isBalanced=false;
					break;
				}
				if (isBalanced) return i;
			}
			partitions.add(newSet);
			System.out.println("\t"+i+"...");
		}
	}
	
	public static void main(String[] args)	{
		for (int i=2;i<=10;++i)	{
			System.out.println(i+" => "+calculateF(i));
		}
	}
}
