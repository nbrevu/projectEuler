package com.euler;

import java.math.BigInteger;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler608 {
	private final static int LIMIT=200;
	
	public static void main(String[] args)	{
		int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		DivisorHolder divisors=new DivisorHolder();
		for (int i=2;i<=LIMIT;++i) divisors=DivisorHolder.combine(divisors,DivisorHolder.getFromFirstPrimes(i,firstPrimes));
		System.out.println(divisors.toString());
		BigInteger totalAmountOfDivisors=BigInteger.ONE;
		for (int power:divisors.getFactorMap().values()) totalAmountOfDivisors=totalAmountOfDivisors.multiply(BigInteger.valueOf(1+power));
		System.out.println(totalAmountOfDivisors.toString());
		System.out.println(divisors.getFactorMap().size());
	}
}
