package com.euler;

import com.euler.common.EulerUtils;

public class Euler805_3 {
	private final static int N=200;
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
	 * Ooooh, wrong answer :(. I don't think I can do much better than this. There must be some weird case, maybe.
	 * 536340067
	 * Elapsed 168.41458870000002 seconds.
	 */
	public static void main(String[] args)	{
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
				if (q>den) result+=getPeriod(q,den,MOD);
				else System.out.println(String.format("Ag, pues para num=%d, den=%d me saldría q=%d, pero no me vale.",num,den,q));
			}			
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
