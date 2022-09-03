package com.euler;

import java.util.Iterator;

import com.euler.common.Primes;

public class Euler526 {
	private static long LIMIT=1000000000l;
	
	private static class PrimeManager	{
		private final boolean[] composites;
		public PrimeManager(long limit)	{
			composites=Primes.sieve((int)limit);
		}
		
		public boolean isPrime(long in)	{
			return !composites[(int)in];
		}
		
		public long getMaxFactor(long in)	{
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
	
	private static class CandidateSet	{
		private final static long nonPrimeIndices[]={1,3,4,5,7};
		private static PrimeManager primeManager=null;
		private final long N;	// The candidate set goes up to N+8.
		
		public CandidateSet(long initialNumber)	{
			N=initialNumber;
		}
		
		public static void setPrimeManager(PrimeManager pm)	{
			primeManager=pm;
		}
		
		public boolean isBasePrimalitySatisfied()	{
			return primeManager.isPrime(N)&&primeManager.isPrime(N+2)&&primeManager.isPrime(N+6)&&primeManager.isPrime(N+8);
		}
		
		public long getSum()	{
			// Assumes isBasePrimalitySatisfied() returns true.
			long sum=N*4+2+6+8;	// N + (N+2) + (N+6) + (N+8).
			for (long l:nonPrimeIndices) sum+=primeManager.getMaxFactor(N+l);
			return sum;
		}
		
		@Override
		public String toString()	{
			return "["+N+"]: "+getSum();
		}
	}
	
	private static class CandidateFinder implements Iterator<CandidateSet>	{
		private long baseNumber;
		
		public CandidateFinder(long in)	{
			baseNumber=in-(in%210);
			baseNumber+=101;
			if (baseNumber>in) baseNumber-=210;
		}

		@Override
		public boolean hasNext() {
			return baseNumber>=0;
		}

		@Override
		public CandidateSet next() {
			CandidateSet result;
			do	{
				result=new CandidateSet(baseNumber);
				baseNumber-=210;
			}	while (!result.isBasePrimalitySatisfied());
			return result;
		}
	}
	
	public static void main(String[] args)	{
		PrimeManager manager=new PrimeManager(LIMIT);
		CandidateSet.setPrimeManager(manager);
		CandidateFinder finder=new CandidateFinder(LIMIT);
		for (int i=0;i<100;++i) System.out.println(finder.next().toString());
	}
}
