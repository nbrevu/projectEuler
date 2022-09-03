package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.Primes;

public class Euler351 {
	// This was also far easier than I had expected... and yes, got it at the first try.
	private final static int LIMIT=100000000;
	private final static int[] FIRST_PRIMES=Primes.firstPrimeSieve(LIMIT);
	
	private static void addDivisor(Map<Integer,Integer> divs,int prime)	{
		Integer prev=divs.get(prime);
		int next=1+((prev==null)?0:prev.intValue());
		divs.put(prime,next);
	}
	
	private static Map<Integer,Integer> getDivisors(int num)	{
		Map<Integer,Integer> result=new HashMap<>();
		for (;;)	{
			int prime=FIRST_PRIMES[num];
			if (prime==0)	{
				addDivisor(result,num);
				return result;
			}
			addDivisor(result,prime);
			num/=prime;
		}
	}
	
	private static int getTotient(int num,Map<Integer,Integer> factor)	{
		int result=num;
		for (int prime:factor.keySet())	{
			result/=prime;
			result*=prime-1;
		}
		return result;
	}
	
	private static int[] getTotients(int limit)	{
		int[] result=new int[1+limit];
		for (int i=2;i<=limit;++i)	{
			Map<Integer,Integer> divisors=getDivisors(i);
			result[i]=getTotient(i,divisors);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		int[] totients=getTotients(LIMIT);
		long sum=0l;
		for (int i=2;i<=LIMIT;++i) sum+=i-totients[i];
		System.out.println(6*sum);
	}
}
