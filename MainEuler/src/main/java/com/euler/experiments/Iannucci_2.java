package com.euler.experiments;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import com.euler.common.Primes;
import com.google.common.base.Joiner;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.LongMath;

public class Iannucci_2 {
	private final static long MAX_PRIME=10000;
	private final static double LOG2=Math.log(3);
	
	private static interface TriFunction<T>	{
		public T accumulate(long prime,int power,T previous);
	}
	
	private static interface TriFunctionDouble	{
		public double accumulate(long prime,int power,double previous);
	}
	
	private static class PrimeCollection implements Comparable<PrimeCollection>	{
		private static Table<Long,Integer,Double> LOG_FACTORS_TABLE=HashBasedTable.create();
		private static long[] PRIMES=Primes.listLongPrimes(MAX_PRIME).stream().mapToLong(Long::longValue).toArray();
		/*
		 * Internal representation: an array of "lengths". The first element is the amount of skipped primes. The other ones are
		 * the amount of primes with exponents in decreasing order.
		 * 
		 * For example: [2, 4, 3, 1] means 2^0 · 3^0 · 5^3 · 7^3 · 11^3 · 13^3 · 17^2 · 19^2 · 23^2 · 29^1.
		 */
		private final int[] primesByExponent;
		private PrimeCollection(int[] primesByExponent)	{
			this.primesByExponent=primesByExponent;
		}
		private <T> T accumulate(T initial,TriFunction<T> accumulator)	{
			T result=initial;
			int currentPrimeIndex=primesByExponent[0];
			for (int i=1;i<primesByExponent.length;++i)	{
				int power=primesByExponent.length-i;
				for (int j=0;j<primesByExponent[i];++j)	{
					long prime=PRIMES[currentPrimeIndex];
					++currentPrimeIndex;
					result=accumulator.accumulate(prime, power, result);
				}
			}
			return result;
		}
		private double accumulate(double initial,TriFunctionDouble accumulator)	{
			double result=initial;
			int currentPrimeIndex=primesByExponent[0];
			for (int i=1;i<primesByExponent.length;++i)	{
				int power=primesByExponent.length-i;
				for (int j=0;j<primesByExponent[i];++j)	{
					long prime=PRIMES[currentPrimeIndex];
					++currentPrimeIndex;
					result=accumulator.accumulate(prime, power, result);
				}
			}
			return result;
		}
		public static PrimeCollection getU(int skip)	{
			double qLog=0.0;
			for (int k=skip;k<PRIMES.length;++k)	{
				long p=PRIMES[k];
				double l=Math.log(p);
				qLog+=l-Math.log(p-1);
				if (qLog>LOG2)	{
					int[] exponents=new int[] {skip,k+1-skip};
					return new PrimeCollection(exponents);
				}
			}
			throw new NoSuchElementException("T'has pasao, macho t'has pasao.");
		}
		@Override
		public boolean equals(Object other)	{
			PrimeCollection pc=(PrimeCollection)other;
			return Arrays.equals(primesByExponent,pc.primesByExponent);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(primesByExponent);
		}
		public List<PrimeCollection> getChildren()	{
			List<PrimeCollection> result=new ArrayList<>();
			// First addition: the first prime goes from exponent X to X+1 (which is new).
			{
				int[] newExponents=new int[1+primesByExponent.length];
				newExponents[0]=primesByExponent[0];
				newExponents[1]=1;
				newExponents[2]=primesByExponent[1]-1;
				for (int i=2;i<primesByExponent.length;++i) newExponents[i+1]=primesByExponent[i];
				result.add(new PrimeCollection(newExponents));
			}
			// Standard additions: moving one prime to the "left" (increasing exponent).
			for (int i=2;i<primesByExponent.length;++i)	{
				int[] newExponents=Arrays.copyOf(primesByExponent,primesByExponent.length);
				++newExponents[i-1];
				--newExponents[i];
				result.add(new PrimeCollection(newExponents));
			}
			// Finally: add a new prime with exponent 1.
			{
				int[] newExponents=Arrays.copyOf(primesByExponent,primesByExponent.length);
				++newExponents[newExponents.length-1];
				result.add(new PrimeCollection(newExponents));
			}
			return result;
		}
		private double getLogFactor(Long prime,Integer power)	{
			Double knownValue=LOG_FACTORS_TABLE.get(prime,power);
			if (knownValue==null)	{
				long bigPower=LongMath.pow(prime,power+1);
				long sigma=(bigPower-1)/(prime-1);
				double result=Math.log(sigma)-power*Math.log(prime);
				LOG_FACTORS_TABLE.put(prime,power,result);
				return result;
			}
			return knownValue;
		}
		public double sigmaQuotLog()	{
			return accumulate(0.0,(long prime,int power,double previous)->previous+getLogFactor(prime,power));
		}
		public BigInteger toBigInteger()	{
			return accumulate(BigInteger.ONE,(long prime,int power,BigInteger previous)->previous.multiply(BigInteger.valueOf(LongMath.pow(prime,power))));
		}
		@Override
		public int compareTo(PrimeCollection o) {
			return Double.compare(getLog(),o.getLog());
		}
		private double getLog()	{
			return accumulate(0.0,(long prime,int power,double previous)->previous+power*Math.log(prime));
		}
		@Override
		public String toString()	{
			List<String> substrings=accumulate(new ArrayList<>(),(long prime,int power,List<String> previous)->{
				previous.add(prime+"^"+power);
				return previous;
			});
			return Joiner.on(" · ").join(substrings);
		}
	}
	
	private static PrimeCollection getFirstAbundantWithoutNPrimes(int n)	{
		NavigableSet<PrimeCollection> candidates=new TreeSet<>();
		candidates.add(PrimeCollection.getU(n));
		for (;;)	{
			PrimeCollection head=candidates.pollFirst();
			if (head.sigmaQuotLog()>LOG2) return head;
			candidates.addAll(head.getChildren());
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger mod=BigInteger.valueOf(LongMath.pow(10l,9)+7);
		for (int i=0;i<=3;++i)	{
			PrimeCollection result=getFirstAbundantWithoutNPrimes(i);
			System.out.println(i+" primes:");
			System.out.println("\tThe exponents are "+Arrays.toString(result.primesByExponent)+".");
			BigInteger bigResult=result.toBigInteger();
			BigInteger modResult=bigResult.mod(mod);
			System.out.println("\tResult: "+bigResult+".");
			System.out.println("\tReduced result: "+modResult+".");
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
