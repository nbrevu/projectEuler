package com.euler;

import java.util.Collection;
import java.util.List;

import com.euler.common.Primes;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class Euler313 {
	private final static long LIMIT=1000000;
	private final static List<Long> PRIMES=Primes.listLongPrimes(LIMIT);
	
	private static Collection<Long> getSquaredPrimes()	{
		return Collections2.transform(PRIMES,new Function<Long,Long>()	{
			@Override
			public Long apply(Long in)	{
				return in*in;
			}
		});
	}
	
	public static void main(String[] args)	{
		long sum=0;
		for (long p2:getSquaredPrimes())	{
			long n=(p2+13)/2;
			long k=n/4+1;
			long q=n-3*k;
			sum+=2*((q+1)/3);
			if (((sum+3)%8)==0) ++sum;
		}
		System.out.println(sum);
	}
}
