package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler445 {
	private final static int LIMIT=10000;
	
	private static class CombinatorialDivisorCalculator	{
		private final int max;
		private final long[] firstPrimes;
		private final DivisorHolder[] factorials;
		public CombinatorialDivisorCalculator(int max)	{
			this.max=max;
			firstPrimes=Primes.firstPrimeSieve((long)max);
			factorials=new DivisorHolder[1+max];
			factorials[0]=new DivisorHolder();
			factorials[1]=new DivisorHolder();
			for (int i=2;i<=max;++i)	{
				DivisorHolder thisNumber=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
				factorials[i]=DivisorHolder.combine(thisNumber,factorials[i-1]);
			}
		}
		public DivisorHolder getCombinatorial(int in)	{
			return DivisorHolder.divide(factorials[max],DivisorHolder.combine(factorials[in],factorials[max-in]));
		}
	}
	
	public static void main(String[] args)	{
		CombinatorialDivisorCalculator calculator=new CombinatorialDivisorCalculator(LIMIT);
		long maxDivs=0;
		long maxPrimeCounter=0;
		for (int i=1;i<LIMIT;++i)	{
			DivisorHolder bigCombinatorial=calculator.getCombinatorial(i);
			long divisors=bigCombinatorial.getAmountOfDivisors();
			long primeCounter=bigCombinatorial.getFactorMap().size();
			System.out.println("C("+LIMIT+","+i+") tiene "+divisors+" divisores repartidos entre "+primeCounter+" primos.");
			maxDivs=Math.max(maxDivs,divisors);
			maxPrimeCounter=Math.max(maxPrimeCounter,primeCounter);
		}
		System.out.println("El máximo es "+maxDivs+". JAJA SÍ.");
		System.out.println("Máximo de conteo de primos: "+maxPrimeCounter+".");
	}
}
