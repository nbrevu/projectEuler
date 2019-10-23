package com.euler;

import com.euler.common.Timing;

public class Euler145 {
	private final static int MAX_DIGITS=9;
	
	private static long pow(long l,int p)	{
		// This is SO much faster than LongMath.pow...
		long result=1l;
		for (int i=0;i<p;++i) result*=l;
		return result;
	}
	
	private static long getReversible(int amountOfDigits)	{
		if ((amountOfDigits%2)==0) return 20*pow(30l,((amountOfDigits)/2)-1);
		else if ((amountOfDigits%4)==3) return 100*pow(500l,((amountOfDigits-3)/4));
		else return 0l;
	}
	
	private static long solve()	{
		long result=0;
		for (int i=2;i<=MAX_DIGITS;++i) result+=getReversible(i);
		return result;
	}

	public static void main(String[] args)	{
		Timing.time(Euler145::solve);
	}
}
