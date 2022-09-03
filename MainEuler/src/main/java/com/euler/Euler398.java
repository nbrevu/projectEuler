package com.euler;

import com.google.common.math.IntMath;

public class Euler398 {
	private final static int N=IntMath.pow(10,7);
	private final static int M=100;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long cases=0;
		long extensions=0;
		for (int i=1;i<=N/M;++i) for (int j=i;j<=(N-i)/(M-1);++j)	{
			long maxExtension=Math.min(M-1,(N-i)/j);
			if (maxExtension*j+i==N) ++maxExtension;
			if (maxExtension==0) break;
			++cases;
			extensions+=maxExtension;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(cases+" cases; "+extensions+" extensions.");
		System.out.println("Elapsed "+seconds+" seconds."); // Almost 30 seconds JUST TO COMPUTE THE AMOUNT OF CASES.
		/*
		int count=0;
		for (int i=1;i<=N/M;++i)	{
			int maxValue=(N-i)/(M-1);
			int minValue=i;
			if (minValue>maxValue) break;
			else count+=maxValue+1-minValue;
		}
		System.out.println(count+" casos.");	// 755537759. I'm not sure if my scheme can solve this...
		// ACTUALLY THERE IS AN OVERFLOW, the real amount is 5050505055. I don't think this is doable :(.
		*/
	}
}
