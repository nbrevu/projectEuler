package com.euler;

import com.euler.common.Timing;

public class Euler120 {
	private final static long LIMIT=1000;
	
	private static long solve()	{
		long N=(LIMIT-2)/2;	// Works only for even values of LIMIT!
		return (N*(11+N*(30+16*N)))/6;
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler120::solve);
	}
}
