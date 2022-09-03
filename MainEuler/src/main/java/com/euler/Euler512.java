package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler512 {
	private final static int LIMIT=5*IntMath.pow(10,8);
	
	private static Map<Long,Integer> factor(int number,long[] firstPrimeSieve)	{
		Map<Long,Integer> result=new HashMap<>();
		while (number>1)	{
			long prime=firstPrimeSieve[number];
			if (prime==0)	{
				EulerUtils.increaseCounter(result,(long)number);
				return result;
			}	else	{
				EulerUtils.increaseCounter(result,prime);
				number/=prime;
			}
		}
		return result;
	}
	
	public static long sumOfTotientsClever(int number,long[] firstPrimeSieve)	{
		if (number<=1) return 1;
		else if ((number%2)==0) return 0;
		else if (firstPrimeSieve[number]==0) return number-1;
		Map<Long,Integer> factors=factor(number,firstPrimeSieve);
		return EulerUtils.getTotient(factors);
	}
	
	public static void main(String[] args)	{
		long sum=0;
		long[] firstPrimeSieve=Primes.firstPrimeSieve((long)LIMIT);
		for (int i=1;i<=LIMIT;++i)	{
			long sot=sumOfTotientsClever(i,firstPrimeSieve);
			sum+=sot;
		}
		System.out.println(sum);
	}
}
