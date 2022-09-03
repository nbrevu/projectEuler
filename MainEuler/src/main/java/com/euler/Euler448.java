package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler448 {
	private final static long N=100;
	
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(N);
		long result=N;
		for (long i=1;i<=N;++i)	{
			DivisorHolder decomp=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			long factor=i*decomp.getTotient();
			result+=factor*(N/i);
		}
		System.out.println(result/2);
	}
}
