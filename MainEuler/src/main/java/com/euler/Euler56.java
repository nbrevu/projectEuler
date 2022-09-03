package com.euler;

import java.math.BigInteger;

import com.euler.common.BigIntegerUtils;

public class Euler56 {
	private final static int LIMIT_A=100;
	private final static int LIMIT_B=100;
	
	public static void main(String[] args)	{
		int max=0;
		for (int a=1;a<LIMIT_A;++a) for (int b=1;b<LIMIT_B;++b)	{
			BigInteger power=BigIntegerUtils.pow(a,b);
			int sum=BigIntegerUtils.sumDigits(power);
			if (sum>max) max=sum;
		}
		System.out.println(max);
	}
}
