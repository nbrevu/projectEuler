package com.euler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.BigIntegerUtils.BigFactorialCache;

public class Euler239_3 {
	private final static int TOTAL=100;
	private final static int PRIMES=25;
	private final static int DERANGED=22;
	private final static int PRECISION=12;
	
	public static void main(String[] args)	{
		BigCombinatorialNumberCache combinatorials=new BigCombinatorialNumberCache(DERANGED);
		BigFactorialCache factorials=new BigFactorialCache(TOTAL);
		BigInteger numerator=BigInteger.ZERO;
		int fixed=PRIMES-DERANGED;
		int nonFixedTotal=TOTAL-fixed;
		for (int i=0;i<=DERANGED;++i)	{
			BigInteger res=combinatorials.get(DERANGED,i).multiply(factorials.get(nonFixedTotal-i));
			if ((i%2)==0) numerator=numerator.add(res);
			else numerator=numerator.subtract(res);
		}
		numerator=numerator.multiply(combinatorials.get(PRIMES,fixed));
		BigInteger denominator=factorials.get(TOTAL);
		MathContext context=new MathContext(PRECISION,RoundingMode.DOWN);
		BigDecimal result=new BigDecimal(numerator).divide(new BigDecimal(denominator),context);
		System.out.println(result);
	}
}
