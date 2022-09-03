package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.koloboke.collect.LongCursor;

public class Euler659_4 {
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(100000000);
		for (long k=1;k<=1000;++k)	{
			long kk=k*k;
			long p=1;
			long l=4*kk+1;
			for (long n=1;n<=l;++n)	{
				long g=EulerUtils.gcd(n*n+kk,2*n+1);
				if (g!=1)	{
					DivisorHolder divisors=decomposer.decompose(g);
					long pp=1l;
					for (LongCursor cursor=divisors.getFactorMap().keySet().cursor();cursor.moveNext();) pp=Math.max(pp,cursor.elem());
					p=Math.max(p,pp);
				}
			}
			System.out.println("P("+k+")="+p+".");
			if ((l%p)!=0) System.out.println("\tCaso raruno encontrado. Scheiße!!!!!");
		}
	}
}
