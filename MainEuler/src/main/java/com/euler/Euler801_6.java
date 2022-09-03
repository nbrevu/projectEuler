package com.euler;

import java.math.BigInteger;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler801_6 {
	private final static int LIMIT=100000;
	private final static long MOD=993353399;
	private final static BigInteger BIG_MOD=BigInteger.valueOf(MOD);
	
	private final static class JordanCalculator	{
		private final LongObjMap<BigInteger> baseValues;
		private final LongObjMap<BigInteger> fullValues;
		public JordanCalculator()	{
			baseValues=HashLongObjMaps.newMutableMap();
			fullValues=HashLongObjMaps.newMutableMap();
		}
		private static long pow(long base,int exp)	{
			long current=base;
			long prod=1;
			while (exp>0)	{
				if ((exp%2)==1) prod*=current;
				current*=current;
				exp/=2;
			}
			return prod;
		}
		private static BigInteger pow(BigInteger base,int exp)	{
			BigInteger current=base;
			BigInteger prod=BigInteger.ONE;
			while (exp>0)	{
				if ((exp%2)==1) prod=prod.multiply(current);
				current=current.multiply(current);
				exp/=2;
			}
			return prod;
		}
		private BigInteger getBaseValue(long n,long p,int e)	{
			return baseValues.computeIfAbsent(n,(long unused)->	{
				BigInteger bp=BigInteger.valueOf(p);
				BigInteger p2=bp.multiply(bp);
				return p2.subtract(BigInteger.ONE).multiply(pow(p2,e-1));
			});
		}
		public BigInteger getValue(long n,LongIntMap primeDecomp)	{
			return fullValues.computeIfAbsent(n,(long unused)->	{
				BigInteger result=BigInteger.ONE;
				for (LongIntCursor cursor=primeDecomp.cursor();cursor.moveNext();)	{
					long p=cursor.key();
					int e=cursor.value();
					long pp=pow(p,e);
					result=result.multiply(getBaseValue(pp,p,e));
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
		public BigInteger getForPrime(long p)	{
			long p1=p-1;
			DivisorHolder p1Divs=DivisorHolder.getFromFirstPrimes(p1,firstPrimes);
			LongObjMap<LongIntMap> divisors=p1Divs.getUnsortedListOfDivisorsWithDecomposition();
			BigInteger bp=BigInteger.valueOf(p);
			BigInteger singleCount=bp.multiply(bp.subtract(BigInteger.ONE));
			BigInteger result=BigInteger.ZERO;
			for (LongObjCursor<LongIntMap> cursor=divisors.cursor();cursor.moveNext();)	{
				long div=cursor.key();
				if (div==p1) continue;
				BigInteger amount=jordans.getValue(div,cursor.value());
				long value=p1/div;
				result=result.add(amount.multiply(BigInteger.valueOf(value)));
				singleCount=singleCount.subtract(amount);
			}
			return result.add(singleCount).multiply(BigInteger.valueOf(p1));
		}
	}
	
	public static void main(String[] args)	{
		BigInteger fullResult=BigInteger.ZERO;
		FunctionCalculator calcu=new FunctionCalculator(LIMIT);
		long[] primes=Primes.listLongPrimesAsArray(LIMIT);
		for (long p:primes) fullResult=fullResult.add(calcu.getForPrime(p));
		System.out.println(fullResult);
		System.out.println(fullResult.mod(BIG_MOD));
	}
}
