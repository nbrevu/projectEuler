package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler797 {
	private final static long N=10_000_000;
	
	/*
	 * 8648640 = 1001 * 864 * 10
	 * 1001=7*11*13
	 * 864=27*32
	 * 10=2*5
	 * 8648640=64*27*5*7*11*13 -> 7*4*2*2*2*2=448, ES GEFÄLLT MIR, ODER.
	 */
	public static void main(String[] args)	{
		long[] primes=Primes.firstPrimeSieve(N);
		long max=0;
		for (long i=2;i<=N;++i)	{
			DivisorHolder h=DivisorHolder.getFromFirstPrimes(i,primes);
			long d=h.getAmountOfDivisors();
			max=Math.max(max,d);
			if (max==d) System.out.println("AHÍ VA LA HOSTIA, ODER? "+i+" tiene "+d+" divisores.");
		}
		System.out.println(max);
	}
}
