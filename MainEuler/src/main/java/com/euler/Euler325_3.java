package com.euler;

public class Euler325_3 {
	private final static long SIZE=10000;
	
	private final static double PHI=(1.0+Math.sqrt(5.0))/2.0;
	
	private static long getWythoff(long in)	{
		return (long)Math.floor(in*PHI);
	}
	
	public static void main(String[] args)	{
		long sum=0;
		for (long i=1;i<=SIZE;++i)	{
			long lower=i+1;
			long higher=Math.min(SIZE,getWythoff(i));
			long amount=higher+1-lower;
			long sum1=i*amount;
			long sum2=((lower+higher)*amount)/2;
			sum+=sum1+sum2;
		}
		System.out.println(sum);
	}
}
