package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.google.common.math.LongMath;

public class Euler526_4 {
	private final static long LIMIT=10000000000000000l;

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
			return primeManager.isPrime((N+4)/315)&&primeManager.isPrime(N)&&primeManager.isPrime(N+2)&&primeManager.isPrime(N+6)&&primeManager.isPrime(N+8);
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
		/*
		 * We want the numbers to follow one of these structures:
		 *  N: prime
		 *  N+1: 2*2*2*3*prime
		 *  N+2: prime
		 *  N+3: 2*prime
		 *  N+4: 3*3*5*7*prime
		 *  N+5: 2*2*prime
		 *  N+6: prime
		 *  N+7: 2*3*prime
		 *  N+8: prime
		 *  	OR
		 *  N: prime
		 *  N+1: 2*3*prime
		 *  N+2: prime
		 *  N+3: 2*2*prime
		 *  N+4: 3*3*5*7*prime
		 *  N+5: 2*prime
		 *  N+6: prime
		 *  N+7: 2*2*2*3*prime
		 *  N+8: prime
		 *  
		 * The first case is N==311 (mod 2520).
		 * The second case is N==2201 (mod 2520).
		 */
		private long numberA;
		private long numberB;
		private boolean nextIsA;
		
		public CandidateFinder(long in)	{
			long baseNumber=in-(in%2520);
			numberA=baseNumber+311;
			numberB=baseNumber+2201;
			if (numberA>in) numberA-=2520;
			if (numberB>in) numberB-=2520;
			nextIsA=(numberA>numberB);
		}

		@Override
		public boolean hasNext() {
			return (numberA>0)||(numberB>0);
		}
		
		@Override
		public CandidateSet next() {
			CandidateSet result;
			do result=new CandidateSet(nextNumber()); while (!result.isBasePrimalitySatisfied());
			return result;
		}
		
		private long nextNumber()	{
			long result;
			if (nextIsA)	{
				result=numberA;
				numberA-=2520;
			}	else	{
				result=numberB;
				numberB-=2520;
			}
			nextIsA=!nextIsA;
			return result;
		}
	}

	public static void main(String[] args)	{
		PrimeManager manager=new PrimeManager(LIMIT);
		CandidateSet.setPrimeManager(manager);
		CandidateFinder finder=new CandidateFinder(LIMIT);
		long maximum=0;
		for (int i=0;i<SEARCHES_LIMIT;++i)	{
			if ((i%10000)==0) System.out.println("\t"+i+"...");
			CandidateSet set=finder.next();
			long sum=set.getSum();
			if (sum>maximum)	{
				System.out.println(set.toString());
				maximum=sum;
			}
		}
	}
}
