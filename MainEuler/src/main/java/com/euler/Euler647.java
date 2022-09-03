package com.euler;

import com.google.common.math.LongMath;

public class Euler647 {
	private final static long LIMIT=LongMath.pow(10l,12);
	
	private static long square(long in)	{
		return in*in;
	}
	
	private static long sum(int k,long limit)	{
		long result=0;
		/*
		 * A = ((2k-4)n+1)^2
		 * B = ((k-4)^2*((k-2)*n^2+n))/2
		 */
		long coefA=2*k-4;
		long coefB1=square(k-4);
		long coefB2=k-2;
		for (int n=1;;++n)	{
			long A=square((coefA*n)+1);
			long B=coefB1*((coefB2)*square(n)+n)/2;
			if (A<=limit&&B<=limit) result+=A+B;
			else break;
		}
		return result;
	}
	
	private static long sumAll(long limit)	{
		long sum=0;
		for (int k=3;;k+=2)	{
			long thisSum=sum(k,limit);
			if (thisSum==0) break;
			sum+=thisSum;
		}
		return sum;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=sumAll(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
