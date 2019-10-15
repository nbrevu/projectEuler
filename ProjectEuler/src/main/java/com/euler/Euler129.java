package com.euler;

import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler129 {
	private final static int MIN=IntMath.pow(10,6);
	
	private final static int[] END_DIGITS=new int[] {1,3,7,9};
	
	private static int minRepunitDivisible(int n)	{
		int count=1;
		int mod=1;
		for (;;)	{
			mod%=n;
			if (mod==0) return count;
			++count;
			mod=10*mod+1;
		}
	}
	
	private static long solve()	{
		for (int i=MIN;;i+=10) for (int d:END_DIGITS)	{
			int n=i+d;
			if (minRepunitDivisible(n)>MIN) return n;
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler129::solve);
	}
}
