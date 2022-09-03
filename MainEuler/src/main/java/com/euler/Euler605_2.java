package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.google.common.math.LongMath;

public class Euler605_2 {
	// This program works because n is prime.
	private final static long N=LongMath.pow(10l,8)+7;
	private final static long K=LongMath.pow(10l,4)+7;
	private final static long MOD=LongMath.pow(10l,8);
	
	private static Pair<BigInteger,BigInteger> getNumAndDen(long n,long k)	{
		BigInteger power=EulerUtils.exp(BigInteger.valueOf(2l),n).subtract(BigInteger.ONE);
		BigInteger factor1=power.multiply(BigInteger.valueOf(k-1)).add(BigInteger.valueOf(n));
		BigInteger pow2=EulerUtils.exp(BigInteger.valueOf(2l),n-k);
		BigInteger num=factor1.multiply(pow2);
		BigInteger den=power.multiply(power);
		return new Pair<>(num,den);
	}
	
	private static Pair<BigInteger,BigInteger> getCanonical(Pair<BigInteger,BigInteger> in)	{
		BigInteger gcd=EulerUtils.gcd(in.first,in.second);
		return new Pair<>(in.first.divide(gcd),in.second.divide(gcd));
	}
	
	public static void main(String[] args)	{
		Pair<BigInteger,BigInteger> pair=getCanonical(getNumAndDen(N,K));
		BigInteger result=pair.first.multiply(pair.second).mod(BigInteger.valueOf(MOD));
		System.out.println(result.longValue());
	}
}
