package com.euler;

import java.util.List;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.DivisorHolder;
import com.euler.common.DivisorHolder.TwoFactorDecomposition;
import com.euler.common.Primes;
import com.google.common.base.Optional;

public class Euler598_2 {
	private final static int LIMIT=100;
	
	private static int primesInFactorial(int prime,int factorial)	{
		int result=0;
		for (int div=prime;div<=factorial;div*=prime) result+=factorial/div;
		return result;
	}
	
	private static DivisorHolder getFactorialPrimeComposition(int in)	{
		DivisorHolder result=new DivisorHolder();
		List<Integer> primes=Primes.listIntPrimes(in);
		for (int p:primes) result.addFactor(p,primesInFactorial(p,in));
		return result;
	}
	
	public static long getAcceptableDecompositions(int in)	{
		DivisorHolder divs=getFactorialPrimeComposition(in);
		int p2=divs.removeSinglePrimes();
		BigCombinatorialNumberCache cache=new BigCombinatorialNumberCache(p2);
		System.out.println("\tDespu�s del truqui, "+in+"! tiene "+divs.getAmountOfDivisors()+" divisores.");
		long result=0;
		for (TwoFactorDecomposition d:divs.getDecomposer())	{
			Optional<Integer> on2=d.neededPowersOf2();
			if (on2.isPresent())	{
				int n2=on2.get();
				if (n2<=p2)	{
					int diff=p2-n2;
					if ((diff%2)==0) result+=cache.get(p2,diff/2).longValue();
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		/*
		for (int i=2;i<=LIMIT;++i) System.out.println("f("+i+")="+getAcceptableDecompositions(i));
		/*/
		long tic=System.nanoTime();
		long result=getAcceptableDecompositions(LIMIT);
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println(result);
		System.out.println("Result found in "+seconds+" seconds.");
		//*/
	}
}
