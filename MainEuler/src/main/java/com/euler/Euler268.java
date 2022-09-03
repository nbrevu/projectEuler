package com.euler;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.Primes;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class Euler268 {
	private final static long PRIME_LIMIT=100;
	private final static long LIMIT=10000000000000000l;
	private final static int FACTORS=4;
	
	private static class PrimeProductCombination	{
		public BitSet representation;
		private static List<Long> primes=Collections.emptyList();
		private static List<Double> primeLogs=Collections.emptyList();
		public PrimeProductCombination(long in)	{
			representation=BitSet.valueOf(new long[]{in});
			assert representation.length()<=primes.size();
		}
		public static void setPrimes(List<Long> primeList)	{
			primes=primeList;
			primeLogs=Lists.transform(primes,new Function<Long,Double>()	{
				@Override
				public Double apply(Long in)	{
					return Math.log(in);
				}
			});
		}
		public boolean isViable()	{
			double log=0.0;
			int size=Math.min(representation.length(),primes.size());
			for (int i=0;i<size;++i) if (representation.get(i)) log+=primeLogs.get(i);
			return log<63*Math.log(2.0);
		}
		public int getWeight()	{
			return representation.cardinality();
		}
		public long getMultiplesCount(long limit)	{
			long factor=1;
			int size=Math.min(representation.length(),primes.size());
			for (int i=0;i<size;++i) if (representation.get(i)) factor*=primes.get(i);
			return limit/factor;
		}
	}
	
	public static void main(String[] args)	{
		List<Long> primes=Primes.listLongPrimes(PRIME_LIMIT);
		int N=primes.size();
		CombinatorialNumberCache combs=new CombinatorialNumberCache(N);
		long maxCombination=1l<<N;
		PrimeProductCombination.setPrimes(primes);
		long sum=0;
		for (long l=1;l<maxCombination;++l)	{
			PrimeProductCombination prod=new PrimeProductCombination(l);
			int w=prod.getWeight();
			if ((w<FACTORS)||!prod.isViable()) continue;
			long count=prod.getMultiplesCount(LIMIT)*combs.get(w-1,3);
			if ((w%2)==0) sum+=count;
			else sum-=count;
		}
		System.out.println(sum);
	}
}
