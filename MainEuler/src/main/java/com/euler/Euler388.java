package com.euler;

import com.euler.common.MoebiusCalculator;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler388 {
	private final static int LIMIT=10;
	private final static int DIMENSIONS=3;
	
	public static void main(String[] args)	{
		int[] firstPrimeSieve=Primes.firstPrimeSieve(LIMIT);
		MoebiusCalculator calculator=new MoebiusCalculator(firstPrimeSieve);
		long result=0;
		for (int i=1;i<=LIMIT;++i)	{
			int moebius=calculator.getMoebiusFunction(i);
			if (moebius==0) continue;
			int multiples=1+(LIMIT/i);
			long addend=LongMath.pow(multiples,DIMENSIONS)-1;
			if (moebius==1) result+=addend;
			else result-=addend;
		}
		System.out.println(result);
	}
}
