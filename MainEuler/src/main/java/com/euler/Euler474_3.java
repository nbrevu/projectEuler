package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler474_3 {
	private static int timesInFactorial(int prime,int factorial)	{
		int result=0;
		while (factorial>=prime)	{
			int div=factorial/prime;
			result+=div;
			factorial=div;
		}
		return result;
	}
	
	private static Map<Integer,Integer> getFactorList(int in,int[] firstPrimes)	{
		Map<Integer,Integer> result=new HashMap<>();
		for (;;)	{
			int prime=firstPrimes[in];
			if (prime==0)	{
				EulerUtils.increaseCounter(result,in);
				return result;
			}	else	{
				EulerUtils.increaseCounter(result,prime);
				in/=prime;
			}
		}
	}

	public static void main(String[] args)	{
		int N=1000000;
		int[] firstPrimes=Primes.firstPrimeSieve(1+N);
		Map<Integer,Integer> allPrimeFactors=new HashMap<>();
		for (int i=2;i<=N;++i) EulerUtils.increaseCounter(allPrimeFactors,getFactorList(i,firstPrimes));
		for (int i=2;i<=N;++i) if (firstPrimes[i]==0)	{
			assert allPrimeFactors.containsKey(i);
			assert allPrimeFactors.get(i).intValue()==timesInFactorial(i,N);
		}
	}
}
