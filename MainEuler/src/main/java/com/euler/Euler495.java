package com.euler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Euler495 {
	// This is just a stub...
	/*
	 * ZUTUN!
	 * We need a method to solve a generic equation a1·x1+a2·x2+...an·xn=k.
	 * We also need (THIS IS THE DIFFICULT PART) to determine which coefficient multiplies each partition. Some are obvious. Other... not so much.
	 * 
	 * ALSO: I just realized why this problem is so difficult. For each partition you will get an amount of combinations, BUT because of repetition,
	 * you need to divide. For example, for partition 1,1,1,2,2,3 you need to divide by (3!·2!) because 1 is repeated thrice and two is repeated
	 * twice. However, the division must be done AFTER subtracting, meaning that you need to create a graph of partitions and be very careful,
	 * subtracting when you are finished with a partition and dividing after. This requires a level of finesse that I'm not sure I can pull,
	 * especially considering that I don't even have the proper multipliers calculated.
	 * 
	 * I now understand why this has 100%.
	 */
	private static class Partition	{
		private final int[] numbers;
		public Partition()	{
			numbers=new int[0];
		}
		public Partition(int[] numbers)	{
			Arrays.sort(numbers);
			this.numbers=numbers;
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				Partition pOther=(Partition)other;
				return Arrays.equals(numbers,pOther.numbers);
			}	catch (ClassCastException exc)	{
				return false;
			}
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(numbers);
		}
		public Partition addNumber(int number)	{
			int N=numbers.length;
			int[] newArray=new int[N+1];
			System.arraycopy(numbers,0,newArray,0,N);
			newArray[N]=number;
			return new Partition(newArray);
		}
	}
	
	private static class PartitionSet	{
		private final Set<Partition> set;
		public PartitionSet()	{
			set=new HashSet<>();
		}
		public PartitionSet(Set<Partition> set)	{
			this.set=set;
		}
		public Set<Partition> get()	{
			return set;
		}
		public void addNumberAndAddResultToSet(int n,PartitionSet destination)	{
			for (Partition p:set) destination.set.add(p.addNumber(n));
		}
	}
	
	private static PartitionSet[] getPartitions(int N)	{
		PartitionSet[] result=new PartitionSet[1+N];
		Partition basePartition=new Partition();
		result[0]=new PartitionSet(Collections.singleton(basePartition));
		for (int i=1;i<=N;++i)	{
			PartitionSet holder=new PartitionSet();
			for (int j=0;j<i;++j) result[j].addNumberAndAddResultToSet(i-j,holder);
			System.out.println(""+i+": "+holder.get().size());
			result[i]=holder;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		PartitionSet[] result=getPartitions(30);
		System.out.println("Tengo "+result[30].get().size()+" particiones.");
		int counter=0;
		for (Partition part:result[30].get()) if (part.numbers[0]==1) ++counter;
		System.out.println("Sólo me valen "+counter+". No son tantas, ODER?");
	}
}
