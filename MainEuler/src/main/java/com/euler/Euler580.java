package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;

public class Euler580 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	private static int getModifiedMöbiusCoefficient(DivisorHolder decomposition)	{
		int result=1;
		for (LongIntCursor cursor=decomposition.getFactorMap().cursor();cursor.moveNext();)	{
			int exp=cursor.value();
			long m=(cursor.key()%4);
			if (((m==1)&&(exp==1))||((m==3)&&(exp==2))) result=-result;
			else return 0;
		}
		return result;
	}
	
	/*
	 * OK, I see the problem. Things don't work properly regarding numbers like "21^2".
	 * Yet again, inclusion-exclusion needs to be handled carefully. Maybe it won't be as destructive as with #768...
	 */
	public static void main(String[] args)	{
		long s=LongMath.sqrt(LIMIT/4,RoundingMode.DOWN);
		long[] lastPrimes=Primes.lastPrimeSieve(s);
		long result=0l;
		for (long i=1;i<s;i+=4)	{
			int möbius=getModifiedMöbiusCoefficient(DivisorHolder.getFromFirstPrimes(i,lastPrimes));
			if (möbius==0) continue;
			long i2=i*i;
			long howMany=LIMIT/(4*i2);
			long rem=LIMIT%(4*i2);
			if (rem>=i2) ++howMany;
			result+=howMany*möbius;
		}
		// Ooooh, not correct.
		System.out.println(result);
	}
}
