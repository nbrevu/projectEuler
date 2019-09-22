package com.euler.common;

import java.util.HashSet;
import java.util.Set;

import com.euler.common.EulerUtils.Pair;

public class MoebiusCalculator	{
	private final int[] firstPrimeSieve;
	public MoebiusCalculator(int limit)	{
		firstPrimeSieve=Primes.firstPrimeSieve(limit);
	}
	public MoebiusCalculator(int[] firstPrimeSieve)	{
		this.firstPrimeSieve=firstPrimeSieve;
	}
	public int getMoebiusFunction(int n)	{
		Set<Integer> primes=new HashSet<>();
		while (n>1)	{
			int p=firstPrimeSieve[n];
			if (p==0)	{
				p=n;
				n=1;
			}	else n/=p;
			if (primes.contains(p)) return 0;
			primes.add(p);
		}
		boolean isOdd=(primes.size()%2)==1;
		return isOdd?-1:1;
	}
	// This is used for large numbers. It's much, much, much slower.
	public static int getMoebiusFunction(long n)	{
		return getMoebiusFunction(n,new Primes.PrimeCandidatesIterator());
	}
	
	public static int getMoebiusFunction(long n,Iterable<Long> iterable)	{
		Set<Long> factors=new HashSet<>();
		for (long candidate:iterable)	{
			if (n==1) break;
			else if ((candidate*candidate)>n)	{
				factors.add(n);
				break;
			}
			Pair<Boolean,Long> result=tryFactor(n,factors,candidate);
			if (result.first==false) return 0;
			n=result.second;
		}
		boolean isOdd=(factors.size()%2)==1;
		return isOdd?-1:1;
	}
	private static Pair<Boolean,Long> tryFactor(long in,Set<Long> factors,long factor)	{
		long result=in;
		if ((result%factor)==0)	{
			factors.add(factor);
			result/=factor;
			if ((result%factor)==0) return new Pair<Boolean,Long>(false,0l);
		}
		return new Pair<Boolean,Long>(true,result);
	}
}