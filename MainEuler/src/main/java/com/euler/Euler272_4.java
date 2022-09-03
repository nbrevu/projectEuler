package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;

public class Euler272_4 {
	private static int countCubicRoots(long in)	{
		int result=0;
		for (long i=1;i<in;++i) if (EulerUtils.expMod(i,3,in)==1l) ++result;
		return result;
	}
	
	public static void main(String[] args)	{
		for (long p:Primes.listLongPrimes(1000l))	{
			long currentNumber=p;
			int exp=1;
			do	{
				int cubicRoots=countCubicRoots(currentNumber);
				System.out.println(String.format("%d^%d=%d has %d cubic roots.",p,exp,currentNumber,cubicRoots));
				++exp;
				currentNumber*=p;
			}	while (currentNumber<100000000l);
		}
	}
}
