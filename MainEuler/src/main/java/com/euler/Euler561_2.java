package com.euler;

import com.google.common.math.LongMath;

public class Euler561_2	{
	private final static long PRIMES=904961l;
	private final static long N=LongMath.pow(10l,12);
	
	public static void main(String[] args)	{
		long result=0l;
		long q=2;
		long n=N/2;
		while (q<n)	{
			result+=n/q;
			q*=2;
		}
		result*=1+PRIMES;
		System.out.println(result);
	}
}
