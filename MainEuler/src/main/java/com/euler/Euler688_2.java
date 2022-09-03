package com.euler;

import java.math.RoundingMode;

import com.google.common.math.LongMath;

public class Euler688_2 {
	private final static long LIMIT=LongMath.pow(10l,2);
	
	private static long triangularMinus(long in)	{
		return (in*(in-1))/2;
	}
	
	public static void main(String[] args)	{
		long result=0;
		long maxK=(long)Math.ceil(0.5*Math.sqrt(8*LIMIT+1)-1);
		for (long k=1;k<=maxK;++k)	{
			long tK=triangularMinus(k);
			long lK=LongMath.divide(LIMIT-tK,k,RoundingMode.DOWN);
			long residue=LIMIT+1-tK-k*lK;
			result+=k*triangularMinus(lK)+lK*residue;
		}
		System.out.println(result);
	}
}
