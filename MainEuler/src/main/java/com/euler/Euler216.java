package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.euler.common.Primes;

public class Euler216 {
	private final static int LIMIT=50000000;
	private final static List<Integer> RABIN_MILLER_WITNESSES=Arrays.asList(2,3,5,7,11,13,17,19,23);
	private static boolean isValid(long n,Primes.RabinMiller rabinMiller)	{
		BigInteger bigN=BigInteger.valueOf(n);
		BigInteger number=bigN.multiply(bigN);
		number=number.add(number).subtract(BigInteger.ONE);
		return rabinMiller.isPrime(number, RABIN_MILLER_WITNESSES);
	}
	public static void main(String[] args)	{
		int counter=2;
		Primes.RabinMiller rabinMiller=new Primes.RabinMiller();
		for (long i=4;i<=LIMIT;++i)	{
			if (isValid(i,rabinMiller)) ++counter;
		}
		System.out.println(counter);
	}
}
