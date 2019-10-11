package com.euler;

import com.euler.common.Timing;

public class Euler112 {
	private static boolean isNonBouncy(int i)	{
		int currentSign=0;
		int prevDigit=i%10;
		i/=10;
		while (i>0)	{
			int nextDigit=i%10;
			i/=10;
			if (nextDigit==prevDigit) continue;
			if (currentSign==0)	currentSign=prevDigit-nextDigit;
			else if (currentSign<0)	{
				if (prevDigit>nextDigit) return false;
			}	else if (prevDigit<nextDigit) return false;
			prevDigit=nextDigit;
		}
		return true;
	}
	
	private static long solve()	{
		int nonbouncy=100;
		for (int i=101;;++i)	{
			if (isNonBouncy(i)) ++nonbouncy;
			if (nonbouncy*100==i) return i;
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler112::solve);
	}
}
