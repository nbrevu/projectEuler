package com.euler;

public class Euler637 {
	private final static int LIMIT=100;
	private final static int B1=3;
	private final static int B2=10;
	
	private static int sumOfDigits(int in,int base)	{
		int result=0;
		while (in>0)	{
			result+=in%base;
			in/=base;
		}
		return result;
	}
	
	private static int[] countSteps(int limit,int base)	{
		// Pensaba que esto iba a funcionar, pero no. Parece que hay que hilar más fino.
		int[] result=new int[1+limit];
		for (int i=0;i<base;++i) result[i]=0;
		for (int i=base;i<=limit;++i) result[i]=1+sumOfDigits(i,base);
		return result;
	}
	
	private static long getSumOfMatches(int limit,int base1,int base2)	{
		int[] a1=countSteps(limit,base1);
		int[] a2=countSteps(limit,base2);
		long result=0;
		for (int i=0;i<=limit;++i) if (a1[i]==a2[i]) result+=i;
		return result;
	}
	
	public static void main(String[] args)	{
		System.out.println(getSumOfMatches(LIMIT,B1,B2));
	}
}
