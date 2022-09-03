package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler715 {
	public static void main(String[] args)	{
		int maxN=100;
		int[] firstPrimes=Primes.firstPrimeSieve(maxN);
		long sumG=0;
		for (int n=1;n<=maxN;++n)	{
			int n2=n*n;	// I could use n, but oh well.
			long counters=0;
			for (int x1=0;x1<n;++x1)	{
				int sum1=x1*x1;
				for (int x2=0;x2<n;++x2)	{
					int sum2=sum1+x2*x2;
					for (int x3=0;x3<n;++x3)	{
						int sum3=sum2+x3*x3;
						for (int x4=0;x4<n;++x4)	{
							int sum4=sum3+x4*x4;
							for (int x5=0;x5<n;++x5)	{
								int sum5=sum4+x5*x5;
								for (int x6=0;x6<n;++x6)	{
									int sum6=sum5+x6*x6;
									if (EulerUtils.gcd(sum6,n2)==1) ++counters;
								}
							}
						}
					}
				}
			}
			int denom=n*n*(int)DivisorHolder.getFromFirstPrimes(n,firstPrimes).getTotient();
			if (counters%denom!=0) throw new RuntimeException("¿Pero por qué D:?");
			long g=counters/denom;
			long diff=g-(n*n*n);
			String diffStr=(diff==0)?"":((diff>0)?("+"+diff):Long.toString(diff));
			System.out.println(String.format("g(%d)=%d=%d^3%s.",n,g,n,diffStr));
			sumG+=g;
		}
		System.out.println("Total: G("+maxN+")="+sumG+".");
	}
}
