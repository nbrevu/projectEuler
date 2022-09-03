package com.euler;

import java.math.RoundingMode;
import java.util.Locale;

import com.google.common.math.LongMath;

public class Euler255_3 {
	private final static int NUM_DIGITS=14;
	
	private static class IterationCounter	{
		private final long initialValue;
		private final long finalValue;
		private final long x0;
		public IterationCounter(int numDigits)	{
			initialValue=LongMath.pow(10l,numDigits-1);
			finalValue=LongMath.pow(10l,numDigits)-1;
			if ((numDigits%2)==1) x0=2*LongMath.pow(10l,(numDigits-1)/2);
			else x0=7*LongMath.pow(10l,(numDigits-2)/2);
		}
		public long getCount()	{
			return finalValue+1-initialValue;	// Also 9*initialValue, but this should be faster.
		}
		public long sumIterations()	{
			return sumIterationsRecursive(initialValue,finalValue,x0);
		}
		private long sumIterationsRecursive(long intervalStart,long intervalEnd,long x)	{
			long result=intervalEnd+1-intervalStart;
			long nextX=LongMath.divide(x+LongMath.divide(intervalStart,x,RoundingMode.UP),2,RoundingMode.DOWN);
			long currentInitialValue=intervalStart;
			long currentLastValue=x*(2*nextX+1-x);
			boolean keepGoing=true;
			do	{
				if (currentLastValue>=intervalEnd)	{
					currentLastValue=intervalEnd;
					keepGoing=false;
				}
				if (x!=nextX) result+=sumIterationsRecursive(currentInitialValue,currentLastValue,nextX);
				++nextX;
				currentInitialValue=1+currentLastValue;
				currentLastValue+=2*x;
			}	while (keepGoing);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		IterationCounter counter=new IterationCounter(NUM_DIGITS);
		double num=counter.sumIterations();
		double den=counter.getCount();
		double result=num/den;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(String.format(Locale.UK,"%.10f.",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
