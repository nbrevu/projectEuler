package com.euler;

import java.math.BigInteger;

public class Euler326_3 {
	private final static int N=1000000;
	private final static int CYCLE_LENGTH=6*N;
	private final static int BIG_N=2*CYCLE_LENGTH;
	
	// OK. This is good. I can do what I wanted :D.
	public static void main(String[] args)	{
		long[] result=new long[BIG_N];
		long[] moddedResult=new long[BIG_N];
		result[0]=1;
		moddedResult[0]=1;
		BigInteger sum=BigInteger.ZERO;
		for (int i=1;i<BIG_N;++i)	{
			sum=sum.add(BigInteger.valueOf(result[i-1]*i));
			result[i]=sum.mod(BigInteger.valueOf(i+1)).intValueExact();
			moddedResult[i]=result[i]%N;
		}
		long cycleSum=0;
		for (int i=0;i<CYCLE_LENGTH;++i)	{
			cycleSum+=moddedResult[i];
			if (moddedResult[i]!=moddedResult[i+CYCLE_LENGTH]) throw new IllegalStateException("Not a real cycle :(.");
		}
		if ((cycleSum%N)!=0) throw new IllegalStateException("The big mod is "+(cycleSum%N)+", not 0. Bad news.");
	}
}
