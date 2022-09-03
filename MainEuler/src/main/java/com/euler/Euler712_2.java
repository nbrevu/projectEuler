package com.euler;

import com.euler.common.Primes;

public class Euler712_2 {
	private final static long MOD=1_000_000_007l;
	
	private final static long LIMIT=10000;
	
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
		long result=0l;
		for (long p:Primes.listLongPrimes(LIMIT))	{
			result+=sumPowerDifferences(countOccurrences(LIMIT,p,MOD),MOD);
			result%=MOD;
		}
		result*=2;
		result%=MOD;
		System.out.println(result);
		System.out.println((long)Math.floor(Math.pow(125d,1d/3d)));
	}
}
