package com.euler;

import com.euler.common.Primes;

public class Euler526_2 {
	private static long LIMIT=1000000000l;
	
	private static class PrimeManager	{
		private final boolean[] composites;
		public PrimeManager(long limit)	{
			composites=Primes.sieve((int)limit);
		}
		
		public long getMaxFactor(long in)	{
			if (!composites[(int)in]) return in;
			if (in<=2) return in;
			while ((in%2)==0) in/=2;
			if (in==1) return 2;
			for (long i=3;;i+=2) if (!composites[(int)i])	{
				if ((i*i)>in) return in;
				while ((in%i)==0) in/=i;
				if (in==1) return i;
			}
		}
	}
	
	public static void main(String[] args)	{
		PrimeManager manager=new PrimeManager(LIMIT+8);
		long current=0;
		for (long i=LIMIT;i<LIMIT+9;++i) current+=manager.getMaxFactor(i);
		long maximum=current;
		for (long i=LIMIT-1;i>0;--i)	{
			current-=manager.getMaxFactor(i+9);
			current+=manager.getMaxFactor(i);
			if (current>maximum)	{
				System.out.println("DAS MÁXIMO: "+current+" EN "+i+".");
				maximum=current;
			}
			// Known maximum...
			if (maximum==4896292593l) return;
		}
	}
	
	/*
	 *  Maximum found at 995600231.
	 *  995600231-101=995600130
	 *  995600130 IS a multiple of 210.
	 *  995600235/105=9481907. This is not prime.
	 *  
	 */
}
