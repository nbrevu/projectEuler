package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler805_7 {
	private final static int N=3;
	private final static long MOD=1_000_000_007l;
	
	private static long getPeriod(long q,long n,long mod)	{
		long result=0;
		long firstMod=n;
		do	{
			long n10=n*10;
			long digit=n10/q;
			n=n10%q;
			result=(result*10+digit)%mod;
		}	while (n!=firstMod);
		return result;
	}

	/*
	 * 647354610
	 * Elapsed 162.824058 seconds.
	 * Still wrong :(.
	 */
	public static void main(String[] args)	{
		long primeLimit=N*N*N*10l;
		long[] firstPrimes=Primes.firstPrimeSieve(primeLimit);
		long tic=System.nanoTime();
		long result=0;
		for (long u=1;u<=N;++u)	{
			long num=u*u*u;
			for (long v=1;v<=N;++v) if (EulerUtils.areCoprime(u,v))	{
				long den=v*v*v;
				long diff=10*den-num;
				if (diff<=0) continue;
				long q=diff;
				while ((q%2)==0) q/=2;
				while ((q%5)==0) q/=5;
				if ((q<=den)||(q<=num)) continue;
				LongSet allDivisors=DivisorHolder.getFromFirstPrimes(q,firstPrimes).getUnsortedListOfDivisors();
				long chosenQ=q;
				for (LongCursor c=allDivisors.cursor();c.moveNext();)	{
					long n=c.elem();
					if ((n<=chosenQ)&&(n>den)&&(n>num)) chosenQ=n;
				}
				result+=getPeriod(chosenQ,den,MOD);
			}			
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
