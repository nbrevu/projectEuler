package com.euler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler555 {
	private final static long P=1000000l;
	private final static long M=1000000l;
	
	private static class DivisorCalculator	{
		private final long[] firstPrimes;
		public DivisorCalculator(long max)	{
			firstPrimes=Primes.firstPrimeSieve(max);
		}
		public NavigableSet<Long> getDivisors(long in)	{
			Map<Long,Integer> primeFactors=getPrimeFactors(in);
			NavigableSet<Long> result=new TreeSet<>();
			result.add(1l);
			for (Map.Entry<Long,Integer> entry:primeFactors.entrySet())	{
				Set<Long> toAdd=new HashSet<>();
				long base=entry.getKey();
				int exponent=entry.getValue();
				long factor=base;
				for (int i=1;i<=exponent;++i)	{
					for (long current:result) toAdd.add(current*factor);
					factor*=base;
				}
				result.addAll(toAdd);
			}
			return result;
		}
		private Map<Long,Integer> getPrimeFactors(long in)	{
			Map<Long,Integer> result=new HashMap<>();
			while (in>1)	{
				long prime=firstPrimes[(int)in];
				if (prime==0) prime=in;
				EulerUtils.increaseCounter(result,prime);
				in/=prime;
			}
			return result;
		}
	}
	
	private static long get(long p,long m)	{
		DivisorCalculator calculator=new DivisorCalculator(p-1);
		long result=0l;
		for (long s=1;s<p;++s)	{
			long a=m+1-s;
			NavigableSet<Long> divisors=calculator.getDivisors(s);
			divisors=divisors.headSet(p-s,true);
			for (long r:divisors)	{
				long b=m+r-s;
				result+=((b+a)*(b-a+1))/2l;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=get(P,M);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
