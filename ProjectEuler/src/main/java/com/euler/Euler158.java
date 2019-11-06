package com.euler;

import com.euler.common.Timing;

public class Euler158 {
	private final static int N=26;
	
	private static long combinatorialNumber(long n,long k)	{
		long result=1l;
		for (long i=n-k+1;i<=n;++i) result*=i;
		for (long i=1;i<=k;++i) result/=i;
		return result;
	}
	
	private static long solve()	{
		int maxPos=N-(N/3);
		long factor=(1<<maxPos)-maxPos-1;
		return factor*combinatorialNumber(N,N-maxPos);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler158::solve);
	}
}
