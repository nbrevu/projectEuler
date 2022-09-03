package com.euler;

import java.math.BigInteger;

public class Euler479_2 {
	private final static int N=1000000;
	private final static long MOD=1000000007;
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger sum=BigInteger.ZERO;
		BigInteger biMod=BigInteger.valueOf(MOD);
		for (long k=2;k<=N;++k)	{
			BigInteger oneMinusK2=BigInteger.valueOf(1-k*k);
			BigInteger k2=BigInteger.valueOf(k*k);
			BigInteger num=oneMinusK2.subtract(oneMinusK2.pow(N+1));
			sum=sum.add(num.divide(k2)).remainder(biMod);
		}
		long tac=System.nanoTime();
		System.out.println(sum.toString());
		double seconds=(tac-tic)*1e-9;
		System.out.println("Seconds: "+seconds+".");
	}
}
