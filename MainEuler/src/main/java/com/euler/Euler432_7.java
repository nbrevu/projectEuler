package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.BigSumOfTotientCalculator;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler432_7 {
	private final static int[] PRIMES=new int[] {2,3,5,7,11,13,17};	// The product of these numbers is 510510.
	private final static long BASE_NUMBER=Arrays.stream(PRIMES).reduce((int a,int b)->a*b).getAsInt();
	private final static long LIMIT=LongMath.pow(10l,11);
	
	private static class TotientCalculator	{
		private final LongLongMap cache;
		public TotientCalculator()	{
			cache=HashLongLongMaps.newMutableMap();
		}
		public long getTotient(long in)	{
			return cache.computeIfAbsent(in,(long n)->{
				long result=1;
				for (int p:PRIMES) if ((n%p)==0)	{
					result*=p-1;
					for (;;)	{
						n/=p;
						if ((n%p)==0) result*=p;
						else break;
					}
				}
				if (n>1) throw new IllegalArgumentException();
				return result;
			});
		}
	}
	
	private static class CandidateGenerator	{
		private final long number;
		private final int growAt;
		public CandidateGenerator()	{
			number=1l;
			growAt=0;
		}
		private CandidateGenerator(long product,int growAt)	{
			this.number=product;
			this.growAt=growAt;
		}
		public List<CandidateGenerator> getChildren()	{
			List<CandidateGenerator> result=new ArrayList<>();
			for (int i=growAt;i<PRIMES.length;++i)	{
				CandidateGenerator child=getChild(i);
				if (child.number<=LIMIT) result.add(child);
			}
			return result;
		}
		private CandidateGenerator getChild(int grow)	{
			return new CandidateGenerator(number*PRIMES[grow],grow);
		}
		public long getNumber()	{
			return number;
		}
	}
	
	private static SortedSet<Long> generateNumbers()	{
		SortedSet<Long> result=new TreeSet<>();
		Set<CandidateGenerator> thisGen=ImmutableSet.of(new CandidateGenerator());
		while (!thisGen.isEmpty())	{
			Set<CandidateGenerator> nextGen=new HashSet<>();
			for (CandidateGenerator dl:thisGen) nextGen.addAll(dl.getChildren());
			thisGen.forEach((CandidateGenerator l)->result.add(l.getNumber()));
			thisGen=nextGen;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SortedSet<Long> candidates=generateNumbers();
		TotientCalculator totientCalculator=new TotientCalculator();
		BigSumOfTotientCalculator summator=new BigSumOfTotientCalculator();
		BigInteger result=BigInteger.ZERO;
		for (long key:candidates) result=result.add(summator.getTotientSum(LIMIT/key));
		result=result.multiply(BigInteger.valueOf(totientCalculator.getTotient(BASE_NUMBER)));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}