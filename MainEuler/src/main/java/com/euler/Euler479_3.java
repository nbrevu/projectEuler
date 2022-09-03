package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;

public class Euler479_3 {
	// I can't believe I spent so much time getting this right. It's super simple.
	private final static int N=1000000;
	private final static long MOD=1000000007;
	
	private static long getAugend(long k,long n,long mod,BigInteger bigMod)	{
		long k2=k*k;
		BigInteger bigK2=BigInteger.valueOf(k2);
		BigInteger base=BigInteger.valueOf(k2-1);
		BigInteger totalMod=bigMod.multiply(bigK2);
		BigInteger num=BigInteger.ONE.add(EulerUtils.expMod(base,n+1,totalMod));
		BigInteger result=num.divide(bigK2);
		return result.longValue()-1;	// We remove the case p=0.
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long sum=0;
		BigInteger bigMod=BigInteger.valueOf(MOD);
		for (long k=2;k<=N;++k)	{
			long augend=getAugend(k,N,MOD,bigMod);
			sum=(sum+augend)%MOD;
		}
		long tac=System.nanoTime();
		System.out.println(sum);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
