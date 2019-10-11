package com.euler;

import java.util.Arrays;

import com.euler.common.Timing;

public class Euler113 {
	private final static int NUM_DIGITS=100;
	
	private static long sum(long[] in)	{
		long result=0l;
		for (long n:in) result+=n;
		return result;
	}
	
	private static long getIncreasing(int n)	{
		long[] current=new long[9];
		long[] next=new long[9];
		Arrays.fill(next,1l);
		long result=sum(next);
		for (int i=2;i<=n;++i)	{
			System.arraycopy(next,0,current,0,9);
			for (int j=0;j<9;++j) for (int k=0;k<j;++k) next[j]+=current[k];
			result+=sum(next);
		}
		return result;
	}
	
	private static long getDecreasing(int n)	{
		long[] current=new long[10];
		long[] next=new long[10];
		Arrays.fill(next,1,10,1l);
		long result=sum(next);
		for (int i=2;i<=n;++i)	{
			System.arraycopy(next,0,current,0,10);
			for (int j=0;j<10;++j) for (int k=j+1;k<10;++k) next[j]+=current[k];
			result+=sum(next);
		}
		return result;
	}
	
	private static long getSameDigits(long n)	{
		return 9*n;
	}
	
	private static long solve()	{
		return getIncreasing(NUM_DIGITS)+getDecreasing(NUM_DIGITS)-getSameDigits(NUM_DIGITS);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler113::solve);
	}
}
