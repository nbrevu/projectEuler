package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler605 {
	// This is wrong because I confuse N and N-1 regarding the exponential properties.
	private final static long N=3;//LongMath.pow(10l,8)+7;
	private final static long K=1;//LongMath.pow(10l,4)+7;
	private final static long MOD=LongMath.pow(10l,8);
	
	private static long getQ(long n,long mod)	{
		/*
		 * Calculate (2^(N-1)/N)%mod. It's calculated as [(2^N-1)%(N*mod)]/N.
		 */
		BigInteger bigMod=BigInteger.valueOf(n*mod);
		BigInteger two=BigInteger.valueOf(2l);
		long num=EulerUtils.expMod(two,n-1,bigMod).longValue()-1;
		if ((num%n)!=0) throw new RuntimeException();
		return num/n;
	}
	
	private static long getNumerator(long k,long n,long q,long mod)	{
		long fact1=(q*(k-1)+1)%mod;
		long fact2=EulerUtils.expMod(2l,n-k,mod);
		return (fact1*fact2)%mod;
	}
	
	private static long getDenominator(long q,long n,long mod)	{
		long power=EulerUtils.expMod(2l,n-1,mod)-1;
		return (q*power)%mod;
	}
	
	public static void main(String[] args)	{
		long q=getQ(N,MOD);
		long num=getNumerator(K,N,q,MOD);
		long den=getDenominator(q,N,MOD);
		long res=(num*den)%MOD;
		System.out.println(res);
	}
}
