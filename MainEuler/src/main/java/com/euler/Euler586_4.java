package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Primes;
import com.google.common.collect.Iterables;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler586_4 {
	private final static long GOAL=LongMath.pow(10l,15);
	private final static int COUNTER=40;
	
	private static class Decomposition	{
		private final int[] factors;
		private Decomposition(int[] factors)	{
			this.factors=factors;
			Arrays.sort(factors);
		}
		public static Decomposition emptyDecomposition()	{
			return new Decomposition(new int[0]);
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(factors);
		}
		@Override
		public boolean equals(Object other)	{
			Decomposition dOther=(Decomposition)other;
			return Arrays.equals(factors,dOther.factors);
		}
		@Override
		public String toString()	{
			return Arrays.toString(factors);
		}
		private void addFactor(int newFactor,Set<Decomposition> target)	{
			for (int i=0;i<factors.length;++i) if ((i==0)||(factors[i]!=factors[i-1]))	{
				int[] newFactors=Arrays.copyOf(factors,factors.length);
				newFactors[i]*=newFactor;
				target.add(new Decomposition(newFactors));
			}
			int[] newFactors=Arrays.copyOf(factors,1+factors.length);
			newFactors[factors.length]=newFactor;
			target.add(new Decomposition(newFactors));
		}
		public static Set<Decomposition> addFactor(Set<Decomposition> lastDecompositions,int newFactor)	{
			Set<Decomposition> result=new HashSet<>();
			for (Decomposition d:lastDecompositions) d.addFactor(newFactor,result);
			return result;
		}
		public int[] getExponents()	{
			int[] result=new int[factors.length];
			for (int i=0;i<factors.length;++i) result[i]=factors[factors.length-1-i]-1;
			return result;
		}
	}
	
	private static Set<Decomposition> getAllDecompositions(DivisorHolder divs)	{
		Set<Decomposition> current=Set.of(Decomposition.emptyDecomposition());
		for (LongIntCursor cursor=divs.getFactorMap().cursor();cursor.moveNext();) for (int i=0;i<cursor.value();++i) current=Decomposition.addFactor(current,(int)(cursor.key()));
		return current;
	}
	
	private static int[] getFirstRelevantPrimes(int[] lastPrimes,int howMany)	{
		IntStream.Builder result=IntStream.builder();
		int counter=0;
		boolean add8=true;
		for (int i=11;i<lastPrimes.length;i+=(add8?8:2),add8=!add8) if (lastPrimes[i]==0)	{
			result.accept(i);
			++counter;
			if (counter>=howMany) return result.build().toArray();
		}
		throw new NoSuchElementException("Not enough primes!");
	}
	
	private static class CombinationCounter	{
		private final List<int[]> allDecompositions;
		private final long[] relevantPrimes;
		private final long[] extraFactors;
		private final long maxValue;
		private CombinationCounter(List<int[]> allDecompositions,long[] relevantPrimes,long[] extraFactors,long maxValue)	{
			this.allDecompositions=allDecompositions;
			this.relevantPrimes=relevantPrimes;
			this.extraFactors=extraFactors;
			this.maxValue=maxValue;
		}
		public long getCount()	{
			LongSet used=HashLongSets.newMutableSet();
			long result=0l;
			for (int[] decomp:allDecompositions) result+=getCountRecursive(decomp,0,0,1l,used);
			return result;
		}
		private long getCountRecursive(int[] decomp,int currentDecompIndex,int currentPrimeIndex,long currentProduct,LongSet used)	{
			long result=0l;
			boolean nextIndexIsSame=(currentDecompIndex<decomp.length-1)&&(decomp[currentDecompIndex]==decomp[currentDecompIndex+1]);
			for (int i=currentPrimeIndex;i<relevantPrimes.length;++i)	{
				long prime=relevantPrimes[i];
				if (used.contains(prime)) continue;
				long pow=LongMath.pow(prime,decomp[currentDecompIndex]);
				if (maxValue/pow<currentProduct) break;
				long nextProduct=currentProduct*pow;
				if (currentDecompIndex>=decomp.length-1)	{
					long q=maxValue/nextProduct;
					int index=Arrays.binarySearch(extraFactors,q);
					if (index<0) result+=-1-index;
					else result+=1+index;
				}	else	{
					used.add(prime);
					int nextPrimeIndex=(nextIndexIsSame)?(1+i):0;
					result+=getCountRecursive(decomp,1+currentDecompIndex,nextPrimeIndex,nextProduct,used);
					used.removeLong(prime);
				}
			}
			return result;
		}
		public static CombinationCounter create(long maxValue,int expectedCounter,int smallPrimeLimit,int smallPrimeCount)	{
			int[] initialPrimeSet=Primes.lastPrimeSieve(smallPrimeLimit);
			int[] smallPrimes=getFirstRelevantPrimes(initialPrimeSet,smallPrimeCount);
			Set<Decomposition> evenSets=getAllDecompositions(DivisorHolder.getFromFirstPrimes(2*expectedCounter,initialPrimeSet));
			Set<Decomposition> oddSets=getAllDecompositions(DivisorHolder.getFromFirstPrimes(2*expectedCounter+1,initialPrimeSet));
			BigInteger[] bigSmallPrimes=IntStream.of(smallPrimes).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
			long maxRelevantPrime=1l;
			long maxExtraFactor=1l;
			BigInteger bigValue=BigInteger.valueOf(maxValue);
			List<int[]> validDecompositions=new ArrayList<>();
			for (Decomposition decomp:Iterables.concat(evenSets,oddSets))	{
				int[] exponents=decomp.getExponents();
				Pair<BigInteger,BigInteger> accumulatedFactors=accumulateAll(bigSmallPrimes,exponents);
				BigInteger incompleteAccumulator=accumulatedFactors.first;
				BigInteger completeAccumulator=accumulatedFactors.second;
				long q=(bigValue.divide(incompleteAccumulator)).longValueExact();
				long lastPrime=1+(long)Math.ceil(Math.pow(q+1,1d/exponents[exponents.length-1]));
				if (lastPrime>=smallPrimes[exponents.length-1])	{
					validDecompositions.add(exponents);
					maxRelevantPrime=Math.max(maxRelevantPrime,lastPrime);
					maxExtraFactor=Math.max(maxExtraFactor,1+(bigValue.divide(completeAccumulator).longValueExact()));
				}
			}
			long maxPrimeNeededForExtraFactor=Math.max(5l,1+LongMath.sqrt(maxExtraFactor,RoundingMode.UP));
			boolean[] composites=Primes.sieve((int)(Math.max(maxRelevantPrime,maxPrimeNeededForExtraFactor)));
			return new CombinationCounter(validDecompositions,getRelevantPrimes(composites,maxRelevantPrime),getExtraFactors(composites,maxExtraFactor),maxValue);
		}
		private static long[] getRelevantPrimes(boolean[] composites,long maxRelevantPrime)	{
			LongStream.Builder result=LongStream.builder();
			boolean add8=true;
			for (int i=11;i<maxRelevantPrime;i+=(add8?8:2),add8=!add8) if (!composites[i]) result.accept(i);
			return result.build().toArray();
		}
		private static long[] getExtraFactors(boolean[] composites,long maxFactor)	{
			SortedSet<Long> result=new TreeSet<>();
			result.add(1l);
			addExtraFactor(result,4l,maxFactor);
			addExtraFactor(result,9l,maxFactor);
			addExtraFactor(result,5l,maxFactor);
			boolean add4=true;
			for (long i=7;;i+=(add4?4:2),add4=!add4) if (!composites[(int)i])	{
				long m=i%10l;
				if ((m==1)||(m==9)) continue;
				long i2=i*i;
				if (i2>maxFactor) break;
				addExtraFactor(result,i2,maxFactor);
			}
			return result.stream().mapToLong(Long::longValue).toArray();
		}
		private static void addExtraFactor(SortedSet<Long> result,long newFactor,long limit)	{
			Set<Long> toAdd=new HashSet<>();
			for (long l:result)	{
				long factor=l*newFactor;
				while (factor<=limit)	{
					toAdd.add(factor);
					factor*=newFactor;
				}
			}
			result.addAll(toAdd);
		}
		private static Pair<BigInteger,BigInteger> accumulateAll(BigInteger[] smallPrimes,int[] exponents)	{
			BigInteger result=BigInteger.ONE;
			for (int i=0;i<exponents.length-1;++i) result=result.multiply(smallPrimes[i].pow(exponents[i]));
			BigInteger result2=result.multiply(smallPrimes[exponents.length-1].pow(exponents[exponents.length-1]));
			return new Pair<>(result,result2);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CombinationCounter counter=CombinationCounter.create(GOAL,COUNTER,100,6);
		long result=counter.getCount();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
