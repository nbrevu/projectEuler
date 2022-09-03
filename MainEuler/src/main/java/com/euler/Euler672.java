package com.euler;

import java.math.BigInteger;

import com.google.common.math.IntMath;

public class Euler672 {
	private static int calculateBruteForce(int n)	{
		int counter=0;
		while (n>1) if ((n%7)==0) n/=7;
		else	{
			++n;
			++counter;
		}
		return counter;
	}
	
	private static int calculateFinesse(int n)	{
		while ((n%7)==0) n/=7;	// Remove zeroes.
		if (n==1) return 0;	// Special case.
		int result=1;
		while (n>0)	{
			int digit=(n%7);
			n/=7;
			result+=6-digit;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		for (int i=1;i<=1000000;++i)	{
			int v1=calculateBruteForce(i);
			int v2=calculateFinesse(i);
			if (v1!=v2) throw new RuntimeException(String.format("Para n=%d el valor real es %d pero el algoritmo me da %d.",i,v1,v2));
		}
		BigInteger seven=BigInteger.valueOf(7);
		BigInteger eleven=BigInteger.valueOf(11);
		for (int i=1;i<=3;++i)	{
			int exponent=IntMath.pow(10,i);
			BigInteger num=seven.pow(exponent).subtract(BigInteger.ONE);
			BigInteger[] div=num.divideAndRemainder(eleven);
			if (div[1].signum()!=0) throw new RuntimeException("D:");
			System.out.println(div[0].toString(7));
		}
	}
}
