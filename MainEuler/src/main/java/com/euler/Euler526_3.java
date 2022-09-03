package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.google.common.math.LongMath;

public class Euler526_3 {
	private final static long LIMIT=10000000000000000l;

	private final static long SPACING=30;
	private final static long OFFSET=11;
	
	private final static List<Integer> WITNESSES=Arrays.asList(2,3,5,7,11,13,17);
	private final static int SEARCHES_LIMIT=1000000000;
	
	private static class PrimeManager	{
		private final static RabinMiller tester=new RabinMiller(); 
		private final boolean[] composites;
		public PrimeManager(long limit)	{
			composites=Primes.sieve((int)LongMath.sqrt(limit, RoundingMode.DOWN));
		}
		
		public boolean isPrime(long in)	{
			return tester.isPrime(BigInteger.valueOf(in),WITNESSES);
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
		private long sum=-1;
		
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
			if (sum==-1)	{
				// Assumes isBasePrimalitySatisfied() returns true.
				sum=N*4+16;	// N + (N+2) + (N+6) + (N+8).
				for (long l:nonPrimeIndices) sum+=primeManager.getMaxFactor(N+l);
			}
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
			baseNumber=in-(in%SPACING);
			baseNumber+=OFFSET;
			if (baseNumber>in) baseNumber-=SPACING;
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
				baseNumber-=SPACING;
			}	while (!result.isBasePrimalitySatisfied());
			return result;
		}
	}
	
	public static void main(String[] args)	{
		PrimeManager manager=new PrimeManager(LIMIT);
		CandidateSet.setPrimeManager(manager);
		CandidateFinder finder=new CandidateFinder(LIMIT);
		long maximum=0;
		for (int i=0;i<SEARCHES_LIMIT;++i)	{
			if ((i%1000000)==0) System.out.println("\t"+i+"...");
			CandidateSet set=finder.next();
			long sum=set.getSum();
			if (sum>maximum)	{
				System.out.println(set.toString());
				maximum=sum;
			}
		}
	}
}
