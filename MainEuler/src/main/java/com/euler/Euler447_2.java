package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler447_2 {
	private final static long N=LongMath.pow(10l,7);
	private final static long MOD=1_000_000_007l;
	
	// Es funktioniert nicht...
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(N);
		int[] möbius=EulerUtils.möbiusSieve((int)N);
		long result=0;
		for (long i=2;i<=N;++i)	{
			DivisorHolder divs=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			for (LongCursor cursor=divs.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				long möbiusSum=0;
				for (LongCursor cursor2=DivisorHolder.getFromFirstPrimes(d,firstPrimes).getUnsortedListOfDivisors().cursor();cursor2.moveNext();)	{
					long j=cursor.elem();
					if ((i%(j*d)==0)) möbiusSum+=möbius[(int)j];
				}
				result+=d*möbiusSum;
			}
			// result-=i;
		}
		System.out.println(result);
		System.out.println(result%MOD);
	}
}
