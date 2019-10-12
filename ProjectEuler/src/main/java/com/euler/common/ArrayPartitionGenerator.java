package com.euler.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayPartitionGenerator	{
	private final int size;
	private final int[] partition;
	public ArrayPartitionGenerator(int size)	{
		this.size=size;
		partition=new int[size];
	}
	public List<ArrayPartition> generatePartitions()	{
		List<ArrayPartition> result=new ArrayList<>();
		generatePartitionsRecursive(result,0,1,0);
		return result;
	}
	private void generatePartitionsRecursive(List<ArrayPartition> result,int index,int value,int sum)	{
		for (int i=value;i<=size-sum;++i)	{
			partition[index]=i;
			if (i+sum==size) result.add(new ArrayPartition(Arrays.copyOf(partition,1+index)));
			else generatePartitionsRecursive(result,index+1,i,sum+i);
		}
	}
}