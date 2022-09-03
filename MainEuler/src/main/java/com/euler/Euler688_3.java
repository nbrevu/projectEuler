package com.euler;

import java.math.BigInteger;

import com.google.common.math.LongMath;

public class Euler688_3 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static BigInteger triangularMinus(BigInteger in)	{
		return in.multiply(in.subtract(BigInteger.ONE)).divide(BigInteger.TWO);
	}
	
	public static void main(String[] args)	{
		BigInteger bigLimit=BigInteger.valueOf(LIMIT);
		BigInteger result=BigInteger.ZERO;
		BigInteger maxK=BigInteger.valueOf((long)Math.ceil(0.5*Math.sqrt(8*LIMIT+1)-1));
		for (BigInteger k=BigInteger.ONE;k.compareTo(maxK)<=0;k=k.add(BigInteger.ONE))	{
			BigInteger tK=triangularMinus(k);
			BigInteger lK=bigLimit.subtract(tK).divide(k);
			BigInteger residue=bigLimit.add(BigInteger.ONE).subtract(tK).subtract(k.multiply(lK));
			result=result.add(k.multiply(triangularMinus(lK)).add(lK.multiply(residue)));
		}
		System.out.println(result);
		System.out.println(result.mod(BigInteger.valueOf(MOD)));
		/*
		929723499956693737817604096682206
		110941813
		 */
	}
}
