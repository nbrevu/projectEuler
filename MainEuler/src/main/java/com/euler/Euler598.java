package com.euler;

import java.util.Arrays;
import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.DivisorHolder.TwoFactorDecomposition;
import com.euler.common.Primes;

public class Euler598 {
	private final static int LIMIT=50;
	
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
	
	// This is a brute force approach. It doesn't work because the amount of divisors is about 3.9e16.
	// Originally I thought there were way fewer, because of an overflow.
	public static int getAcceptableDecompositions(int in)	{
		DivisorHolder divs=getFactorialPrimeComposition(in);
		System.out.println("\t"+in+"! tiene "+divs.getAmountOfDivisors()+" divisores.");
		int result=0;
		for (TwoFactorDecomposition d:divs.getDecomposer()) if (d.sameAmountOfFactors())	{
			System.out.println(Arrays.toString(d.factor1)+" <==> "+Arrays.toString(d.factor2));
			++result;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		getAcceptableDecompositions(14);
		// for (int i=2;i<=LIMIT;++i) System.out.println("f("+i+")="+getAcceptableDecompositions(i));
		/*
		long tic=System.nanoTime();
		DivisorHolder divs=getFactorialPrimeComposition(LIMIT);
		int result=0;
		for (Decomposition d:divs.getDecomposer()) if (d.sameAmountOfFactors()) ++result;
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println(result);
		System.out.println("Result found in "+seconds+" seconds.");
		*/
	}
}
