package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.euler.common.ArrayPartition;
import com.euler.common.ArrayPartitionGenerator;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.euler.common.Timing;
import com.google.common.collect.Collections2;
import com.google.common.math.IntMath;

public class Euler118 {
	private static int build(int[] array)	{
		int result=array[0];
		for (int i=1;i<array.length;++i) result=10*result+array[i];
		return result;
	}
	
	private static boolean isSetValid(int[] digits,ArrayPartition partition,boolean[] composites)	{
		List<int[]> partitioned=partition.separate(digits);
		int[] numbers=new int[partitioned.size()];
		for (int i=0;i<numbers.length;++i)	{
			numbers[i]=build(partitioned.get(i));
			if ((i>0)&&(numbers[i]<numbers[i-1])) return false;
			else if (composites[numbers[i]]) return false;
		}
		return true;
	}
	
	private static long solve()	{
		boolean[] composites=Primes.sieve(IntMath.pow(10,8));
		ArrayPartitionGenerator generator=new ArrayPartitionGenerator(9);
		List<ArrayPartition> partitions=new ArrayList<>(Collections2.filter(generator.generatePartitions(),ArrayPartition::canHoldPrimes));
		int[] digits=new int[9];
		Arrays.setAll(digits,(int i)->i+1);
		int count=0;
		do for (ArrayPartition partition:partitions) if (isSetValid(digits,partition,composites)) ++count;
		while (EulerUtils.nextPermutation(digits));
		return count;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler118::solve);
	}
}
