package com.euler;

public class Euler1 {
	private final static long LIMIT=999;
	private final static long PRIME1=3;
	private final static long PRIME2=5;
	
	private static long triangular(long in)	{
		return ((in+1)*in)/2;
	}
	
	private static long multiplesOfAUpToB(long a,long b)	{
		long howMany=b/a;
		return a*triangular(howMany);
	}
	
	public static void main(String[] args)	{
		long result=multiplesOfAUpToB(PRIME1,LIMIT)+multiplesOfAUpToB(PRIME2,LIMIT)-multiplesOfAUpToB(PRIME1*PRIME2,LIMIT);
		System.out.println(result);
	}
}
