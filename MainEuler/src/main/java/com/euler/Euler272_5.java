package com.euler;

import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler272_5 {
	/*
	 * Facts:
	 *  A) The amount of cubic roots is a multiplicative function.
	 *  B) Every primitive (i.e. either prime or multiple of a prime) has either 1 or 3 cubic roots.
	 *  	B1) for primes of the form p=3k+1, p^n has exactly 3 cubic roots for every n>=1.
	 *  	B2) for primes of the form p=3k+2, p^n has exactly 1 cubic root for every n>=1.
	 *  	B3) Special case p=3: 3^1=3 has exactly one cubic root. 3^n has exactly 3 cubic roots for n>=2.
	 *  C) Since the goal is numbers with 243 cubic roots, we need to count numbers with 5 coprime factors with 3 cubic roots, and any amount
	 *  of coprime factors with 1 root.
	 *  
	 * General schematics:
	 *  A) We will generate three separate calculation schemes.
	 *  	A1) Doesn't contain 3 (requires 5 additional 3k+1 primes).
	 *  	A2) contains 3^1 (requires 5 additional 3k+1 primes).
	 *  	A3) contains 3^n for some n>=2 (requires 4 additional 3k+1 primes).
	 *  B) The smallest "pure-243" number is 7*9*13*19*31=482391. Therefore we need:
	 *  	B1) p=3k+2 primes up to 10^11/(7*9*13*19*31)=207300.
	 *  	B2) p=3k+1 primes up to 10^11/(7*9*13*19)=6426322.
	 *  	B3) Of course we need to generate primes up to max{B1,B2}=6426322 and then separate them into distinct bins.
	 *  
	 * Algorithm:
	 *  1) Generate all the primes up to the required number.
	 *  2) Separate the primes into {3k+1 up to 6426322} and {3k+2 up to 207300}, discarding the rest. This can be done super fast using a sieve.
	 *  	2A) Maybe it makes sense to use NavigableSets for these bins. They are slower (boxing, UGH) but the navigation functions are useful.
	 *  	2B) Alternatively we could use sorted arrays and Arrays.binarySearch.
	 *  3) For each one of the cases: iterate over the required amount of primes, then over the powers of each.
	 *  4) Given the product of a power of primes, P, we count the amount of {3k+2} products below 10^11/P.
	 *  5) A recursive search, with factors, is probably the best option. No need to calculate every factor! Sweet :).
	 */
	private final static long LIMIT=LongMath.pow(10l,11);
	
	private final static long SMALL_BASE=7*9*13*19;
	private final static long BIG_BASE=SMALL_BASE*31;
	private final static int LIMIT_3K1=(int)(LIMIT/SMALL_BASE);
	private final static int LIMIT_3K2=(int)(LIMIT/BIG_BASE);
	
	private static class SolutionAdder	{
		private final long[] primes3k1;
		private final long[] products3k2;	// Doesn't contain primes, but PRODUCTS of numbers of the form 3k+2, including also 1.
		private SolutionAdder(long[] primes3k1,long[] products3k2)	{
			this.primes3k1=primes3k1;
			this.products3k2=products3k2;
		}
		public static SolutionAdder getFor(int limit3k1,int limit3k2)	{
			boolean[] composites=Primes.sieve(Math.max(limit3k1,limit3k2));
			long[] primes3k1=extractPrimes3k1(composites,limit3k1);
			long[] products3k2=extractProducts3k2(composites,limit3k2);
			return new SolutionAdder(primes3k1,products3k2);
		}
		private static long[] extractPrimes3k1(boolean[] composites,int limit)	{
			int count=0;
			for (int i=7;i<=limit;i+=6) if (!composites[i]) ++count;
			long[] result=new long[count];
			int index=0;
			for (int i=7;i<=limit;i+=6) if (!composites[i])	{
				result[index]=i;
				++index;
			}
			return result;
		}
		private static long[] extractProducts3k2(boolean[] composites,int limit)	{
			NavigableSet<Long> result=new TreeSet<>();
			result.add(1l);
			addPrime(result,2,limit);
			for (int i=5;i<=limit;i+=6) if (!composites[i]) addPrime(result,i,limit);
			return result.stream().mapToLong(Long::longValue).toArray();
		}
		private static void addPrime(NavigableSet<Long> numbers,long prime,long limit)	{
			LongSet toAdd=HashLongSets.newMutableSet();
			for (long n:numbers)	{
				long current=n*prime;
				while (current<=limit)	{
					toAdd.add(current);
					current*=prime;
				}
			}
			numbers.addAll(toAdd);
		}
		private long sum3k2Below(long limit)	{
			long result=0;
			for (int i=0;i<products3k2.length;++i)	{
				long toAdd=products3k2[i];
				if (toAdd>limit) break;
				result+=toAdd;
			}
			return result;
		}
		public long sumTotal(long limit)	{
			return sumNo3(limit)+sumOne3(limit)+sumMany3(limit);
		}
		private long sumNo3(long limit)	{
			return sumRecursive(limit,5,0);
		}
		private long sumOne3(long limit)	{
			return 3*sumRecursive(limit/3,5,0);
		}
		private long sumMany3(long limit)	{
			long currentFactor=3*3;
			long currentLimit=limit/currentFactor;
			long result=0l;
			for (;;)	{
				long currentResult=sumRecursive(currentLimit,4,0);
				if (currentResult==0) return result;
				result+=currentFactor*currentResult;
				currentLimit/=3;
				currentFactor*=3;
			}
		}
		private long sumRecursive(long limit,int remainingPrimes,int currentIndex)	{
			if (remainingPrimes<=0) return sum3k2Below(limit);
			long result=0l;
			for (int i=currentIndex;i<primes3k1.length;++i)	{
				long prime=primes3k1[i];
				long currentFactor=prime;
				long currentLimit=limit/prime;
				long subResult=0;
				for (;;)	{
					long currentResult=sumRecursive(currentLimit,remainingPrimes-1,i+1);
					if (currentResult==0) break;
					subResult+=currentFactor*currentResult;
					currentLimit/=prime;
					currentFactor*=prime;
				}
				if (subResult==0) break;
				result+=subResult;
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		// I can't believe that I got this without bugs! YEAH.
		long tic=System.nanoTime();
		SolutionAdder solutionAdder=SolutionAdder.getFor(LIMIT_3K1,LIMIT_3K2);
		long result=solutionAdder.sumTotal(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
