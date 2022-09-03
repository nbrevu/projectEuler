package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.IntMath;

public class Euler223 {
	private final static int N=25000000;
	private final static int A=1+IntMath.divide(N,3,RoundingMode.DOWN);
	
	private final static int[] FIRST_PRIMES=Primes.firstPrimeSieve(A);
	
	private static void addPrime(Map<Long,Integer> divisors,long prime)	{
		Integer pow=divisors.get(prime);
		int next=1+((pow==null)?0:pow.intValue());
		divisors.put(prime,next);
	}
	
	private static void addFactors(Map<Long,Integer> divisors,int n)	{
		if (n<2) return;
		for (;;)	{
			int prime=FIRST_PRIMES[n];
			if (prime==0)	{
				addPrime(divisors,n);
				return;
			}
			addPrime(divisors,prime);
			n/=prime;
		}
	}
	
	private static Map<Long,Integer> getJointFactors(int a,int b)	{
		Map<Long,Integer> result=new HashMap<>();
		addFactors(result,a);
		addFactors(result,b);
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
			Map<Long,Integer> primeFactors=getJointFactors(a-1,a+1);
			long a2_1=la*la-1;
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
		count+=(N/2)-1;
		System.out.println(count);
	}
}
