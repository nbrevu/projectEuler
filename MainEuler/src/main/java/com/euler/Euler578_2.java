package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.MeisselLehmerPrimeCounter;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler578_2 {
	// Ooops, wrong result for 10^9. Also, far too slow.
	private final static long LIMIT=LongMath.pow(10l,9);
	
	private static int[] listToArray(List<Integer> elements)	{
		return elements.stream().mapToInt(Integer::intValue).toArray();
	}
	
	private static List<int[]> findAllValidCombinations(long limit,long smallPrimeLimit)	{
		long[] primes=Primes.listLongPrimesAsArray(smallPrimeLimit);
		List<int[]> result=new ArrayList<>();
		findAllValidCombinationsRecursive(new ArrayList<>(),1l,false,primes,limit,result);
		return result;
	}
	
	private static void findAllValidCombinationsRecursive(List<Integer> currentList,long smallestRepresentative,boolean isCurrentCombinationValid,long[] primes,long limit,List<int[]> result)	{
		if (isCurrentCombinationValid) result.add(listToArray(currentList));
		int n=currentList.size();
		long prime=primes[n];
		currentList.add(0);
		for (int i=1;;++i)	{
			currentList.set(n,i);
			smallestRepresentative*=prime;
			if (smallestRepresentative>limit) break;
			boolean isValid=isCurrentCombinationValid||((n>=1)&&(i>currentList.get(n-1)));
			findAllValidCombinationsRecursive(currentList,smallestRepresentative,isValid,primes,limit,result);
		}
		currentList.remove(n);
	}
	
	// I'm not sure whether this is computationally feasible, but I'll try.
	private static class CombinationCounter	{
		private final long limit;
		private final MeisselLehmerPrimeCounter primeCounter;
		private final long[] primes;
		public CombinationCounter(long limit)	{
			this.limit=limit;
			primeCounter=new MeisselLehmerPrimeCounter(1+LongMath.sqrt(limit,RoundingMode.DOWN));
			primes=primeCounter.getPrimes();
		}
		public long countDistributions(int[] primePowers)	{
			return getCountRecursive(primePowers,0,0,1l);
		}
		private static long root(long n,long r)	{
			return (long)(Math.floor(Math.pow(n,1d/r)));
		}
		private long getCountRecursive(int[] decomp,int currentDecompIndex,int currentPrimeIndex,long currentProduct)	{
			long result=0l;
			for (int i=currentPrimeIndex;i<primes.length;++i)	{
				long prime=primes[i];
				// ZUTUN! Check the possible overflow of pow?
				long pow=LongMath.pow(prime,decomp[currentDecompIndex]);
				if (limit/pow<currentProduct) break;
				long nextProduct=currentProduct*pow;
				if (currentDecompIndex==decomp.length-2)	{
					long q=limit/nextProduct;
					// ZUTUN! Check precision around the root?
					long maxLimit=root(q,decomp[decomp.length-1]);
					long minLimit=prime;
					if (maxLimit>minLimit) result+=primeCounter.pi(maxLimit)-primeCounter.pi(minLimit);
				}	else result+=getCountRecursive(decomp,1+currentDecompIndex,1+i,nextProduct);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<int[]> combinations=findAllValidCombinations(LIMIT,100l);
		CombinationCounter counter=new CombinationCounter(LIMIT);
		long result=LIMIT;
		for (int[] c:combinations) result-=counter.countDistributions(c);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
