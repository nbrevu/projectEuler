package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;

public class Euler515 {
	private final static int START=IntMath.pow(10,9);
	private final static int END=START+IntMath.pow(10,5);
	private final static long K=LongMath.pow(10l,5);
	
	public static void main(String[] args)	{
		boolean[] composites=Primes.sieve(END);
		long result=0;
		for (int i=START;i<=END;++i) if (!composites[i]) result+=EulerUtils.expMod(K-1,i-2,i);
		System.out.println(result);
	}
}
