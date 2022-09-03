package com.euler;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Lists;

public class Euler561 {
	private static class DivisorLister	{
		private List<Long> primes;
		public int size()	{
			return primes.size();
		}
		public long getNumber()	{
			long result=1l;
			for (Long p:primes) result*=p;
			return result;
		}
		public long getNumber(int exponent)	{
			return (long)Math.pow(getNumber(),exponent);
		}
		public Set<Long> listDivisors(int exponent)	{
			Set<Long> result=new HashSet<>();
			result.add(1L);
			for (long p:primes)	{
				Set<Long> newFactors=new HashSet<>();
				for (long oldFactor:result)	{
					 for (int i=1;i<=exponent;++i)	{
						 oldFactor*=p;
						 newFactors.add(oldFactor);
					 }
				}
				result.addAll(newFactors);
			}
			return result;
		}
		public long[] listDivisorsAsArray(int exponent)	{
			Set<Long> divisors=listDivisors(exponent);
			long result[]=new long[divisors.size()];
			int index=0;
			for (Long l:divisors)	{
				result[index]=l;
				++index;
			}
			return result;
		}
		public DivisorLister(Collection<Long> primes)	{
			this.primes=Lists.newArrayList(primes);
		}
	}
	public static int countDivisible(long[] divisors)	{
		int count=0;
		int s=divisors.length;
		for (int i=0;i<s-1;++i) for (int j=i+1;j<s;++j) if ((divisors[j]%divisors[i])==0) ++count;
		return count;
	}
	
	public static void main(String[] args)	{
		DivisorLister div6=new DivisorLister(Arrays.asList(2l,3l));
		DivisorLister div30=new DivisorLister(Arrays.asList(2l,3l,5l));
		DivisorLister div210=new DivisorLister(Arrays.asList(2l,3l,5l,7l));
		DivisorLister div2310=new DivisorLister(Arrays.asList(2l,3l,5l,7l,11l));
		List<DivisorLister> allListers=Arrays.asList(div6,div30,div210,div2310);
		SortedMap<Long,Integer> counts=new TreeMap<>();
		for (int i=1;i<=5;++i) for (DivisorLister lister:allListers)	{
			long divisors[]=lister.listDivisorsAsArray(i);
			assert(divisors.length==(int)Math.pow(lister.size(),i+1));
			counts.put(lister.getNumber(i),countDivisible(divisors));
		}
		for (Map.Entry<Long,Integer> entry:counts.entrySet())	{
			System.out.println(""+entry.getKey()+" => "+entry.getValue());
		}
	}
}
