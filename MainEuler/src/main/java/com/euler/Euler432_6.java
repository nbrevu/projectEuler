package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.BigSumOfTotientCalculator;
import com.euler.common.EulerUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler432_6 {
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
	
	private static class FactoredNumber	{
		private static long limit;
		private final LongIntMap divisors;
		private final long product;
		private final int growAt;
		public static void setLimit(long theLimit)	{
			limit=theLimit;
		}
		public FactoredNumber()	{
			divisors=HashLongIntMaps.newMutableMap();
			product=1l;
			growAt=0;
		}
		private FactoredNumber(LongIntMap divisors,long product,int growAt)	{
			this.divisors=divisors;
			this.product=product;
			this.growAt=growAt;
		}
		public List<FactoredNumber> getChildren()	{
			List<FactoredNumber> result=new ArrayList<>();
			for (int i=growAt;i<PRIMES.length;++i)	{
				FactoredNumber child=getChild(i);
				if (child.product<=limit) result.add(child);
			}
			return result;
		}
		private FactoredNumber getChild(int grow)	{
			long newFactor=PRIMES[grow];
			LongIntMap newDivs=HashLongIntMaps.newMutableMap(divisors);
			EulerUtils.increaseCounter(newDivs,newFactor);
			return new FactoredNumber(newDivs,product*newFactor,grow);
		}
		@Override
		public String toString()	{
			return Long.toString(product);
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(product);
		}
		@Override
		public boolean equals(Object other)	{
			return product==((FactoredNumber)other).product;
		}
		public int howManyDivisors()	{
			int result=1;
			LongIntCursor cursor=divisors.cursor();
			while (cursor.moveNext()) result*=cursor.value()+1;
			return result;
		}
		public long[] getAllDivisors()	{
			long[] result=new long[howManyDivisors()];
			result[0]=1l;
			int addingAt=1;
			LongIntCursor cursor=divisors.cursor();
			while (cursor.moveNext())	{
				int useUpTo=addingAt;
				for (int i=0;i<useUpTo;++i)	{
					long value=result[i];
					for (int j=0;j<cursor.value();++j)	{
						value*=cursor.key();
						result[addingAt]=value;
						++addingAt;
					}
				}
			}
			if (addingAt!=result.length) throw new IllegalStateException();
			return result;
		}
	}
	
	private static SortedMap<Long,FactoredNumber> generateNumbers(long limit)	{
		FactoredNumber.setLimit(limit);
		SortedMap<Long,FactoredNumber> result=new TreeMap<>();
		Set<FactoredNumber> thisGen=ImmutableSet.of(new FactoredNumber());
		while (!thisGen.isEmpty())	{
			Set<FactoredNumber> nextGen=new HashSet<>();
			for (FactoredNumber dl:thisGen) nextGen.addAll(dl.getChildren());
			thisGen.forEach((FactoredNumber l)->result.put(l.product,l));
			thisGen=nextGen;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SortedMap<Long,FactoredNumber> factored=generateNumbers(LongMath.pow(10l,6));
		LongLongMap factors=HashLongLongMaps.newMutableMap();
		TotientCalculator totientCalculator=new TotientCalculator();
		BigSumOfTotientCalculator summator=new BigSumOfTotientCalculator();
		BigInteger result=BigInteger.ZERO;
		for (Map.Entry<Long,FactoredNumber> entry:factored.entrySet())	{
			long key=entry.getKey();
			FactoredNumber value=entry.getValue();
			long factor=totientCalculator.getTotient(key*BASE_NUMBER);
			for (long d:value.getAllDivisors()) if (d!=key) factor-=factors.get(d)*totientCalculator.getTotient(key/d);
			factors.put(key,factor);
			result=result.add(BigInteger.valueOf(factor).multiply(summator.getTotientSum(LIMIT/key)));
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}