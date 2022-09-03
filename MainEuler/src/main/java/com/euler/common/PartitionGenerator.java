package com.euler.common;

import java.util.ArrayList;
import java.util.List;

public class PartitionGenerator {
	public static List<int[]> generatePartitions(int size)	{
		List<int[]> result=new ArrayList<>();
		generatePartitionsRecursive(result,0,1,size);
		return result;
	}
	
	/*
	 * I DID IT. I MANAGED IT. This method generates partitions recursively WITHOUT using intermediate lists.
	 * 
	 * The trick is that the recursion stack contains all the intermediate numbers, and they are filled into the appropriate bins of the array
	 * when the array is already created.
	 */
	private static void generatePartitionsRecursive(List<int[]> holder,int currentPosition,int nextToAdd,int remaining)	{
		int lastPosition=holder.size();
		if (remaining==0)	{
			/*
			 * If remaining==0, we have finished the partition, so we create an empty array and we return immediately.
			 * The caller method will fill the details.
			 */
			holder.add(new int[currentPosition]);
			return;
		}
		/*
		 * The invariant of this loop is: all the arrays in "holder" up to "lastPosition" have had its "currentPosition"th element filled.
		 * This means that the call to generatePartitionsRecursive might have added a few of them, which are the ones from "lastPosition" to
		 * the current size. These are the ones whose "currentPosition"th element must be assigned to the current value to be added.
		 */
		for (int i=nextToAdd;i<=remaining;++i)	{
			generatePartitionsRecursive(holder,1+currentPosition,i,remaining-i);
			for (int j=lastPosition;j<holder.size();++j) holder.get(j)[currentPosition]=i;
			lastPosition=holder.size();
		}
	}
}
