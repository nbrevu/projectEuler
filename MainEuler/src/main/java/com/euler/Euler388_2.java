package com.euler;

import java.math.BigInteger;

import com.euler.common.MoebiusCalculator;
import com.google.common.math.LongMath;

public class Euler388_2 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private static BigInteger getCube(BigInteger in)	{
		BigInteger square=in.multiply(in);
		return square.multiply(in);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		for (long i=1;i<=LIMIT;++i)	{
			if ((i%10000000)==0) System.out.println(i);
			int moebius=MoebiusCalculator.getMoebiusFunction(i);
			if (moebius==0) continue;
			BigInteger multiples=BigInteger.valueOf(1+(LIMIT/i));
			BigInteger addend=getCube(multiples).subtract(BigInteger.ONE);
			result=(moebius==1)?result.add(addend):result.subtract(addend);
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
