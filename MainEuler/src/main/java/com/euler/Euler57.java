package com.euler;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils;

public class Euler57 {
	private final static int LIMIT=1000;
	
	public static void main(String[] args)	{
		BigInteger num=BigInteger.valueOf(3);
		BigInteger den=BigInteger.valueOf(2);
		int counter=0;
		for (int i=2;i<=LIMIT;++i)	{
			BigInteger newDen=den.add(num);
			BigInteger newNum=den.add(newDen);
			if (BigIntegerUtils.numDigits(newNum)>BigIntegerUtils.numDigits(newDen)) ++counter;
			num=newNum;
			den=newDen;
		}
		System.out.println(counter);
	}
}
