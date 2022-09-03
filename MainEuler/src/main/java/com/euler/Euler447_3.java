package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler447_3 {
	private final static long N=LongMath.pow(10l,7);
	private final static long MOD=1_000_000_007l;
	
	// Es funktioniert überhaupt nicht :(.
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve(N);
		int[] möbius=EulerUtils.möbiusSieve((int)N);
		long result=0;
		for (long i=2;i<=N;++i)	{
			long si=LongMath.sqrt(i,RoundingMode.DOWN);
			for (long j=1;j<=si;++j)	{
				long m=möbius[(int)j];
				if ((m!=0)&&((i%(j*j))==0))	{
					long factor=j*m;
					long inner=i%(j*j);
					long divSum=DivisorHolder.getFromFirstPrimes(inner,firstPrimes).getSumOfDivisors();
					result+=factor*divSum;
				}
			}
		}
		System.out.println(result);
		System.out.println(result%MOD);
	}
}
