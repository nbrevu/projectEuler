package com.euler;

import com.euler.common.Timing;

public class Euler114 {
	private final static int N=50;
	private static long solve()	{
		long[][] combinations=new long[N+1][2];
		combinations[0][0]=1l;
		combinations[0][1]=0l;
		for (int i=1;i<=N;++i)	{
			combinations[i][0]=combinations[i-1][0]+combinations[i-1][1];
			for (int j=0;j<=i-3;++j) combinations[i][1]+=combinations[j][0];
		}
		return combinations[N][0]+combinations[N][1];
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler114::solve);
	}
}
