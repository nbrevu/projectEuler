package com.euler;

import java.util.Locale;

import com.euler.common.EulerUtils;

public class Euler777_2 {
	private final static long LIMIT=10000;
	
	private static double d(long a,long b)	{
		if (((a*b)%10)==0) return (2*a*b-3*(a+b)+4)*0.25;
		else return 2*a*b-3*(a+b)*0.5;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		double sum=0;
		for (long a=2;a<=LIMIT;++a) for (long b=2;b<=LIMIT;++b) if (EulerUtils.areCoprime(a,b)) sum+=d(a,b);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.10e",sum));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
