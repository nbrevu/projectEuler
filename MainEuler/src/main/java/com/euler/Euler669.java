package com.euler;

public class Euler669 {
	private final static long F=99194853094755497l;
	
	public static void main(String[] args)	{
		int index=2;
		long fPrev=1;
		long fCurr=1;
		do	{
			long fN=fCurr+fPrev;
			fPrev=fCurr;
			fCurr=fN;
			++index;
		}	while (fCurr<F);
		// "99194853094755497 es el 83º número de Fibonacci."
		if (fCurr==F) System.out.println(F+" es el "+index+"º número de Fibonacci.");
		else System.out.println(F+" no es un número de Fibonacci. Está entre el "+(index-1)+"º, "+fPrev+", y el "+index+"º, "+fCurr+".");
	}
}
