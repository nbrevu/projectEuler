package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler704_2 {
	private final static long N=LongMath.pow(10l,16);
	
	private static long getFullChainSum(long n)	{
		return (2l<<n)*(n-2)+n+4;
	}
	
	private static long getPartialChainSum(long n,long howMany)	{
		if (howMany==0) return 0l;
		if (n==1)	{
			if (howMany==1) return 1l;
			else throw new IllegalArgumentException("Chain exceeded.");
		}
		long previousChain=howMany/2l;
		long interleaved=previousChain+(howMany%2l);
		return n*interleaved+getPartialChainSum(n-1,previousChain);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long maxChain=LongMath.log2(N,RoundingMode.DOWN);
		long base=getFullChainSum(maxChain-1);
		long inPartialSum=N+1-(1l<<maxChain);
		long result=base+getPartialChainSum(maxChain,inPartialSum);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
