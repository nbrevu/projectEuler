package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler769 {
	private final static long N=1000;
	
	private final static long N2=N*N;
	
	/*
	 * Heeeeeeeeey, OEIS gives me information :D!
	 * 
	 * https://oeis.org/search?q=1%2C6%2C13%2C22%2C33%2C37&sort=&language=english&go=Search
	 * 
	 * "Positive numbers that are primitively represented by the indefinite quadratic form x^2 - 3y^2 of discriminant 12".
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int counter=0;
		for (long x=1;;++x)	{
			long x2=x*x;
			if (x2>=N2) break;
			for (long y=1;;++y) if (EulerUtils.areCoprime(x,y))	{
				long f=x2+(5*x+3*y)*y;
				if (f>N2) break;
				long z=LongMath.sqrt(f,RoundingMode.DOWN);
				if (z*z==f)	{
					++counter;
					System.out.println("f("+x+","+y+")="+z+"^2.");
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(counter);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
