package com.euler;

import java.util.function.IntToLongFunction;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.map.IntDoubleMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntDoubleMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler756_4 {
	private final static int SIZE=10000;
	private final static int LENGTH=100;
	
	private static class LogCombiCalculator	{
		private final double[] factorials;
		private final IntObjMap<IntDoubleMap> cache;
		public LogCombiCalculator(int length)	{
			factorials=new double[1+length];
			factorials[0]=0d;
			for (int i=1;i<=length;++i) factorials[i]=factorials[i-1]+Math.log(i);
			cache=HashIntObjMaps.newMutableMap();
		}
		public double get(int n,int k)	{
			IntDoubleMap subMap=cache.computeIfAbsent(k,(int unused)->HashIntDoubleMaps.newMutableMap());
			return subMap.computeIfAbsent(n,(int unused)->calculate(n,k));
		}
		private double calculate(int n,int k)	{
			return factorials[n]-(factorials[k]+factorials[n-k]);
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
		// Scheiße, el valor está "bien" pero no es lo suficientemente preciso :(. ¿Cómo de bien funciona BigDecimal con logaritmos y tal?
		long tic=System.nanoTime();
		LogCombiCalculator calculator=new LogCombiCalculator(SIZE);
		int[] lastPrimes=Primes.lastPrimeSieve(SIZE);
		FunctionCalculator totients=new FunctionCalculator((int i)->DivisorHolder.getFromFirstPrimes(i,lastPrimes).getTotient(),SIZE,1l);
		int lim1=SIZE+1-LENGTH;
		double[] logsToAdd=new double[2*lim1];
		// First term: special case related to x0=0.
		for (int i=1;i<=lim1;++i) logsToAdd[i-1]=calculator.get(SIZE-i,LENGTH-1)+Math.log(i*totients.getFun(i));
		// Second term: separable term at the end of the generic sum.
		for (int d=1;d<=lim1;++d) logsToAdd[lim1-1+d]=calculator.get(SIZE-1-d,LENGTH-2)+Math.log(d)+Math.log(totients.getSum(SIZE)-totients.getSum(d));
		double denom=calculator.get(SIZE,LENGTH);
		double statResult=0;
		for (double l:logsToAdd) statResult+=Math.exp(l-denom);
		double realSum=totients.getSum(SIZE);
		double result=realSum-statResult;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
