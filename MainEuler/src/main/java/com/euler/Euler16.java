package com.euler;

import java.math.BigInteger;

public class Euler16 {
	public static void main(String[] args)	{
		BigInteger twentyfive=BigInteger.valueOf(1<<25);
		BigInteger fifty=twentyfive.multiply(twentyfive);
		BigInteger hundred=fifty.multiply(fifty);
		BigInteger hundredTwentyFive=hundred.multiply(twentyfive);
		BigInteger twoHundredFifty=hundredTwentyFive.multiply(hundredTwentyFive);
		BigInteger fiveHundred=twoHundredFifty.multiply(twoHundredFifty);
		BigInteger thousand=fiveHundred.multiply(fiveHundred);
		String toAdd=thousand.toString();
		int res=0;
		for (int i=0;i<toAdd.length();++i)	{
			res+=toAdd.charAt(i)-'0';
		}
		System.out.println(res);
	}
}
