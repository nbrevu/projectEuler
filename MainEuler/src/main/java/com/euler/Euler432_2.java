package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler432_2 {
	// private final static IntSet BASE_FACTORS=HashIntSets.newImmutableSetOf(2,3,5,7,11,13,17);
	// private final static long LIMIT=LongMath.pow(10l,6);
	
	// Prueba: suma de phi(6n) para n=1..10.
	public static void main(String[] args)	{
		int[] firstPrimeSieve=Primes.firstPrimeSieve(61);
		int realTotientSum=0;
		for (int i=1;i<=10;++i)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(6*i,firstPrimeSieve);
			realTotientSum+=divs.getTotient();
		}
		System.out.println("Suma real: "+realTotientSum+".");
		// Totient(6)=2. Totient(3)=2. Totient(2)=1. Totient(1)=1.
		System.out.println("\tSumaDeTotient(10): "+EulerUtils.getSumOfTotientsUpTo(10).toString());
		System.out.println("\tSumaDeTotient(5): "+EulerUtils.getSumOfTotientsUpTo(5).toString());
		System.out.println("\tSumaDeTotient(3): "+EulerUtils.getSumOfTotientsUpTo(3).toString());
		System.out.println("\tSumaDeTotient(1): "+EulerUtils.getSumOfTotientsUpTo(1).toString());
		System.out.println("\t¿Se ve alguna combinación lineal guapante?");
	}
}