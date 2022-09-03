package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.LongIntCursor;

public class Euler715_3 {
	private static int getMultiplicativeFunction(DivisorHolder holder)	{
		int result=1;
		for (LongIntCursor cursor=holder.getFactorMap().cursor();cursor.moveNext();)	{
			long prime=cursor.key();
			int power=cursor.value();
			if ((power>=2)||(prime==2)) return 0;
			else if ((prime%4)==1) result=-result;
		}
		return result;
	}
	
	private static int[] getMultiplicativeFunctionSums(int n)	{
		int[] lastPrimes=Primes.lastPrimeSieve(n);
		int[] result=new int[1+n];
		result[0]=0;
		for (int i=1;i<=n;++i)	{
			DivisorHolder divisors=DivisorHolder.getFromFirstPrimes(i,lastPrimes);
			result[i]=result[i-1]+getMultiplicativeFunction(divisors);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		int N=IntMath.pow(10,6);
		long mod=1_000_000_007l;
		long result=0l;
		int[] multiplicativeSumPrefix=getMultiplicativeFunctionSums(N);
		for (int n=1;n<=N;++n)	{
			int toSum=(N/n);
			long cube=(((long)n)*n*n)%mod;
			result+=(cube*multiplicativeSumPrefix[toSum])%mod;
		}
		System.out.println(result%mod);
		for (int i=1;i<multiplicativeSumPrefix.length;++i) System.out.println(String.format("Sm(%d)=%d",i,multiplicativeSumPrefix[i]));
	}
}
