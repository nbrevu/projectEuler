package com.euler;

import java.math.BigInteger;

import com.euler.common.Primes;

public class Euler574 {
	private final static int LIMIT=3800;
	
	public static void main(String[] args)	{
		BigInteger result=BigInteger.ONE;
		boolean[] composites=Primes.sieve(LIMIT);
		for (int i=2;i<LIMIT;++i) if (!composites[i]) result=result.multiply(BigInteger.valueOf(i));
		String bigString=result.toString();
		System.out.println(bigString);
		System.out.println("Este bicho tiene "+bigString.length()+" cifras.");
	}
}
