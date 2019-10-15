package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Timing;
import com.google.common.math.IntMath;

public class Euler129 {
	private final static int MIN=IntMath.pow(10,6);
	
	private final static int[] END_DIGITS=new int[] {1,3,7,9};
	
	private static long solve()	{
		for (int i=MIN;;i+=10) for (int d:END_DIGITS)	{
			int n=i+d;
			if (EulerUtils.minRepunitDivisible(n)>MIN) return n;
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler129::solve);
	}
}
