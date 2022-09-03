package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.function.IntToLongFunction;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler756_3 {
	// The theory works, but the numbers are far too big :(.
	private final static int SIZE=10000;
	private final static int LENGTH=100;
	
	private static class BigCombiCalculator	{
		private final BigInteger[] factorials;
		private final IntObjMap<IntObjMap<BigInteger>> cache;
		public BigCombiCalculator(int length)	{
			factorials=new BigInteger[1+length];
			factorials[0]=BigInteger.ONE;
			for (int i=1;i<=length;++i) factorials[i]=factorials[i-1].multiply(BigInteger.valueOf(i));
			cache=HashIntObjMaps.newMutableMap();
		}
		public BigInteger get(int n,int k)	{
			IntObjMap<BigInteger> subMap=cache.computeIfAbsent(k,(int unused)->HashIntObjMaps.newMutableMap());
			return subMap.computeIfAbsent(n,(int unused)->calculate(n,k));
		}
		private BigInteger calculate(int n,int k)	{
			return factorials[n].divide(factorials[k].multiply(factorials[n-k]));
		}
	}
	
	private static class FunctionCalculator	{
		private final long[] funValues;
		private final long[] funSums;
		public FunctionCalculator(IntToLongFunction fun,int maxSize,long fun1)	{
			funValues=new long[1+maxSize];
			funSums=new long[1+maxSize];
			funValues[1]=fun1;
			funSums[1]=fun1;
			for (int i=2;i<=maxSize;++i)	{
				funValues[i]=fun.applyAsLong(i);
				funSums[i]=funSums[i-1]+funValues[i];
			}
		}
		public long getFun(int i)	{
			return funValues[i];
		}
		public long getSum(int i)	{
			return funSums[i];
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigCombiCalculator calculator=new BigCombiCalculator(SIZE);
		int[] lastPrimes=Primes.lastPrimeSieve(SIZE);
		FunctionCalculator totients=new FunctionCalculator((int i)->DivisorHolder.getFromFirstPrimes(i,lastPrimes).getTotient(),SIZE,1l);
		int lim1=SIZE+1-LENGTH;
		// First term: special case related to x0=0.
		BigInteger a=BigInteger.ZERO;
		for (int i=1;i<=lim1;++i)	{
			BigInteger factor1=BigInteger.valueOf(i*totients.getFun(i));
			BigInteger factor2=calculator.get(SIZE-i,LENGTH-1);
			a=a.add(factor1.multiply(factor2));
		}
		// Second term: separable term at the end of the generic sum.
		BigInteger b=BigInteger.ZERO;
		for (int d=1;d<=lim1;++d) b=b.add(calculator.get(SIZE-1-d,LENGTH-2).multiply(BigInteger.valueOf(d)).multiply(BigInteger.valueOf(totients.getSum(SIZE)-totients.getSum(d))));
		BigDecimal realSum=BigDecimal.valueOf(totients.getSum(SIZE));
		BigDecimal numerator=new BigDecimal(a.add(b));
		BigDecimal denominator=new BigDecimal(calculator.get(SIZE,LENGTH));
		BigDecimal result=realSum.subtract(numerator.divide(denominator,7,RoundingMode.HALF_UP));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
