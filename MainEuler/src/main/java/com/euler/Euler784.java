package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;

public class Euler784 {
	private final static int LIMIT=100;
	
	public static void main(String[] args)	{
		int[] lastPrimes=Primes.lastPrimeSieve(LIMIT*LIMIT);
		long sum=0;
		for (int p=3;p<=LIMIT;++p) for (int r=1+IntMath.divide(p,2,RoundingMode.DOWN);r<p;++r) if (EulerUtils.areCoprime(p,r))	{
			int x=p*r-1;
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(x,lastPrimes);
			for (LongCursor cursor=divs.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				long q=cursor.elem();
				if (q<p) continue;
				else if (((q*r)%p)==1)	{
					System.out.println(String.format("p=%d, r=%d: q=%d.",p,r,q));
					sum+=p+q;
				}
			}
		}
		System.out.println(sum);
	}
}
