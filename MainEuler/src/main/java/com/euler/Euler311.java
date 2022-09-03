package com.euler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.BaseSquareDecomposition.PrimePowerDecompositionFinder;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.LongPair;
import com.euler.common.Primes;

public class Euler311 {
	public final static long N=10_000_000_000l;
	
	private static void addFactor(SortedSet<Long> result,long factor,long max)	{
		Set<Long> toAdd=new HashSet<>();
		for (Long value:result)	{
			long product=value*factor;
			if (product>max) break;
			do	{
				toAdd.add(product);
				product*=factor;
			}	while (product<=max);
		}
		result.addAll(toAdd);
	}
	
	private static long[] getMultipliers(boolean[] composites,long max)	{
		SortedSet<Long> result=new TreeSet<>();
		result.add(1l);
		addFactor(result,4l,max);
		for (long i=3;i<composites.length;i+=4) if (!composites[(int)i]) 	{
			long square=i*i;
			if (square>max) break;
			addFactor(result,square,max);
		}
		return result.stream().mapToLong(Long::longValue).toArray();
	}
	
	private static class SquareSumFinder	{
		private final long[] multipliers;
		private final boolean[] composites;
		private final CombinatorialNumberCache combinatorials;
		private final long maxSquareSum;
		public SquareSumFinder(long n)	{
			maxSquareSum=n/4;
			long maxPrime=maxSquareSum/5;
			long maxMultiplier=maxSquareSum/25;
			composites=Primes.sieve((int)maxPrime);
			multipliers=getMultipliers(composites,maxMultiplier);
			combinatorials=new CombinatorialNumberCache((int)Math.ceil(Math.log(n)/Math.log(5)));
		}
		private long countValidCases(BaseSquareDecomposition decomp,BaseSquareDecomposition decomp2)	{
			NavigableSet<Long> as=new TreeSet<>();
			for (LongPair pair:decomp2.getBaseCombinations()) if (pair.x!=pair.y) as.add(pair.x);
			long result=0l;
			for (LongPair pair:decomp.getBaseCombinations())	{
				long diff=pair.y-pair.x;
				long validCases=as.tailSet(diff,false).size();
				if (validCases>=2) result+=(validCases*(validCases-1))/2;
			}
			return result;
		}
		private long getMultiplier(long n)	{
			int position=Arrays.binarySearch(multipliers,maxSquareSum/n);
			if (position>=0) return position+1;
			else return -1-position;
		}
		private long countQuadrilaterals(long n,BaseSquareDecomposition decomp)	{
			if (decomp.getBaseCombinations().size()==1) return 0l;
			BaseSquareDecomposition decomp2=decomp.scramble();
			long result=0;
			long validCases=countValidCases(decomp,decomp2);
			if (validCases>0) result+=validCases*getMultiplier(n);
			if (n*2<=maxSquareSum)	{
				validCases=countValidCases(decomp2,decomp2.scramble());
				if (validCases>0) result+=validCases*getMultiplier(2*n);
			}
			return result;
		}
		public long countAllQuadrilaterals()	{
			SortedMap<Long,BaseSquareDecomposition> decomps=new TreeMap<>();
			long result=0;
			for (int prime=5;prime<composites.length;prime+=4) if (!composites[prime])	{
				decomps.tailMap(1+(maxSquareSum/prime)).clear();
				PrimePowerDecompositionFinder finder=new PrimePowerDecompositionFinder(prime,combinatorials);
				Map<Long,BaseSquareDecomposition> toAdd=new HashMap<>();
				long primePower=prime;
				for (int exp=1;primePower<maxSquareSum;++exp,primePower*=prime)	{
					BaseSquareDecomposition primePowerDecomps=finder.getFor(exp);
					toAdd.put(primePower,primePowerDecomps);
					for (Map.Entry<Long,BaseSquareDecomposition> other:decomps.entrySet())	{
						long number=primePower*other.getKey();
						if (number>maxSquareSum) break;
						toAdd.put(number,other.getValue().combineWith(primePowerDecomps));
					}
				}
				for (Map.Entry<Long,BaseSquareDecomposition> entry:toAdd.entrySet()) result+=countQuadrilaterals(entry.getKey(),entry.getValue());
				decomps.putAll(toAdd);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SquareSumFinder finder=new SquareSumFinder(N);
		long result=finder.countAllQuadrilaterals();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
