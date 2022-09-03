package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.LongIntCursor;

public class Euler779 {
	private final static int LIMIT=1_000_000_000;
	
	public static void main(String[] args)	{
		double sum=0;
		int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		for (int n=2;n<=LIMIT;++n)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(n,firstPrimes);
			long p=Long.MAX_VALUE;
			int a=1;
			for (LongIntCursor cursor=divs.getFactorMap().cursor();cursor.moveNext();) if (cursor.key()<p)	{
				p=cursor.key();
				a=cursor.value();
			}
			if (a==1) continue;
			double dp=(double)p;
			sum+=(a-1)/(dp-1);
			if ((n>990_000_000)||((n%1_000_000)==0))	{
				double q=sum/n;
				System.out.println("n="+n+": s="+q+".");
			}
		}
	}
}
