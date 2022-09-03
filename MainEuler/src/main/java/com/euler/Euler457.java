package com.euler;

import com.euler.common.Primes;

public class Euler457 {
	private final static long LIMIT=650;
	
	private static long f(long n)	{
		return n*(n-3)-1;
	}
	
	private static long getRP(long p)	{
		long pp=p*p;
		for (long n=1;n<pp;++n)	{
			long fn=f(n);
			if ((fn%pp)==0) return n;
		}
		return 0l;
	}
	
	public static void main(String[] args)	{
		for (long prime:Primes.listLongPrimes(LIMIT))	{
			long r=getRP(prime);
			if (r!=0) System.out.println(""+prime+", "+r+".");
		}
	}
}
