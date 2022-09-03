package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler333_2 {
	// Correct result at the first ACTUAL try! (3053105)
	private final static int LIMIT=1000000;
	
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
		public Partition next;
		public int myFactor;
		public Partition(int factor)	{
			next=null;
			myFactor=factor;
		}
		public Partition(Partition parent,int newFactor)	{
			next=parent;
			myFactor=newFactor;
		}
		public boolean canAdd(int factor)	{
			if ((factor%myFactor)==0) return false;
			return (next==null)?true:(next.canAdd(factor));
		}
	}
	
	public static class PartitionList	{
		private List<Partition> partitions;
		public PartitionList()	{
			this.partitions=new ArrayList<>();
		}
		public List<Partition> children(int n)	{
			List<Partition> result=new ArrayList<>();
			for (Partition prev:partitions) if (prev.canAdd(n)) result.add(new Partition(prev,n));
			return result;
		}
		public boolean isSingleton()	{
			return partitions.size()==1;
		}
		public void add(List<Partition> morePartitions)	{
			partitions.addAll(morePartitions);
		}
		public void add(Partition partition)	{
			partitions.add(partition);
		}
	}
	
	private static PartitionList[] getAllLists(int upTo,SortedSet<Integer> available)	{
		PartitionList[] result=new PartitionList[upTo];
		for (int i=0;i<upTo;++i) result[i]=new PartitionList();
		int last=0;
		for (int augend:available)	{
			for (int i=0;i<=last;++i)	{
				int next=i+augend;
				if (next>=upTo) break;
				result[next].add(result[i].children(augend));
			}
			result[augend].add(new Partition(augend));
			last+=augend;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SortedSet<Integer> multiples=getMultiplesOf2And3(LIMIT);
		boolean composites[]=Primes.sieve(LIMIT);
		int lastPrime=LIMIT-1;
		while (composites[lastPrime]) --lastPrime;
		PartitionList partitions[]=getAllLists(lastPrime+1,multiples);
		long sum=2;
		for (int i=3;i<LIMIT;i+=2) if ((!composites[i])&&partitions[i].isSingleton()) sum+=(long)i;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(sum);
	}
}
