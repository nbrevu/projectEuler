package com.euler;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Euler374 {
	private static class PartitionHolder	{
		public final long product;
		public final int amount;
		public PartitionHolder(long product,int amount)	{
			this.product=product;
			this.amount=amount;
		}
	}
	
	private static class OptimalPartitions	{
		private SortedMap<Long,PartitionHolder> biggestNumberToOptimalPartition;
		public OptimalPartitions(long n)	{
			biggestNumberToOptimalPartition=new TreeMap<>();
			PartitionHolder singletonPartition=new PartitionHolder(n,1);
			biggestNumberToOptimalPartition.put(n,singletonPartition);
		}
		public void addPartition(long biggest,PartitionHolder partition)	{
			PartitionHolder previous=biggestNumberToOptimalPartition.get(biggest);
			if ((previous==null)||(previous.product<partition.product)) biggestNumberToOptimalPartition.put(biggest,partition);
		}
		public PartitionHolder getChild(long toAdd)	{
			PartitionHolder result=null;
			for (Map.Entry<Long,PartitionHolder> entry:biggestNumberToOptimalPartition.entrySet())	{
				long biggest=entry.getKey();
				if (biggest>=toAdd) break;
				PartitionHolder partition=entry.getValue();
				PartitionHolder newPartition=new PartitionHolder(partition.product*toAdd,partition.amount+1);
				if ((result==null)||(result.product<newPartition.product)) result=newPartition;
			}
			return result;
		}
		public PartitionHolder getOptimal()	{
			PartitionHolder result=null;
			for (PartitionHolder partition:biggestNumberToOptimalPartition.values()) if ((result==null)||(result.product<partition.product)) result=partition;
			return result;
		}
	}
	
	private final static int SIZE=100;
	
	public static void main(String[] args)	{
		OptimalPartitions[] partitions=new OptimalPartitions[SIZE];
		partitions[0]=new OptimalPartitions(1);
		for (int i=1;i<SIZE;++i)	{
			partitions[i]=new OptimalPartitions((long)(i+1));
			for (int j=0;j<i;++j)	{
				long diff=(long)(i-j);
				PartitionHolder newPartition=partitions[j].getChild(diff);
				if (newPartition==null) break;
				partitions[i].addPartition(diff,newPartition);
			}
		}
		long sum=0l;
		for (int i=0;i<SIZE;++i)	{
			PartitionHolder optimal=partitions[i].getOptimal();
			long pr=optimal.product*(long)(optimal.amount);
			sum+=pr;
			System.out.println("f("+(i+1)+")="+optimal.product+", m("+(i+1)+")="+optimal.amount+" => "+pr+" ["+sum+"] [["+(sum%982451653l)+"]].");
		}
		System.out.println(sum);
	}
}
