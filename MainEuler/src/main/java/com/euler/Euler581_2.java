package com.euler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler581_2 {
	// Greatest value: 1109496723125 (barely above 10^12).
	// private final static long LIMIT=1000000000000000l;
	private final static long LIMIT=1200000000000l;
	private final static List<Long> PRIMES=Arrays.asList(2l,3l,5l,7l,11l,13l,17l,19l,23l,29l,31l,37l,41l,43l,47l);
	
	private static class PrimeCombination	{
		private final List<Long> primes;
		private long limit;
		private static Set<Long> ONE=Collections.singleton(1l);
		public PrimeCombination(List<Long> primes,long limit)	{
			this.primes=primes;
			this.limit=limit;
		}
		private static Set<Long> addPrime(Set<Long> previous,long newPrime,long limit)	{
			System.out.println(newPrime+" ["+previous.size()+"]...");
			Set<Long> result=new HashSet<>();
			for (long l:previous)	{
				do	{
					result.add(l);
					l*=newPrime;
				}	while (l<=limit);
			}
			return result;
		}
		public Set<Long> getProductCombinations()	{
			Set<Long> current=ONE;
			for (long p:primes) current=addPrime(current,p,limit);
			return current;
		}
	}
	
	public static void main(String[] args)	{
		System.out.println("CO MEN ZAMOS");
		PrimeCombination pc=new PrimeCombination(PRIMES,LIMIT);
		Set<Long> allFactors=pc.getProductCombinations();
		long sum=0l;
		System.out.println("Me he encontrado "+allFactors.size()+" candidatos.");
		long minimum=Long.MAX_VALUE;
		long maximum=Long.MIN_VALUE;
		int counter=0;
		for (long p:allFactors) if (allFactors.contains(p+1))	{
			++counter;
			minimum=Long.min(minimum,p);
			maximum=Long.max(maximum,p);
			sum+=p;
		}
		System.out.println("Hay "+counter+" índices válidos.");
		System.out.println("El menor es "+minimum+" y el mayor es "+maximum);
		System.out.println(sum);
	}
}
