package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.euler.common.Primes.RabinMiller;
import com.google.common.collect.ImmutableMap;

public class Euler526_5 {
	// OK, so this gives the result ([9997194587108081]: 49601160286750947), but it lasts about 12 hours.
	// Apparently the key is just to sieve much more aggressively. No ultra-clever solutions in the forums.
	private final static long LIMIT=10000000000000000l;

	private final static List<Integer> WITNESSES=Arrays.asList(2,3,5,7,11,13,17);
	
	private static class PrimeManager	{
		private final static RabinMiller tester=new RabinMiller(); 
		public PrimeManager()	{}
		
		public boolean isPrime(long in)	{
			return tester.isPrime(BigInteger.valueOf(in),WITNESSES);
		}
	}
	
	private abstract static class CandidateSet	{
		protected static PrimeManager primeManager=null;
		protected final long N;	// The candidate set goes up to N+8.
		private long sum=-1;
		
		public CandidateSet(long initialNumber)	{
			N=initialNumber;
		}
		
		public static void setPrimeManager(PrimeManager pm)	{
			primeManager=pm;
		}
		
		public boolean isBasePrimalitySatisfied()	{
			return primeManager.isPrime(N)&&primeManager.isPrime(N+2)&&primeManager.isPrime(N+6)&&primeManager.isPrime(N+8)&&additionalPrimeChecks(N);
		}
		
		public abstract boolean additionalPrimeChecks(long in);
		
		// Assumes isBasePrimalitySatisfied() returns true.
		public long getSum()	{
			if (sum==-1) sum=calculateSum(N);
			return sum;
		}
		
		public abstract long calculateSum(long in);
		
		@Override
		public String toString()	{
			return "["+N+"]: "+getSum();
		}
	}
	
	private static class CandidateTypeA extends CandidateSet	{
		private final static Map<Long,Long> MAP_OF_MODS=ImmutableMap.of(1l,24l,3l,2l,4l,315l,5l,4l,7l,6l);
		/*
		 *  N: prime
		 *  N+1: 2*2*2*3*prime -> also not a multiple of 8.
		 *  N+2: prime
		 *  N+3: 2*prime
		 *  N+4: 3*3*5*7*prime
		 *  N+5: 2*2*prime
		 *  N+6: prime
		 *  N+7: 2*3*prime
		 *  N+8: prime
		 */
		public CandidateTypeA(long initialNumber) {
			super(initialNumber);
		}

		@Override
		public boolean additionalPrimeChecks(long in) {
			// if ((in%48)==47) return false;
			long in2=(in+4)/315;
			if (((in2%3)==0)&&((in2%5)==0)&&((in2%7)==0)) return false;
			for (Map.Entry<Long,Long> mod:MAP_OF_MODS.entrySet()) if (!primeManager.isPrime((in+mod.getKey())/mod.getValue())) return false;
			return true;
		}

		@Override
		public long calculateSum(long in) {
			return N+((N+1)/24)+(N+2)+((N+3)/2)+((N+4)/315)+((N+5)/4)+(N+6)+((N+7)/6)+(N+8);
		}
	}
	
	private static class CandidateTypeB extends CandidateSet	{
		private final static Map<Long,Long> MAP_OF_MODS=ImmutableMap.of(1l,6l,3l,4l,4l,315l,5l,2l,7l,24l);
		/*
		 *  N: prime
		 *  N+1: 2*3*prime
		 *  N+2: prime
		 *  N+3: 2*2*prime
		 *  N+4: 3*3*5*7*prime
		 *  N+5: 2*prime
		 *  N+6: prime
		 *  N+7: 2*2*2*3*prime -> also not a multiple of 8.
		 *  N+8: prime
		 */
		public CandidateTypeB(long initialNumber) {
			super(initialNumber);
		}

		@Override
		public boolean additionalPrimeChecks(long in) {
			// if ((in%48)==41) return false;
			long in2=(in+4)/315;
			if (((in2%3)==0)&&((in2%5)==0)&&((in2%7)==0)) return false;
			for (Map.Entry<Long,Long> mod:MAP_OF_MODS.entrySet()) if (!primeManager.isPrime((in+mod.getKey())/mod.getValue())) return false;
			return true;
		}

		@Override
		public long calculateSum(long in) {
			return N+((N+1)/6)+(N+2)+((N+3)/4)+((N+4)/315)+((N+5)/2)+(N+6)+((N+7)/24)+(N+8);
		}
	}
	
	private static class CandidateFinder implements Iterator<CandidateSet>	{
		/* 
		 * The first case is N==311 (mod 5040).
		 * The second case is N==4721 (mod 5040).
		 */
		private long numberA;
		private long numberB;
		private boolean nextIsA;
		
		private int internalCounter=0;
		
		public CandidateFinder(long in)	{
			long baseNumber=in-(in%5040);
			numberA=baseNumber+311;
			numberB=baseNumber+4721;
			if (numberA>in) numberA-=5040;
			if (numberB>in) numberB-=5040;
			nextIsA=(numberA>numberB);
		}

		@Override
		public boolean hasNext() {
			return (numberA>0)||(numberB>0);
		}
		
		@Override
		public CandidateSet next() {
			CandidateSet result;
			do result=nextUntestedCandidate(); while (!result.isBasePrimalitySatisfied());
			return result;
		}
		
		private CandidateSet nextUntestedCandidate()	{
			CandidateSet result;
			if (nextIsA)	{
				result=new CandidateTypeA(numberA);
				numberA-=5040;
			}	else	{
				result=new CandidateTypeB(numberB);
				numberB-=5040;
			}
			nextIsA=!nextIsA;
			++internalCounter;
			if (((internalCounter)%1000000)==0) System.out.println(""+internalCounter+": "+numberA+", "+numberB+".");
			return result;
		}
	}

	public static void main(String[] args)	{
		PrimeManager manager=new PrimeManager();
		CandidateSet.setPrimeManager(manager);
		CandidateFinder finder=new CandidateFinder(LIMIT);
		CandidateSet result=finder.next();	// Because of the way we're doing this, it's the result.
		System.out.println(result.toString());
	}
}
