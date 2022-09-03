package com.euler;

public class Euler692_2 {
	// 23416728348467685 ist der 80ste Fibonacci Nummer!!!!!
	private final static long LIMIT=23416728348467685l;

	private static long solve(long in)	{
		// With such a simple pattern, it would have been a pity not to get it right at the first try :).
		// Apparently this gives some information about problem 366? See the comment thread...
		long sumPrev=6;
		long fiboPrev=3;
		long sumCurr=12;
		long fiboCurr=5;
		for (;;)	{
			long fiboNext=fiboCurr+fiboPrev;
			if (fiboNext>in) throw new IllegalArgumentException("The input must be a Fibonacci number.");
			long sumNext=sumCurr+sumPrev+fiboNext-fiboPrev;
			if (fiboNext==in) return sumNext;
			sumPrev=sumCurr;
			fiboPrev=fiboCurr;
			sumCurr=sumNext;
			fiboCurr=fiboNext;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=solve(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
