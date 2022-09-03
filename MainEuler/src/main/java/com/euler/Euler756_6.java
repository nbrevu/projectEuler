package com.euler;

import java.util.Locale;
import java.util.function.IntToLongFunction;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;

public class Euler756_6 {
	// The theory works, but the numbers are far too big :(.
	private final static int N=12345678;
	private final static int M=12345;
	
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
		int[] lastPrimes=Primes.lastPrimeSieve(N);
		FunctionCalculator totients=new FunctionCalculator((int i)->DivisorHolder.getFromFirstPrimes(i,lastPrimes).getTotient(),N,1l);
		int lim1=N+1-M;
		double factor1=M/(double)N;
		double factor2=(factor1*(M-1))/(N-1);
		double statSum=0d;
		for (int i=1;i<=lim1;++i)	{
			if (i>1)	{
				factor1*=N-M-i+2;
				factor1/=N-i+1;
				factor2*=N-M-i+2;
				factor2/=N-i;
			}
			statSum+=i*totients.getFun(i)*factor1;
			statSum+=(double)(totients.getSum(N)-totients.getSum(i))*(double)i*factor2;
		}
		double realSum=totients.getSum(N);
		double result=realSum-statSum;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
		/*
		 * I'm getting 607244.062500 and I believe that's really really close to the actual value, but there might be some sliiiiiight precision
		 * issue. Should I use long double? Or maybe BigDecimal with 20 or 30 digits? Let's try...
		 */
		System.out.println(String.format(Locale.UK,"%.7f",result)); 
	}
}
