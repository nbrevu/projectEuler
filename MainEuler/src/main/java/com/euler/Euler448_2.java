package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;

public class Euler448_2 {
	private final static long N=100000l;
	private final static long MOD=999_999_017l;
	
	public static void main(String[] args)	{
		long result=N;
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(1_000_000);
		long nextToShow=100_000_000l;
		for (long i=1;i<=N;++i)	{
			if (i>=nextToShow)	{
				System.out.println(i+"...");
				nextToShow+=100_000_000l;
			}
			DivisorHolder decomp=decomposer.decompose(i);
			long totient=decomp.getTotient()%MOD;
			long factor=((i%MOD)*totient)%MOD;
			long howMany=(N/i)%MOD;
			long toAdd=(howMany*factor)%MOD;
			result+=toAdd;
			result%=MOD;
		}
		System.out.println(result/2);
	}
}
