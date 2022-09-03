package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler688_4 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long MOD=LongMath.pow(10l,9)+7;
	
	private static long triangularMinus(long in)	{
		return (in*(in-1))/2;
	}
	
	private static long triangularMinus(long in,long mod)	{
		long f1=in%mod;
		long f2=(in-1)%mod;	// Not f1-1, this doesn't do what we want if in is a multiple of mod.
		long div2=(mod+1)/2l;
		long p1=(f1*f2)%mod;
		return (p1*div2)%mod;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		long maxK=(long)Math.ceil(0.5*Math.sqrt(8*LIMIT+1)-1);
		for (long k=1;k<=maxK;++k)	{
			long tK=triangularMinus(k);
			long lK=LongMath.divide(LIMIT-tK,k,RoundingMode.DOWN);
			long residue=LIMIT+1-tK-k*lK;
			result+=(k*triangularMinus(lK,MOD))%MOD+(lK*residue)%MOD;
			result%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
