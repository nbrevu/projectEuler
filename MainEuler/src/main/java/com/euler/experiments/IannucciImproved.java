package com.euler.experiments;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.base.Joiner;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntDoubleMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashIntDoubleMaps;
import com.koloboke.collect.map.hash.HashLongObjMaps;

/**
 * Based on https://projecteuclid.org/DPubS?service=UI&version=1.0&verb=Display&handle=euclid.bbms/1113318127.
 * 
 * @author Pablo Moreno Olalla
 */
public class IannucciImproved {
	/*
	 * Functions to get a list of primes. I usually have them in a separate file, but I have moved them here because it made more sense
	 * to have just the necessary functions with as few dependencies as possible.
	 */
	private static boolean[] sieve(int maxNumber)	{
		// Defaults to false! True values correspond to composites.
		boolean[] composites=new boolean[1+maxNumber];
		composites[0]=composites[1]=true;
		for (int j=4;j<=maxNumber;j+=2) composites[j]=true;
		int sq=IntMath.sqrt(maxNumber,RoundingMode.DOWN);
		for (int i=3;i<=sq;i+=2) if (!composites[i]) for (int j=i*i;j<=maxNumber;j+=i+i) composites[j]=true;
		return composites;
	}
	private static long[] listLongPrimes(int maxNumber)	{
		boolean[] composites=sieve(maxNumber);
		List<Long> result=new ArrayList<>();
		for (int i=2;i<composites.length;++i) if (!composites[i]) result.add((long)i);
		return result.stream().mapToLong(Long::longValue).toArray();
	}
	
	private final static int MAX_PRIME=10000;
	
	// Interfaces used for some internal functional operations.
	private static interface LongIntDoubleFunction	{
		public double transform(long prime,int power);
	}
	private static interface LongIntFunction<T>	{
		public T get(long prime,int power);
	}
	
	/*
	 * The internal representation is an array of "lengths". The first element is the amount of skipped primes. The other ones are
	 * the amount of primes with exponents in decreasing order.
	 * 
	 * For example: [2, 4, 3, 1] means 2^0 · 3^0 · 5^3 · 7^3 · 11^3 · 13^3 · 17^2 · 19^2 · 23^2 · 29^1.
	 * 
	 * When calling toString(), the skipped primes won't be displayed. Instead, the result would be:
	 * 		5^3 · 7^3 · 11^3 · 13^3 · 17^2 · 19^2 · 23^2 · 29^1
	 */
	private static class PrimeCollection implements Comparable<PrimeCollection>	{
		// Cache used for the sigma calculations.
		private static LongObjMap<IntDoubleMap> LOG_FACTORS_TABLE=HashLongObjMaps.newMutableMap();
		// Table of primes. 
		private static long[] PRIMES=listLongPrimes(MAX_PRIME);
		/*
		 * Static method used as a first heuristic approximation (more exactly, some starting point which is guaranteed not to surpass the limit).
		 * 
		 * Read the paper for mor information of this (it's very simple, really). It returns a list of non repeated consecutive primes,
		 * after skipping as many as specified).
		 */
		public static PrimeCollection getU(int skip,double abundanceIndexLog)	{
			double qLog=0.0;
			for (int k=skip;k<PRIMES.length;++k)	{
				long p=PRIMES[k];
				double l=Math.log(p);
				qLog+=l-Math.log(p-1);
				if (qLog>abundanceIndexLog)	{
					int[] exponents=new int[] {skip,k+1-skip};
					return new PrimeCollection(exponents);
				}
			}
			throw new IllegalStateException("Not enough primes. Increase the order of magnitude or something.");
		}
		// Array with the internal representation.
		private final int[] primesByExponent;
		// Cached log, used for comparisons.
		private final double log;
		// Private constructor. Do not call from outside; use getU().
		private PrimeCollection(int[] primesByExponent)	{
			this.primesByExponent=primesByExponent;
			log=accumulate((long prime,int power)->power*Math.log(prime));
		}
		/*
		 * Some accumulation operations based on the functional interfaces above. The code is very repetitive and basically the same
		 * in all three functions... I could have avoided it using a custom stream, but I didn't bother. There are better ways to do
		 * this, but this is good enough.
		 * 
		 * What these interfaces do is "unroll" the internal representation and perform operations on them. So, if we have a list like
		 * [2, 4, 3, 1], then the transformer will be called with (5,3), (7,3), (11,3), (13,3), (17,2), (19,2), (23,2) and (29,1). The result
		 * will be accumulated in a slightly different way depending on the function called.
		 */
		private <T> T accumulate(T initial,LongIntFunction<T> transformer,BiFunction<T,T,T> accumulator)	{
			T result=initial;
			int currentPrimeIndex=primesByExponent[0];
			for (int i=1;i<primesByExponent.length;++i)	{
				int power=primesByExponent.length-i;
				for (int j=0;j<primesByExponent[i];++j)	{
					long prime=PRIMES[currentPrimeIndex];
					++currentPrimeIndex;
					result=accumulator.apply(result,transformer.get(prime,power));
				}
			}
			return result;
		}
		private <T> T accumulate(LongIntFunction<T> transformer,Function<List<T>,T> joiner)	{
			List<T> result=new ArrayList<>();
			int currentPrimeIndex=primesByExponent[0];
			for (int i=1;i<primesByExponent.length;++i)	{
				int power=primesByExponent.length-i;
				for (int j=0;j<primesByExponent[i];++j)	{
					long prime=PRIMES[currentPrimeIndex];
					++currentPrimeIndex;
					result.add(transformer.get(prime,power));
				}
			}
			return joiner.apply(result);
		}
		private double accumulate(LongIntDoubleFunction transformer)	{
			double result=0.0;
			int currentPrimeIndex=primesByExponent[0];
			for (int i=1;i<primesByExponent.length;++i)	{
				int power=primesByExponent.length-i;
				for (int j=0;j<primesByExponent[i];++j)	{
					long prime=PRIMES[currentPrimeIndex];
					++currentPrimeIndex;
					result+=transformer.transform(prime, power);
				}
			}
			return result;
		}
		// equals and hashCode required to work properly with sets.
		@Override
		public boolean equals(Object other)	{
			PrimeCollection pc=(PrimeCollection)other;
			return Arrays.equals(primesByExponent,pc.primesByExponent);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(primesByExponent);
		}
		// Comparison function. We're dealing with huge numbers so of course we use logarithms.
		@Override
		public int compareTo(PrimeCollection o) {
			return Double.compare(log,o.log);
		}
		// Display the list of factors as an approximately readable string.
		@Override
		public String toString()	{
			return accumulate((long prime,int power)->prime+((power==1)?"":("^"+power)),Joiner.on(" · ")::join);
		}
		/*
		 * I've used a search graph to look for the smaller solution. This is a bit inefficient because some nodes are generated more than
		 * once, but it's fast enough.
		 * 
		 * This function creates the direct descendants of the current node.
		 */
		public List<PrimeCollection> getChildren()	{
			List<PrimeCollection> result=new ArrayList<>();
			// First addition: the first prime goes from exponent X to X+1 (which is new).
			// So: [2,4,3,1] gets transformed into [2,1,3,3,1].
			{
				int[] newExponents=new int[1+primesByExponent.length];
				newExponents[0]=primesByExponent[0];
				newExponents[1]=1;
				newExponents[2]=primesByExponent[1]-1;
				for (int i=2;i<primesByExponent.length;++i) newExponents[i+1]=primesByExponent[i];
				result.add(new PrimeCollection(newExponents));
			}
			// Standard additions: moving one prime to the "left" (increasing exponent).
			// So: from [2,4,3,1] we get [2,5,2,1] and [2,4,4,0]. Note that [2,4,4] is not the same as [2,4,4,0].
			for (int i=2;i<primesByExponent.length;++i)	{
				int[] newExponents=Arrays.copyOf(primesByExponent,primesByExponent.length);
				++newExponents[i-1];
				--newExponents[i];
				result.add(new PrimeCollection(newExponents));
			}
			// Finally: add a new prime with exponent 1.
			// So: [2,4,3,1] gets transformed into [2,4,3,2].
			{
				int[] newExponents=Arrays.copyOf(primesByExponent,primesByExponent.length);
				++newExponents[newExponents.length-1];
				result.add(new PrimeCollection(newExponents));
			}
			return result;
		}
		/*
		 * This function calculates a factor of sigma- (see the paper). For each prime factor, an augend of sigma- is generated.
		 */
		private double getLogFactor(long prime,int power)	{
			IntDoubleMap internalMap=LOG_FACTORS_TABLE.computeIfAbsent(prime,(long unused)->HashIntDoubleMaps.newMutableMap());
			return internalMap.computeIfAbsent(power,(int pw)->{
				long bigPower=LongMath.pow(prime,pw+1);
				long sigma=(bigPower-1)/(prime-1);
				return Math.log(sigma)-pw*Math.log(prime);
			});
		}
		/*
		 * Sigma quotient sigma- (see the paper).
		 */
		public double sigmaQuotLog()	{
			return accumulate(this::getLogFactor);
		}
		/*
		 * Get a big integer representation of the number.
		 */
		public BigInteger toBigInteger()	{
			return accumulate(BigInteger.ONE,(long prime,int power)->BigInteger.valueOf(LongMath.pow(prime,power)),BigInteger::multiply);
		}
	}
	
	private static PrimeCollection getFirstAbundantWithoutNPrimes(int n,double abundanceIndex)	{
		double abundanceIndexLog=Math.log(abundanceIndex);
		NavigableSet<PrimeCollection> candidates=new TreeSet<>();
		candidates.add(PrimeCollection.getU(n,abundanceIndexLog));
		for (;;)	{
			PrimeCollection head=candidates.pollFirst();
			if (head.sigmaQuotLog()>abundanceIndexLog) return head;
			candidates.addAll(head.getChildren());
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		for (int i=0;i<16;++i)	{
			PrimeCollection result=getFirstAbundantWithoutNPrimes(i,2.0);
			System.out.println("Smallest abundant number which is not a multiple of the first "+i+" primes:");
			System.out.println("\t"+result+" = "+result.toBigInteger());
		}
		System.out.println();
		for (int i=0;i<4;++i)	{	// Mortally slow for i=4, not sure why.
			PrimeCollection result=getFirstAbundantWithoutNPrimes(i,3.0);
			System.out.println("Smallest number with abundance index >= 3.0 which is not a multiple of the first "+i+" primes:");
			System.out.println("\t"+result+" = "+result.toBigInteger());
		}
		System.out.println();
		for (int i=0;i<5;++i)	{
			double abundanceIndex=4.0+i;
			PrimeCollection result=getFirstAbundantWithoutNPrimes(0,abundanceIndex);
			System.out.println("Smallest number with abundance index >= "+abundanceIndex+":");
			System.out.println("\t"+result+" = "+result.toBigInteger());
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
