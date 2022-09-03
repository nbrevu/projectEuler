package com.euler;

import com.euler.common.BigIntegerUtils.Fraction;
import com.google.common.math.LongMath;

public class Euler523_2 {
	private final static int SIZE=30;
	
	public static void main(String[] args)	{
		Fraction result=new Fraction();
		for (int i=2;i<=SIZE;++i)	{
			long num=LongMath.pow(2l,i-1)-1;
			Fraction augend=new Fraction(num,i);
			result=result.add(augend);
		}
		System.out.println(result.toString());
	}
}
