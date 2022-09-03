package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler224 {
	// At the first try... after ALMOST EIGHT HOURS! of computation.
	private final static int N=75000000;
	private final static int A=1+IntMath.divide(N,3,RoundingMode.DOWN);
	
	private final static List<Long> PRIMES=Primes.listLongPrimes(A);
	
	private static void addPrime(Map<Long,Integer> divisors,long prime)	{
		Integer pow=divisors.get(prime);
		int next=1+((pow==null)?0:pow.intValue());
		divisors.put(prime,next);
	}
	
	private static Map<Long,Integer> getFactors(long n)	{
		if (n<2) return Collections.emptyMap();
		Map<Long,Integer> result=new HashMap<>();
		for (long p:PRIMES)	{
			if (p*p>n)	{
				addPrime(result,n);
				return result;
			}
			while ((n%p)==0)	{
				addPrime(result,p);
				n/=p;
			}
		}
		addPrime(result,n);
		return result;
	}
	
	private static SortedSet<Long> getAllDivisors(Map<Long,Integer> primes)	{
		SortedSet<Long> result=new TreeSet<>();
		result.add(1l);
		for (Map.Entry<Long,Integer> entry:primes.entrySet())	{
			long prime=entry.getKey();
			int power=entry.getValue();
			long[] factors=new long[power];
			factors[0]=prime;
			for (int i=1;i<power;++i) factors[i]=prime*factors[i-1];
			List<Long> toAdd=new ArrayList<>((power-1)*result.size());
			for (long a:result) for (long b:factors) toAdd.add(a*b);
			result.addAll(toAdd);
		}
		return result;
	}
	
	private static boolean sameParity(long a,long b)	{
		return (a%2)==(b%2);
	}
	
	public static void main(String[] args)	{
		int count=0;
		for (int a=2;a<A;++a)	{
			if ((a%100000)==0) System.out.println(""+a+"...");
			long la=(long)a;
			long a2_1=la*la+1;
			Map<Long,Integer> primeFactors=getFactors(a2_1);
			SortedSet<Long> divisors=getAllDivisors(primeFactors);
			for (long d:divisors)	{
				long ad=a2_1/d;
				if (d>ad) break;
				if (!sameParity(d,ad)) continue;
				long b=(ad-d)/2;
				if (b<a) continue;
				long c=(ad+d)/2;
				if (la+b+c<=N) ++count;
			}
		}
		System.out.println(count);
	}
}
