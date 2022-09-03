package com.euler;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;

public class Euler483 {
	private final static int N=11;
	
	private static class PermutationCounter	{
		private final int[] baseOrder;
		private int[] arr1,arr2;
		public PermutationCounter(int n)	{
			baseOrder=new int[n];
			for (int i=0;i<n;++i) baseOrder[i]=i;
			arr1=new int[n];
			arr2=new int[n];
		}
		public int[] getOriginalPermutation()	{
			return Arrays.copyOf(baseOrder,baseOrder.length);
		}
		public boolean advance(int[] array)	{
			return EulerUtils.nextPermutation(array);
		}
		public int countOrder(int[] permutation)	{
			int[] a=arr1;
			int[] b=arr2;
			System.arraycopy(baseOrder,0,a,0,baseOrder.length);
			int result=0;
			for (;;)	{
				applyPermutation(permutation,a,b);
				++result;
				if (Arrays.equals(b,baseOrder)) return result;
				int[] swap=a;
				a=b;
				b=swap;
			}
		}
		private void applyPermutation(int[] permutation,int[] source,int[] target)	{
			for (int i=0;i<permutation.length;++i) target[permutation[i]]=source[i];
		}
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=N;++i)	{
			PermutationCounter permutations=new PermutationCounter(i);
			SortedMap<Integer,Integer> counters=new TreeMap<>();
			int[] perm=permutations.getOriginalPermutation();
			long num=0;
			long den=0;
			do	{
				int order=permutations.countOrder(perm);
				EulerUtils.increaseCounter(counters,order);
				num+=order*order;
				++den;
			}	while (permutations.advance(perm));
			System.out.println(String.format("g(%d)=%d/%d.",i,num,den));
			System.out.println(counters);
		}
	}
}
