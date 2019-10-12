package com.euler;

import com.euler.common.Timing;

public class Euler116 {
	private final static int N=50;
	
	private static long getReplacements(int colorBar,int rowLength)	{
		long[] result=new long[1+rowLength];
		for (int i=0;i<colorBar;++i) result[i]=1l;
		for (int i=colorBar;i<=rowLength;++i) result[i]=result[i-1]+result[i-colorBar];
		return result[rowLength]-1;
	}
	
	private static long solve()	{
		return getReplacements(2,N)+getReplacements(3,N)+getReplacements(4,N);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler116::solve);
	}
}
