package com.euler;

public class Euler686 {
	private final static int START_DIGITS=123;
	private final static int GOAL=678909;	// D'oh!
	
	public static void main(String[] args)	{
		double log2=Math.log10(2d);
		double smallLog=Math.log10(START_DIGITS);
		double bigLog=Math.log10(1+START_DIGITS);
		int count=0;
		for (;;)	{
			double n=Math.floor(smallLog/log2);
			double n2=Math.floor(bigLog/log2);
			if (n!=n2)	{
				++count;
				if (count==GOAL)	{
					System.out.println((int)n2);
					return;
				}
			}
			++smallLog;
			++bigLog;
		}
	}
}
