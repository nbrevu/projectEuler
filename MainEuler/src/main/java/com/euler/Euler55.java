package com.euler;

import java.math.BigInteger;

public class Euler55 {
	private final static int LIMIT=10000;
	private final static int MAX_ITERATIONS=50;
	
	private static String reverse(final String in)	{
		return new StringBuilder(in).reverse().toString();
	}
	
	private static BigInteger iterate(final BigInteger in)	{
		String inStr=in.toString();
		String newStr=reverse(inStr);
		BigInteger n2=new BigInteger(newStr);
		return in.add(n2);
	}
	
	private static boolean isPalindrome(final BigInteger in)	{
		String inStr=in.toString();
		String newStr=reverse(inStr);
		return inStr.equals(newStr);
	}
	
	private static boolean isLychrel(int in)	{
		BigInteger num=BigInteger.valueOf(in);
		for (int i=1;i<MAX_ITERATIONS;++i)	{
			num=iterate(num);
			if (isPalindrome(num)) return false;
		}
		return true;
	}
	public static void main(String[] args)	{
		int counter=0;
		for (int i=1;i<LIMIT;++i) if (isLychrel(i)) ++counter;
		System.out.println(counter);
	}
}
