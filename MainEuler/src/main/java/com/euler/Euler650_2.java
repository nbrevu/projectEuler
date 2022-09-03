package com.euler;

import java.util.Map;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler650_2 {
	// There are better ways to do this (there is a recurrence for the terms!), but this was good enough (~1h 20m).
	private final static int N=20000;
	private final static long MOD=1000000007l;
	
	private static class Factorer	{
		private final int[] firstPrimes;
		private final DivisorHolder[] cache;
		public Factorer(int limit)	{
			firstPrimes=Primes.firstPrimeSieve(limit);
			cache=new DivisorHolder[1+limit];
		}
		public DivisorHolder factor(int in)	{
			DivisorHolder result=cache[in];
			if (result==null)	{
				result=DivisorHolder.getFromFirstPrimes(in,firstPrimes);
				cache[in]=result;
			}
			return result;
		}
	}
	
	private static DivisorHolder getFactorDecomposition(int n,Factorer factorer)	{
		DivisorHolder result=new DivisorHolder();
		for (int i=2;i<=n;++i)	{
			DivisorHolder factors=factorer.factor(i).pow(2*i-n-1);
			result=DivisorHolder.combine(result,factors);
		}
		result.clean();
		return result;
	}
	
	private static long getSumOfPowers(long prime,int power,long mod)	{
		long numerator=EulerUtils.expMod(prime,power+1,mod)-1;
		// We calculate the value we want using the Chinese remainder theorem.
		if (prime==2) return numerator;
		long inverse=EulerUtils.modulusInverse(prime-1,mod);
		return (numerator*inverse)%mod;
	}
	
	private static long getSumOfDivisors(DivisorHolder factors,long mod)	{
		long result=1l;
		for (Map.Entry<Long,Integer> factor:factors.getFactorMap().entrySet())	{
			long pow=getSumOfPowers(factor.getKey(),factor.getValue(),mod);
			result=(result*pow)%mod;
		}
		return result;
	}
	
	private static long getResult(int n,long mod)	{
		long result=4;	// 1:1; 2:1+2=3.
		Factorer factorer=new Factorer(n);
		for (int i=3;i<=n;++i)	{
			if ((i%100)==0) System.out.println(i+"...");
			DivisorHolder bigNumber=getFactorDecomposition(i,factorer);
			result=(result+getSumOfDivisors(bigNumber,mod))%mod;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=getResult(N,MOD);
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
