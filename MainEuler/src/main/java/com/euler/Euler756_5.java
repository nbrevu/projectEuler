package com.euler;

import java.math.BigDecimal;
import java.util.function.IntToLongFunction;

import com.euler.common.BigDecimalUtils;
import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler756_5 {
	// This could be done, but in about 10-12 hours.
	private final static int SIZE=12345678;
	private final static int LENGTH=12345;
	
	private final static int SCALE=20;
	
	private static class LogCombiCalculator	{
		private final BigDecimal[] factorials;
		public LogCombiCalculator(int length)	{
			factorials=new BigDecimal[1+length];
			factorials[0]=BigDecimal.ZERO;
			for (int i=1;i<=length;++i) factorials[i]=factorials[i-1].add(BigDecimalUtils.ln(BigDecimal.valueOf(i),SCALE));
		}
		private BigDecimal get(int n,int k)	{
			return factorials[n].subtract(factorials[k].add(factorials[n-k]));
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
		BigDecimal[] logsToAdd=new BigDecimal[2*lim1];
		{
			long subtic=System.nanoTime();
			double subseconds=(subtic-tic)*1e-9;
			System.out.println("Factoriales listos ("+subseconds+") seconds.");
		}
		// First term: special case related to x0=0.
		for (int i=1;i<=lim1;++i) logsToAdd[i-1]=calculator.get(SIZE-i,LENGTH-1).add(BigDecimalUtils.ln(BigDecimal.valueOf(i*totients.getFun(i)),SCALE));
		{
			long subtic=System.nanoTime();
			double subseconds=(subtic-tic)*1e-9;
			System.out.println("Parte A lista ("+subseconds+") seconds.");
		}
		// Second term: separable term at the end of the generic sum.
		for (int d=1;d<=lim1;++d) logsToAdd[lim1-1+d]=calculator.get(SIZE-1-d,LENGTH-2).add(BigDecimalUtils.ln(BigDecimal.valueOf(d),SCALE)).add(BigDecimalUtils.ln(BigDecimal.valueOf(totients.getSum(SIZE)-totients.getSum(d)),SCALE));
		{
			long subtic=System.nanoTime();
			double subseconds=(subtic-tic)*1e-9;
			System.out.println("Parte B lista ("+subseconds+") seconds.");
		}
		BigDecimal denom=calculator.get(SIZE,LENGTH);
		BigDecimal statResult=BigDecimal.ZERO;
		for (BigDecimal l:logsToAdd) statResult=statResult.add(BigDecimalUtils.exp(l.subtract(denom),SCALE));
		BigDecimal realSum=BigDecimal.valueOf(totients.getSum(SIZE));
		BigDecimal result=realSum.subtract(statResult);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
