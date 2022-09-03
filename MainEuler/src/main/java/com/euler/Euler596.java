package com.euler;

import java.util.HashMap;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler596 {
	private final static long LIMIT=1000;
	
	private static long getSigma(long n,long[] firstPrimes)	{
		if (n<=1) return n;
		Map<Long,Integer> divCounter=new HashMap<>();
		while (n>1)	{
			long p=firstPrimes[(int)n];
			if (p==0)	{
				EulerUtils.increaseCounter(divCounter,n);
				break;
			}
			EulerUtils.increaseCounter(divCounter,p);
			n/=p;
		}
		long sigma=1;
		for (Map.Entry<Long,Integer> entry:divCounter.entrySet())	{
			long factor=entry.getKey();
			int power=entry.getValue();
			sigma*=(LongMath.pow(factor,power+1)-1)/(factor-1);
		}
		return sigma;
	}
	
	private static long usingSigmaRefined(long limit)	{
		long l2=limit*limit;
		long[] firstPrimes=Primes.firstPrimeSieve(l2);
		long count=1;	// (0,0,0,0).
		long l2_4=l2/4;
		for (long i=1;i<=l2_4;++i) count-=24*getSigma(i,firstPrimes);
		for (long i=1+l2_4;i<=l2;++i) count+=8*getSigma(i,firstPrimes);
		return count;
	}
	
	private static long sumOfSigma(long limit)	{
		long result=limit*limit;
		for (long k=1;k<=limit;++k) result-=limit%k;
		return result;
	}
	
	private static long refinedTwice(long limit)	{
		// This method uses the fact that sum(sigma(k),k,1,n)=n^2-sum(n mod k,k,1,n)
		long l2=limit*limit;
		return 1+8*sumOfSigma(l2)-32*sumOfSigma(l2/4);
	}
	
	public static void main(String[] args)	{
		for (long l=1;l<=200;++l)	{
			long r1=refinedTwice(l);
			long r2=usingSigmaRefined(l);
			if (r1!=r2) throw new RuntimeException("For N="+l+": expecting "+r2+", found "+r1+".");
		}
		System.out.println("Todo correcto.");
		System.out.println(refinedTwice(LIMIT));
	}
}
