package com.euler;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler333 {
	private final static int LIMIT=100;
	
	private static SortedSet<Integer> getMultiplesOf2And3(int upTo)	{
		int n=1;
		SortedSet<Integer> result=new TreeSet<>();
		while (n<upTo)	{
			result.add(n);
			for (int m=n*3;m<=upTo;m*=3) result.add(m);
			n+=n;
		}
		return result;
	}
	
	private static class Partition	{
		private Set<Integer> factors;
		public Partition(int factor)	{
			factors=new TreeSet<>();
			factors.add(factor);
		}
		public Partition(Partition parent,int newFactor)	{
			this.factors=new TreeSet<>(parent.factors);
			this.factors.add(newFactor);
		}
		public boolean canAdd(int factor)	{
			for (int i:factors) if (((i%factor)==0)||((factor%i)==0)) return false;
			return true;
		}
		@Override
		public boolean equals(Object other)	{
			Partition pOther=(Partition)other;
			return pOther.factors.equals(factors);
		}
		@Override
		public int hashCode()	{
			return factors.hashCode();
		}
		@Override
		public String toString()	{
			return factors.toString();
		}
	}
	
	public static class PartitionList	{
		private Set<Partition> partitions;
		public PartitionList(Set<Partition> partitions)	{
			this.partitions=partitions;
		}
		public Set<Partition> children(int n)	{
			Set<Partition> result=new HashSet<>();
			for (Partition prev:partitions) if (prev.canAdd(n)) result.add(new Partition(prev,n));
			return result;
		}
		public boolean isSingleton()	{
			return partitions.size()==1;
		}
		@Override
		public String toString()	{
			return partitions.toString();
		}
	}
	
	private static PartitionList[] getAllLists(int upTo,SortedSet<Integer> available)	{
		PartitionList[] result=new PartitionList[upTo];
		for (int i=1;i<upTo;++i)	{
			if ((i%10000)==0) System.out.println(""+i+"...");
			Set<Partition> allPartitions=new HashSet<>();
			for (int n:available) if (n>i) break;
			else if (n==i) allPartitions.add(new Partition(n));
			else allPartitions.addAll(result[i-n].children(n));
			result[i]=new PartitionList(allPartitions);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		SortedSet<Integer> multiples=getMultiplesOf2And3(LIMIT);
		boolean composites[]=Primes.sieve(LIMIT);
		int lastPrime=LIMIT-1;
		while (composites[lastPrime]) --lastPrime;
		PartitionList partitions[]=getAllLists(lastPrime,multiples);
		long sum=2;
		for (int i=3;i<LIMIT;i+=2) if ((!composites[i])&&partitions[i].isSingleton()) sum+=(long)i;
		System.out.println(sum);
	}
}
