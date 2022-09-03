package com.euler;

import java.util.List;

import com.euler.common.Primes;

public class Euler278 {
	private final static int LIMIT=5000;
	
	private static long getLastImpossibleCombination(long p1,long p2,long p3)	{
		long prod12=p1*p2;
		long prod13=p1*p3;
		long prod23=p2*p3;
		long prod123=prod12*p3;
		return 2*prod123-(prod12+prod13+prod23);
	}
	
	public static void main(String[] args)	{
		List<Integer> primes=Primes.listIntPrimes(LIMIT);
		int howMany=primes.size();
		long sum=0;
		for (int i=0;i<howMany-2;++i)	{
			int p1=primes.get(i);
			for (int j=i+1;j<howMany-1;++j)	{
				int p2=primes.get(j);
				for (int k=j+1;k<howMany;++k)	{
					int p3=primes.get(k);
					long last=getLastImpossibleCombination(p1,p2,p3);
					sum+=last;
				}
			}
		}
		System.out.println(sum);
	}
}
