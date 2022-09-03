package com.euler;

import java.math.BigInteger;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler801_5 {
	private final static int LIMIT=100000;
	private final static long MOD=993353399;
	private final static BigInteger BIG_MOD=BigInteger.valueOf(MOD);
	
	private final static class JordanCalculator	{
		private final LongLongMap baseValues;
		private final LongLongMap fullValues;
		public JordanCalculator()	{
			baseValues=HashLongLongMaps.newMutableMap();
			fullValues=HashLongLongMaps.newMutableMap();
		}
		private static long pow(long base,int exp)	{
			long current=base;
			long prod=1;
			while (exp>0)	{
				if ((exp%2)==1) prod=(prod*current);
				current=(current*current);
				exp/=2;
			}
			return prod;
		}
		private long getBaseValue(long n,long p,int e)	{
			return baseValues.computeIfAbsent(n,(long unused)->	{
				long p2=p*p;
				return (p2-1)*pow(p2,e-1);
			});
		}
		public long getValue(long n,LongIntMap primeDecomp)	{
			return fullValues.computeIfAbsent(n,(long unused)->	{
				long result=1l;
				for (LongIntCursor cursor=primeDecomp.cursor();cursor.moveNext();)	{
					long p=cursor.key();
					int e=cursor.value();
					long pp=pow(p,e);
					result*=getBaseValue(pp,p,e);
				}
				return result;
			});
		}
	}
	
	private static class FunctionCalculator	{
		private final JordanCalculator jordans;
		private final long[] firstPrimes;
		public FunctionCalculator(long limit)	{
			jordans=new JordanCalculator();
			firstPrimes=Primes.firstPrimeSieve(limit);
		}
		public long getForPrime(long p)	{
			long p1=p-1;
			DivisorHolder p1Divs=DivisorHolder.getFromFirstPrimes(p1,firstPrimes);
			LongObjMap<LongIntMap> divisors=p1Divs.getUnsortedListOfDivisorsWithDecomposition();
			long singleCount=p*p1;
			long result=0l;
			for (LongObjCursor<LongIntMap> cursor=divisors.cursor();cursor.moveNext();)	{
				long div=cursor.key();
				if (div==p1) continue;
				long amount=jordans.getValue(div,cursor.value());
				long value=p1*(p1/div);
				result+=amount*value;
				singleCount-=amount;
			}
			return result+singleCount*p1;
		}
	}
	
	public static void main(String[] args)	{
		BigInteger fullResult=BigInteger.ZERO;
		FunctionCalculator calcu=new FunctionCalculator(LIMIT);
		long[] primes=Primes.listLongPrimesAsArray(LIMIT);
		for (long p:primes) fullResult=fullResult.add(BigInteger.valueOf(calcu.getForPrime(p)));
		System.out.println(fullResult);
		System.out.println(fullResult.mod(BIG_MOD));
	}
}
