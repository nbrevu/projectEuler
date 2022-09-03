package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.google.common.math.LongMath;

public class Euler263_2 {
	private final static int GOAL=4;
	
	private final static long[] TO_CHECK_FOR_PRIMES=new long[] {0,6,12,18};
	private final static long[] TO_CHECK_FOR_PRACTICAL=new long[] {1,5,9,13,17};
	private final static long PRIME_LIMIT=LongMath.pow(10l,9);
	
	private static class SexyCandidate	{
		private final long n;
		private final long[] toCheck;
		public SexyCandidate(long n,long[] toCheck)	{
			this.n=n;
			this.toCheck=toCheck;
		}
		public long getN(long base)	{
			return base+n;
		}
		public long getEngineersParadise(long base)	{
			return getN(base)+9;
		}
	}
	// Crafted by hand!
	private static List<SexyCandidate> initCandidateList()	{
		List<SexyCandidate> result=new ArrayList<>();
		result.add(new SexyCandidate(11l,new long[] {2,8}));
		result.add(new SexyCandidate(41l,new long[] {2}));
		result.add(new SexyCandidate(61l,new long[] {10}));
		result.add(new SexyCandidate(131l,new long[] {8}));
		result.add(new SexyCandidate(151l,new long[] {16}));
		result.add(new SexyCandidate(181l,new long[] {10,16}));
		return result;
	}
	
	private static class EngineersParadiseChecker	{
		private static boolean isPractical(DivisorHolder decomposition)	{
			NavigableMap<Long,Integer> sortedPrimeMap=new TreeMap<>(decomposition.getFactorMap());
			long product=1l;
			for (Map.Entry<Long,Integer> entry:sortedPrimeMap.entrySet())	{
				long p=entry.getKey();
				if (p-1>product) return false;
				product*=(LongMath.pow(p,entry.getValue()+1)-1)/(p-1);
			}
			return true;
		}
		private static long[] condense(long[] firstPrimesArray)	{
			List<Long> primes=new ArrayList<>();
			primes.add(2l);
			primes.add(3l);
			boolean add4=false;
			for (int n=5;n<firstPrimesArray.length;n+=(add4?4:2),add4=!add4) if (firstPrimesArray[n]==0) primes.add((long)n);
			return primes.stream().mapToLong(Long::longValue).toArray();
		}
		private final static RabinMiller PRIME_CHECKER=new RabinMiller();
		private final static Collection<Integer> WITNESSES=Arrays.asList(2,7,61);
		private final long[] firstPrimesArray;
		private final long[] primes;
		public EngineersParadiseChecker(long in)	{
			firstPrimesArray=Primes.firstPrimeSieve(in);
			primes=condense(firstPrimesArray);
		}
		private boolean isPrime(long in)	{
			if (in<firstPrimesArray.length) return firstPrimesArray[(int)in]==0l;
			else return PRIME_CHECKER.isPrime(BigInteger.valueOf(in),WITNESSES);
		}
		private DivisorHolder decompose(long in)	{
			if (in<firstPrimesArray.length) return DivisorHolder.getFromFirstPrimes((int)in,firstPrimesArray);
			DivisorHolder base=new DivisorHolder();
			for (long p:primes) {
				if ((p*p)>in) break;
				else if ((in%p)==0)	{
					int counter=0;
					do	{
						in/=p;
						++counter;
					}	while ((in%p)==0l);
					base.addFactor(p,counter);
					if (in<firstPrimesArray.length) return DivisorHolder.combine(base,DivisorHolder.getFromFirstPrimes((int)in,firstPrimesArray));
				}
			}
			base.addFactor(in,1);
			return base;
		}
		private boolean isPractical(long in)	{
			return isPractical(decompose(in));
		}
		public boolean isEngineersParadise(SexyCandidate candidate,long base)	{
			long n=candidate.getN(base);
			for (long p:TO_CHECK_FOR_PRIMES) if (!isPrime(n+p)) return false;
			for (long p:candidate.toCheck) if (isPrime(n+p)) return false;
			for (long p:TO_CHECK_FOR_PRACTICAL) if (!isPractical(n+p)) return false;
			return true;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<SexyCandidate> candidates=initCandidateList();
		long base=0l;
		long result=0l;
		int found=0;
		EngineersParadiseChecker checker=new EngineersParadiseChecker(PRIME_LIMIT);
		while (found<GOAL)	{
			for (SexyCandidate candidate:candidates) if (checker.isEngineersParadise(candidate,base))	{
				result+=candidate.getEngineersParadise(base);
				++found;
				if (found>=GOAL) break;
			}
			base+=210l;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
