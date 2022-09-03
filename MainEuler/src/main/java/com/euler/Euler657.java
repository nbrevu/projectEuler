package com.euler;

import java.util.Arrays;

import com.koloboke.collect.set.hash.HashIntSets;

public class Euler657 {
	private final static int S=4;
	private final static int N=4;
	
	private static int countDifferentElements(int[] array)	{
		return HashIntSets.newImmutableSet(array).size();
	}
	
	public static void main(String[] args)	{
		int[] counters=new int[S+1];
		counters[0]=1;
		for (int k=1;k<=N;++k)	{
			int[] array=new int[k];
			for (;;)	{
				int diff=countDifferentElements(array);
				++counters[diff];
				int index=k-1;
				while (index>=0)	{
					++array[index];
					if (array[index]<S) break;
					--index;
				}
				if (index==-1) break;
				for (++index;index<k;++index) array[index]=0;
			}
		}
		System.out.println(Arrays.toString(counters));
	}
}
