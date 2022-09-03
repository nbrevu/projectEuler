package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler712_4 {
	private final static long MOD=1_000_000_007l;
	
	private final static long LIMIT=LongMath.pow(10l,12);
	
	private static BigInteger[] countOccurrences(long limit,long prime)	{
		int maxPower=(int)(Math.log(limit)/Math.log(prime));
		BigInteger[] result=new BigInteger[1+maxPower];
		long remaining=limit;
		for (int i=0;i<maxPower;++i)	{
			long multiples=remaining/prime;
			result[i]=BigInteger.valueOf(remaining-multiples);
			remaining=multiples;
		}
		result[maxPower]=BigInteger.valueOf(remaining);
		return result;
	}
	
	private static BigInteger sumPowerDifferences(BigInteger[] powerOccurrences)	{
		BigInteger result=BigInteger.ZERO;
		for (int i=0;i<powerOccurrences.length;++i) for (int j=i+1;j<powerOccurrences.length;++j) result=result.add(powerOccurrences[i].multiply(powerOccurrences[j]).multiply(BigInteger.valueOf(j-i)));
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sqrt=LongMath.sqrt(LIMIT,RoundingMode.UNNECESSARY);
		List<Long> basePrimes=Primes.listLongPrimes(sqrt);
		MeisselLehmerPrimeCounter primeCounter=new MeisselLehmerPrimeCounter(sqrt);
		// First part: direct calculations.
		BigInteger result=BigInteger.ZERO;
		for (long p:basePrimes) result=result.add(sumPowerDifferences(countOccurrences(LIMIT,p)));
		// Second part: Meissel-Lehmer magic.
		long previousSet=primeCounter.pi(LIMIT/sqrt);
		BigInteger bigLimit=BigInteger.valueOf(LIMIT);
		for (long i=sqrt-1;i>=1;--i)	{
			long newSet=primeCounter.pi(LIMIT/i);
			BigInteger bigI=BigInteger.valueOf(i);
			BigInteger diff=BigInteger.valueOf(newSet-previousSet);
			result=result.add(bigI.multiply(bigLimit.subtract(bigI)).multiply(diff));
			previousSet=newSet;
		}
		result=result.add(result);
		result=result.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
