package com.euler;

import com.euler.common.Timing;

public class Euler126 {
	private final static int GOAL=1000;
	
	private final static int LIMIT=30000;
	
	private static int getCubes(int x,int y,int z,int n)	{
		return 2*(x*y+x*z+y*z)+4*(x+y+z+n-2)*(n-1);
	}
	
	private static long solve()	{
		int[] counters=new int[1+LIMIT];
		for (int z=1;getCubes(z,z,z,1)<=LIMIT;++z) for (int y=z;getCubes(z,y,z,1)<=LIMIT;++y) for (int x=y;getCubes(z,y,x,1)<=LIMIT;++x) for (int n=1;getCubes(z,y,x,n)<=LIMIT;++n) counters[getCubes(z,y,x,n)]++;
		for (int i=0;i<=LIMIT;++i) if (counters[i]==GOAL) return i;
		throw new RuntimeException("Nicht möglich.");
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler126::solve);
	}
}
