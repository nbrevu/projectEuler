package com.euler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.IntToLongFunction;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler756_7 {
	private final static int N=12345678;
	private final static int M=12345;
	
	private final static int SCALE=50;
	
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
	
	private static BigDecimal divide(BigDecimal a,BigDecimal b)	{
		return a.divide(b,SCALE,RoundingMode.HALF_UP);
	}
	private static BigDecimal divide(double a,double b)	{
		return divide(BigDecimal.valueOf(a),BigDecimal.valueOf(b));
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] lastPrimes=Primes.lastPrimeSieve(N);
		FunctionCalculator totients=new FunctionCalculator((int i)->DivisorHolder.getFromFirstPrimes(i,lastPrimes).getTotient(),N,1l);
		int lim1=N+1-M;
		BigDecimal factor1=divide(M,N);
		BigDecimal factor2=divide(M-1,N-1).multiply(factor1);
		BigDecimal statSum=BigDecimal.ZERO;
		for (int i=1;i<=lim1;++i)	{
			if (i>1)	{
				BigDecimal toMult=BigDecimal.valueOf(N-M-i+2);
				factor1=factor1.multiply(toMult);
				factor1=divide(factor1,BigDecimal.valueOf(N-i+1));
				factor2=factor2.multiply(toMult);
				factor2=divide(factor2,BigDecimal.valueOf(N-i));
			}
			BigDecimal toAdd1=BigDecimal.valueOf(totients.getFun(i)).multiply(factor1);
			BigDecimal toAdd2=BigDecimal.valueOf(totients.getSum(N)-totients.getSum(i)).multiply(factor2);
			statSum=statSum.add(BigDecimal.valueOf(i).multiply(toAdd1.add(toAdd2)));
		}
		BigDecimal realSum=BigDecimal.valueOf(totients.getSum(N));
		BigDecimal result=realSum.subtract(statSum);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result); 
	}
}
