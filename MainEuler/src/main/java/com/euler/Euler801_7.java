package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.RabinMiller;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler801_7 {
	private final static long START=LongMath.pow(10l,16)+1;
	private final static long END=START+LongMath.pow(10l,6);
	private final static long MOD=993353399;
	private final static BigInteger BIG_MOD=BigInteger.valueOf(MOD);
	
	private final static int[] INT_WITNESSES=new int[] {2,3,5,7,11,13,17,19,23};
	private final static BigInteger[] WITNESSES=Arrays.stream(INT_WITNESSES).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
	
	private final static class Jordan2Calculator	{
		private final LongObjMap<BigInteger> baseValues;
		private final LongObjMap<BigInteger> fullValues;
		public Jordan2Calculator()	{
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
		private final Jordan2Calculator jordans;
		private final StandardPrimeDecomposer decomposer;
		public FunctionCalculator(long limit)	{
			jordans=new Jordan2Calculator();
			decomposer=new StandardPrimeDecomposer((int)LongMath.sqrt(limit,RoundingMode.UP));
		}
		public BigInteger getForPrime(long p)	{
			long p1=p-1;
			DivisorHolder p1Divs=decomposer.decompose(p1);
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
		long tic=System.nanoTime();
		BigInteger fullResult=BigInteger.ZERO;
		RabinMiller primeTest=new RabinMiller();
		FunctionCalculator fs=new FunctionCalculator(END);
		for (long p=START;p<=END;p+=2) if ((p%3==0)||(p%5==0)||(p%7==0)) continue;
		else if (primeTest.isPrime(BigInteger.valueOf(p),WITNESSES)) fullResult=fullResult.add(fs.getForPrime(p));
		BigInteger result=fullResult.mod(BIG_MOD);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(fullResult);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
