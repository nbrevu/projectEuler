package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler635 {
	private final static long LIMIT=100_000_000;
	private final static long MOD=1_000_000_009l;
	
	// This result is correct... it's just not what the problem is asking :D. Stupid me.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		// 2, 3, 5 are calculated outside the main loop, so we remove them from the stream.
		long[] primes=Primes.listLongPrimes(LIMIT).stream().mapToLong(Long::longValue).dropWhile((long x)->x<=5).toArray();
		int lastPrimeForInverses=(int)primes[primes.length-1];
		// We are going to need inverses up to 2*lastPrime!
		long[] inverses=EulerUtils.calculateModularInverses2(1+lastPrimeForInverses*2,MOD);
		// A2(2)=2; A2(3)=10; A3(2)=5; A3(3)=31.
		long result=2+10+5+31;
		/*
		 * We start with the calculations for p=5, outside the main loop.
		 * Term for A2: 1/2*(2n n). For n=5, 1/2*(10 5)=1/2*(10*9*8*7*6)/120=126.
		 * Term for A3: 1/3*(3n n). For n=5, 1/3*(15 5)=1001.
		 */
		long term2=126;
		long term3=1001;
		int lastPrime=5;
		result+=term2+term3;
		for (long p:primes)	{
			long seq1Inv=1;
			long seq2Dir=1;
			long seq2Inv=1;
			long seq3Dir=1;
			for (int x=1+lastPrime;x<=p;++x)	{
				seq1Inv*=inverses[x];
				seq1Inv%=MOD;
			}
			for (int x=1+2*lastPrime;x<=2*p;++x)	{
				seq2Dir*=x;
				seq2Dir%=MOD;
				seq2Inv*=inverses[x];
				seq2Inv%=MOD;
			}
			for (int x=1+3*lastPrime;x<=3*p;++x)	{
				seq3Dir*=x;
				seq3Dir%=MOD;
			}
			term2*=seq2Dir;
			term2%=MOD;
			term2*=seq1Inv;
			term2%=MOD;
			term2*=seq1Inv;
			term2%=MOD;
			term3*=seq3Dir;
			term3%=MOD;
			term3*=seq1Inv;
			term3%=MOD;
			term3*=seq2Inv;
			term3%=MOD;
			result+=term2+term3;
			result%=MOD;
			lastPrime=(int)p;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
