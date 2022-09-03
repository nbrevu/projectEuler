package com.euler;

import java.util.HashSet;
import java.util.Set;

public class Euler637_2 {
	// This can be optimized A LOT... but this is fast enough. It ran while I was cooking a REIS MIT DEN DREI LECKERE DINGE!
	private final static int LIMIT=10000000;
	private final static int B1=3;
	private final static int B2=10;
	
	private static void getDigitPartitionSumsRecursive(int n,int base,int currentSum,Set<Integer> result,boolean isFirst)	{
		if (!isFirst) result.add(n+currentSum);
		int basePower=base;
		while (n>=basePower)	{
			int q=n/basePower;
			int r=n%basePower;
			getDigitPartitionSumsRecursive(q,base,currentSum+r,result,false);
			basePower*=base;
		}
	}
	
	private static Set<Integer> getDigitPartitionSums(int n,int base)	{
		Set<Integer> result=new HashSet<>();
		getDigitPartitionSumsRecursive(n,base,0,result,true);
		return result;
	}
	
	private static int getMinimum(int[] array,Set<Integer> indices)	{
		int result=Integer.MAX_VALUE;
		for (int index:indices) result=Math.min(result,array[index]);
		return result;
	}
	
	private static int[] countSteps(int limit,int base)	{
		int[] result=new int[1+limit];
		for (int i=0;i<base;++i) result[i]=0;
		for (int i=base;i<=limit;++i)	{
			Set<Integer> digitSums=getDigitPartitionSums(i,base);
			result[i]=1+getMinimum(result,digitSums);
		}
		return result;
	}
	
	private static long getSumOfMatches(int limit,int base1,int base2)	{
		int[] a1=countSteps(limit,base1);
		int[] a2=countSteps(limit,base2);
		long result=0;
		for (int i=0;i<=limit;++i) if (a1[i]==a2[i]) result+=i;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		System.out.println(getSumOfMatches(LIMIT,B1,B2));
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
