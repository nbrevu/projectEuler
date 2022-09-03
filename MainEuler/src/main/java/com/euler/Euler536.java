package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler536 {
	private final static long LIMIT=LongMath.pow(10,6);
	
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		for (long i=1;i<=LIMIT;++i)	{
			DivisorHolder divisors=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			long totient=divisors.getTotient();
			if (((i+3)%totient)==0) System.out.println(i);
		}
		long sum=0;
		System.out.println("And now for something completely different...");
		for (long i=1;i<=LIMIT;++i)	{
			boolean passes=true;
			for (int j=1;j<i;++j)	{
				long mod=EulerUtils.expMod(j,i+4,i);
				if (mod!=j)	{
					passes=false;
					break;
				}
			}
			if (passes)	{
				DivisorHolder factors=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
				System.out.println(i+" = "+factors+".");
				sum+=i;
			}
		}
		System.out.println(sum);
	}
}
