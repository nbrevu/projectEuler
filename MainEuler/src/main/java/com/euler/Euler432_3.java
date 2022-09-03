package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.euler.common.SumOfTotientCalculator;

public class Euler432_3 {
	// Prueba: suma de phi(3n) para n=1..100.
	// VALE. Next step: probar con varios números primos (5, 7, 19...). 
	private final static int N=1000;
	private final static int PRIME=23;
	
	public static void main(String[] args)	{
		int[] firstPrimeSieve=Primes.firstPrimeSieve(PRIME*N+1);
		int realTotientSum=0;
		for (int i=1;i<=N;++i)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(PRIME*i,firstPrimeSieve);
			realTotientSum+=divs.getTotient();
		}
		System.out.println("Suma real: "+realTotientSum+".");
		SumOfTotientCalculator calc=SumOfTotientCalculator.getWithoutMod();
		int calculatedTotientSum=0;
		for (int n=N;n>0;n/=PRIME) calculatedTotientSum+=(PRIME-1)*calc.getTotientSum(n);
		System.out.println("Cálculo teórico: "+calculatedTotientSum+".");
	}
}