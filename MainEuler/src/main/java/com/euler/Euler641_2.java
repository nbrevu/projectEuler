package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler641_2 {
	private final static int BASE=10;
	private final static int EXPONENT=36;
	
	private final static class Decomposition implements Iterable<int[]>	{
		private final List<Integer> factors;
		public Decomposition(Collection<Integer> data)	{
			factors=new ArrayList<>(data);
			factors.sort(null);
		}
		public List<Integer> getFactors()	{
			return factors;
		}
		public Set<Decomposition> getChildren()	{
			int N=factors.size();
			Set<Decomposition> result=new HashSet<>();
			for (int i=0;i<N-1;++i) for (int j=i+1;j<N;++j) result.add(getChild(i,j));
			return result;
		}
		// This is super inefficient, but for this problem it does the job.
		public Set<Decomposition> getAllDescendants()	{
			Set<Decomposition> result=new HashSet<>();
			Set<Decomposition> currentGeneration=ImmutableSet.of(this);
			do	{
				Set<Decomposition> nextGeneration=new HashSet<>();
				for (Decomposition decomp:currentGeneration)	{
					if (result.contains(decomp)) continue;
					nextGeneration.addAll(decomp.getChildren());
				}
				result.addAll(currentGeneration);
				currentGeneration=nextGeneration;
			}	while (!currentGeneration.isEmpty());
			return result;
		}
		private Decomposition getChild(int i,int j)	{
			List<Integer> result=new ArrayList<>(factors);
			int newFactor=factors.get(i)*factors.get(j);
			result.remove(j);
			result.remove(i);
			result.add(newFactor);
			return new Decomposition(result);
		}
		@Override
		public boolean equals(Object other)	{
			return factors.equals(((Decomposition)other).factors);
		}
		@Override
		public int hashCode()	{
			return factors.hashCode();
		}
		private int[] toArray()	{
			int N=factors.size();
			int[] result=new int[N];
			for (int i=0;i<N;++i) result[i]=factors.get(i).intValue();
			return result;
		}
		@Override
		public Iterator<int[]> iterator()	{
			return new EulerUtils.DestructiveIntArrayPermutationGenerator(toArray()).iterator();
		}
	}
	
	private final static class Factorer	{
		private final int[] lastPrimeSieve;
		private final int[] primesArray;
		private final int heuristicLimit;
		private final double logLimit;
		public final int expectedLimit;
		public Factorer(int base,int exponent)	{
			int maxNumber=IntMath.pow(base,IntMath.divide(exponent,4,RoundingMode.UP));
			lastPrimeSieve=Primes.firstPrimeSieve(maxNumber);
			primesArray=listIntPrimesAsArray(lastPrimeSieve);
			logLimit=exponent*Math.log(base);
			heuristicLimit=DoubleMath.roundToInt(logLimit/Math.log(2.0),RoundingMode.DOWN)+1;
			expectedLimit=Math.min(maxNumber,calculateExpectedLimit(base,exponent,primesArray));
		}
		public SortedMultiset<Integer> factorOrNull(int in)	{
			// Will return null if any heuristic fails.
			SortedMultiset<Integer> result=TreeMultiset.create();
			do	{
				int p=lastPrimeSieve[in];
				if (p==0) p=in;
				if (p>heuristicLimit) return null;
				result.add(p);
				in/=p;
			}	while (in>1);
			return heuristicOverDecomp(result)?result:null;
		}
		private static int[] listIntPrimesAsArray(int[] lastPrimeSieve)	{
			int N=lastPrimeSieve.length;
			int size=2;
			boolean add4=false;
			for (int i=5;i<N;i+=(add4?4:2),add4=!add4) if (lastPrimeSieve[i]==0) ++size;
			int[] result=new int[size];
			result[0]=2;
			result[1]=3;
			add4=false;
			int index=2;
			for (int i=5;i<N;i+=(add4?4:2),add4=!add4) if (lastPrimeSieve[i]==0)	{
				result[index]=i;
				++index;
			}
			return result;
		}
		public boolean heuristicOverDecomp(Collection<Integer> exponents)	{
			return heuristicOverDecomp(ImmutableSortedMultiset.copyOf(exponents));
		}
		public boolean heuristicOverDecomp(SortedMultiset<Integer> exponents)	{
			int pIndex=0;
			double log=0.0;
			for (int exp:exponents.descendingMultiset())	{
				log+=(exp-1)*Math.log(primesArray[pIndex]);
				++pIndex;
				if (log>logLimit) return false;
			}
			return true;
		}
		private Set<Decomposition> getBaseDecompositions(SortedMultiset<Integer> factors)	{
			return new Decomposition(factors).getAllDescendants();
		}
		public List<Decomposition> getAllDecompositions(int in)	{
			SortedMultiset<Integer> factors=factorOrNull(in);
			if (factors==null) return Collections.emptyList();
			List<Decomposition> result=new ArrayList<>();
			for (Decomposition decomp:getBaseDecompositions(factors)) if (heuristicOverDecomp(decomp.getFactors())) result.add(decomp);
			return result;
		}
		public long countNumbers(int[] exponents)	{
			return countNumbersRecursive(exponents,0,0,logLimit);
		}
		private long countNumbersRecursive(int[] exponents,int expIndex,int primeIndex,double maxLog)	{
			int actualExp=exponents[expIndex]-1;
			if (expIndex==exponents.length-1)	{
				int upperLimit=findInPrimes(Math.exp(maxLog/actualExp),primeIndex);
				return Math.max(0,upperLimit-primeIndex);
			}	else	{
				long result=0;
				double maxLogToSearchFor=maxLog;
				int upperLimit=findInPrimes(Math.exp(maxLogToSearchFor/actualExp),primeIndex);
				for (int i=primeIndex;i<upperLimit;++i)	{
					long counter=countNumbersRecursive(exponents,1+expIndex,1+i,maxLog-actualExp*Math.log(primesArray[i]));
					if (counter==0) break;
					result+=counter;
				}
				return result;
			}
		}
		private int findInPrimes(double toFind,int firstIndex)	{
			int lastIndex=primesArray.length;
			int toFindInt=1+DoubleMath.roundToInt(toFind,RoundingMode.DOWN);
			int binarySearch=Arrays.binarySearch(primesArray,firstIndex,lastIndex,toFindInt);
			if (binarySearch>=0) return binarySearch;
			else return -1-binarySearch;
		}
		private static int calculateExpectedLimit(int base,int exponent,int[] primes)	{
			long trueLimit=LongMath.pow(base,IntMath.divide(exponent,4,RoundingMode.UP));
			int count=0;
			long prod=1;
			for (int i=0;;++i)	{
				prod*=primes[i];
				if (prod>trueLimit) break;
				else ++count;
			}
			// We must ensure that the limit is an even power of 5, since only even powers of 5 are of the form 6n+1.
			if ((count%2)==1) ++count;
			return IntMath.pow(5,count);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Factorer factorer=new Factorer(BASE,EXPONENT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(seconds+" seconds invested calculating primes.");
		long counter=1;	// For "1", of course.
		int limit=1000000000;
		// for (int i=7;i<=limit;i+=6) for (Decomposition decomp:factorer.getAllDecompositions(i)) for (int[] exponents:decomp) counter+=factorer.countNumbers(exponents);
		boolean add4=false;
		for (int i=5;i<=limit;i+=add4?4:2,add4=!add4) for (Decomposition decomp:factorer.getAllDecompositions(i)) for (int[] exponents:decomp) counter+=factorer.countNumbers(exponents);
		long tac2=System.nanoTime();
		seconds=(tac2-tac)*1e-9;
		System.out.println(seconds+" seconds invested actually calculating the solution.");
		System.out.println(counter);
	}
}
