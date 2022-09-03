package com.euler;

import java.math.RoundingMode;
import java.util.List;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler712_3 {
	private final static long MOD=1_000_000_007l;
	
	private final static long LIMIT=LongMath.pow(10l,12);
	
	private static long[] countOccurrences(long limit,long prime,long mod)	{
		int maxPower=(int)(Math.log(limit)/Math.log(prime));
		long[] result=new long[1+maxPower];
		long remaining=limit;
		for (int i=0;i<maxPower;++i)	{
			long multiples=remaining/prime;
			result[i]=(remaining-multiples)%mod;
			remaining=multiples;
		}
		result[maxPower]=remaining;
		return result;
	}
	
	private static long sumPowerDifferences(long[] powerOccurrences,long mod)	{
		long result=0l;
		for (int i=0;i<powerOccurrences.length;++i) for (int j=i+1;j<powerOccurrences.length;++j)	{
			long differences=(powerOccurrences[i]*powerOccurrences[j])%mod;
			result+=differences*(j-i);
			result%=mod;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long sqrt=LongMath.sqrt(LIMIT,RoundingMode.UNNECESSARY);
		List<Long> basePrimes=Primes.listLongPrimes(sqrt);
		MeisselLehmerPrimeCounter primeCounter=new MeisselLehmerPrimeCounter(sqrt);
		// First part: direct calculations.
		long result=0l;
		for (long p:basePrimes)	{
			result+=sumPowerDifferences(countOccurrences(LIMIT,p,MOD),MOD);
			result%=MOD;
		}
		// Second part: Meissel-Lehmer magic.
		long previousSet=primeCounter.pi(LIMIT/sqrt);
		// This is wrong but I expect the mistake to be small, i.e., related to programming and not to math.
		for (long i=sqrt-1;i>=1;--i)	{
			long newSet=primeCounter.pi(LIMIT/i);
			//System.out.println("Hay "+newSet+" primos menores o iguales que "+(LIMIT/i)+".");
			long diff=newSet-previousSet;
			//System.out.println(String.format("Hay %d primos en el conjunto (%d,%d].",diff,LIMIT/(i+1),LIMIT/i));
			long otherFactor=(LIMIT-i)%MOD;
			long powerDiffs=(i*otherFactor)%MOD;
			result+=(powerDiffs*diff)%MOD;
			result%=MOD;
			previousSet=newSet;
		}
		result*=2;
		result%=MOD;
		System.out.println(result);
	}
}
