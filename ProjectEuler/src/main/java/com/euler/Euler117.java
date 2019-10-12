package com.euler;

import com.euler.common.Timing;

public class Euler117 {
	private final static int N=50;
	
	private static long solve()	{
		long[] result=new long[1+N];
		result[0]=1l;
		result[1]=1l;
		result[2]=2l;
		result[3]=4l;
		result[4]=8l;
		for (int i=5;i<=N;++i) result[i]=result[i-1]+result[i-1]-result[i-5];
		return result[N];
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler117::solve);
	}
}
