package com.euler;

import com.euler.common.Primes;

public class Euler543_2 {
	private final static int MAX_FIB=44;
	
	// Yes, this is inefficient. It does the work though.
	private static class PrimeCounter	{
		private final boolean[] composites;
		public PrimeCounter(int max)	{
			composites=Primes.sieve(max);
		}
		public int countPrimes(int upTo)	{
			if (upTo>=composites.length) throw new IllegalArgumentException();
			else if (upTo<2) return 0;
			else if (upTo==2) return 1;
			else if (upTo==3) return 2;
			int result=2;
			boolean add4=false;
			for (int i=5;i<=upTo;i+=(add4?4:2),add4=!add4) if (!composites[i]) ++result;
			return result;
		}
	}
	
	private static int[] generateFibonacci(int maxIndex)	{
		int[] result=new int[1+maxIndex];
		result[0]=0;
		result[1]=1;
		for (int i=2;i<=maxIndex;++i) result[i]=result[i-1]+result[i-2];
		return result;
	}
	
	private static long getS(int N,PrimeCounter counter)	{
		long result=counter.countPrimes(N);	// k=1.
		if (N>=4) result+=counter.countPrimes(N-2)-1;	// k=2 (odd: 2+<primes>).
		result+=(N/2)-1;	// k=2 (even: every one except 2).
		// k=3: every one starting from 6. k=4: every one starting from 8. Etc. Use a summation formula for k>=3.
		if (N>=6)	{
			long m=N/2;
			result+=(N-m)*m-2*N+4;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] fibonaccis=generateFibonacci(MAX_FIB);
		PrimeCounter counter=new PrimeCounter(fibonaccis[MAX_FIB]);
		long result=0;
		for (int i=3;i<=MAX_FIB;++i) result+=getS(fibonaccis[i],counter);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
