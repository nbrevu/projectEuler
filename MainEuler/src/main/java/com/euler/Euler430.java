package com.euler;

import java.util.Locale;

import com.google.common.math.LongMath;

public class Euler430 {
	// This ended up being very fun and the level of challenge was just perfect, maybe a bit on the low side. Great!
	private final static long N=LongMath.pow(10l,10);
	private final static int M=4000;
	
	private static class ProbabilityCalculator	{
		private final long n;
		private final int m;
		private final double n2;
		public ProbabilityCalculator(int m,long n)	{
			this.n=n;
			this.m=m;
			this.n2=((double)n)*((double)n);
		}
		public double getProbability(long i)	{
			double p=(2*i*((double)n+1-i)-1)/n2;
			return 0.5+0.5*Math.pow(1-2*p,m);
		}
		public double getExpectedValue()	{
			double result=0.0;
			for (long i=1;i<=n/2;++i)	{
				double probI=getProbability(i);
				if (probI>0.5) result+=2*probI;
				else	{
					// All probabilities starting from here are 1.0.
					result+=n/2+1-i;
					break;
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ProbabilityCalculator calculator=new ProbabilityCalculator(M,N);
		String result=String.format(Locale.UK,"%.2f",calculator.getExpectedValue());
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
