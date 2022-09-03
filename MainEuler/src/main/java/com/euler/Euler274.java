package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler274 {
	// Another one at the first try... but this was just too easy.
	private final static long LIMIT=10000000l;
	
	public static void main(String[] args)	{
		long sum=0l;
		for (long prime:Primes.listLongPrimes(LIMIT)) if ((prime!=2l)&&(prime!=5l)) sum+=EulerUtils.modulusInverse(10l,prime);
		System.out.println(sum);
	}
}
