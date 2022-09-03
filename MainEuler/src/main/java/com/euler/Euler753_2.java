package com.euler;

import com.euler.common.Primes;

public class Euler753_2 {
	/*
	 * This program verifies what I had already proven: there is a solution if and only if -3 DOESN'T have a square root modulo p.
	 * Which, coincidentally, happens to match with primes where p%3!=1. Yes, that includes 3: 1^3+1^3 == 2^3 (mod 3) and 2^3+2^3 == 1^3 (mod 3).
	 * 
	 * When there are solutions, the set of modular cubes is exactly the set of modular values, and... the amount of sums is just (n^2-n) for n=p-1.
	 */
	private final static int LIMIT=6_000_000;

	public static void main(String[] args)	{
		long result=0l;
		for (long p:Primes.listLongPrimesAsArray(LIMIT)) if ((p%3)!=1) result+=(p-1)*(p-2);
		// Ooooh, wrong solution :(. "2357487210859559252" is not it.
		System.out.println(result);
	}
}
