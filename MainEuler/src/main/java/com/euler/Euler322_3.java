package com.euler;

import com.google.common.math.LongMath;

public class Euler322_3 {
	private final static long UPPER=LongMath.pow(10l,18);
	private final static long LOWER=LongMath.pow(10l,12)-10;
	
	private static int[] translateToBase(long number,int base)	{
		int digits=(int)(1+Math.floor(Math.log(number)/Math.log(base)));
		int[] result=new int[digits];
		for (int i=0;i<digits;++i)	{
			result[i]=(int)(number%base);
			number/=base;
		}
		return result;
	}
	
	private static long countNonDivisibleNumbers(int[] upper,int[] lower,int base)	{
		return countNonDivisibleRecursive(upper,lower,base,upper.length-1);
	}
	
	// This is the method that counts "close to the limit".
	private static long countNonDivisibleRecursive(int[] upper,int[] lower,int base,int currentIndex)	{
		// I have some idea about how to do this, but I'm not 100% sure...
		int highDigit=upper[currentIndex];
		int lowDigit=(currentIndex>=lower.length)?0:lower[currentIndex];
		if (highDigit<lowDigit) return 0l;
		long result=countNonDivisibleRecursive(upper,lower,base,currentIndex-1);
		if (highDigit>lowDigit) result+=(highDigit-lowDigit)*countNonDivisibleRecursive(lower,base,currentIndex-1);
		return result;
	}
	
	// This is the version that counts "inside".
	private static long countNonDivisibleRecursive(int[] lower,int base,int currentIndex)	{
		int digit=(currentIndex>=lower.length)?0:lower[currentIndex];
		long result=base-digit;
		if (currentIndex>0) result*=countNonDivisibleRecursive(lower,base,currentIndex-1);
		return result;
	}
	
	private static long countNonDivisibleNumbers(long upper,long lower,int base)	{
		int[] upperDigits=translateToBase(upper,base);
		int[] lowerDigits=translateToBase(lower,base);
		return countNonDivisibleNumbers(upperDigits,lowerDigits,base);
	}
	
	private static long countDivisible(long upper,long lower,int base1,int base2)	{
		long total=upper-lower;
		long nonDivisible1=countNonDivisibleNumbers(upper,lower,base1);
		long nonDivisible2=countNonDivisibleNumbers(upper,lower,base2);
		System.out.println("nonDivisible1="+nonDivisible1+".");
		System.out.println("nonDivisible2="+nonDivisible2+".");
		// Yeah, the "common" numbers are missing. That's the complicated part here.
		return total-nonDivisible1-nonDivisible2;
	}
	
	public static void main(String[] args)	{
		System.out.println(countDivisible(UPPER,LOWER,2,5));
	}
}
