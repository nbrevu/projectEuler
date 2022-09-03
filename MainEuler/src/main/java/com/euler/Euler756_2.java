package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler756_2 {
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
			return (n<k)?BigInteger.ZERO:(factorials[n].divide(factorials[k].multiply(factorials[n-k])));
		}
	}
	
	public static void main(String[] args)	{
		// Wieder: es funktioniert :).
		BigCombiCalculator calculator=new BigCombiCalculator(SIZE);
		int[] lastPrimes=Primes.lastPrimeSieve(SIZE);
		BigInteger[] totients=new BigInteger[1+SIZE];
		totients[1]=BigInteger.ONE;
		for (int i=2;i<=SIZE;++i) totients[i]=BigInteger.valueOf(DivisorHolder.getFromFirstPrimes(i,lastPrimes).getTotient());
		BigInteger denominator=calculator.get(SIZE,LENGTH);
		System.out.println("El denominador es un bicho de "+denominator.toString().length()+" dígitos.");
		BigInteger numerator=BigInteger.ZERO;
		BigInteger realSum=BigInteger.ZERO;
		for (int i=1;i<=SIZE;++i) realSum=realSum.add(totients[i]);
		for (int i=1;i<=SIZE;++i)	{
			BigInteger fI=totients[i];
			// Special case (difference all the way up to 0).
			numerator=numerator.add(fI.multiply(BigInteger.valueOf(i)).multiply(calculator.get(SIZE-i,LENGTH-1)));
			for (int d=1;d<i;++d) numerator=numerator.add(fI.multiply(BigInteger.valueOf(d)).multiply(calculator.get(SIZE-1-d,LENGTH-2)));
		}
		BigDecimal dDenom=new BigDecimal(denominator);
		BigDecimal dNum=new BigDecimal(numerator);
		BigDecimal result=new BigDecimal(realSum).subtract(dNum.divide(dDenom,7,RoundingMode.HALF_UP));
		System.out.println(result);
	}
}
