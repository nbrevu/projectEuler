package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler447 {
	private final static long N=LongMath.pow(10l,7);
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] firstPrimes=Primes.firstPrimeSieve(N);
		long result=0;
		for (long i=2;i<=N;++i)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			for (LongCursor cursor=divs.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				if ((d!=i)&&EulerUtils.areCoprime(d,i/d)) result+=d;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println(result%MOD);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
