package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler580_3 {
	private final static long LIMIT=LongMath.pow(10l,7);
	
	private static int getModifiedMöbiusCoefficient(DivisorHolder decomposition,int[] oldMöbiusValues)	{
		int result=-1;
		for (LongCursor cursor=decomposition.getUnsortedListOfDivisors().cursor();cursor.moveNext();) result-=oldMöbiusValues[(int)(cursor.elem())];
		return result;
	}
	
	/*
	 * OK, I see the problem. Things don't work properly regarding numbers like "21^2".
	 * Yet again, inclusion-exclusion needs to be handled carefully. Maybe it won't be as destructive as with #768...
	 */
	public static void main(String[] args)	{
		long s=LongMath.sqrt(LIMIT/4,RoundingMode.DOWN);
		long[] lastPrimes=Primes.lastPrimeSieve(s);
		int[] möbiusCoefficients=new int[1+(int)s];
		long result=LIMIT/4;
		for (long i=5;i<s;i+=4)	{
			int möbius=getModifiedMöbiusCoefficient(DivisorHolder.getFromFirstPrimes(i,lastPrimes),möbiusCoefficients);
			möbiusCoefficients[(int)i]=möbius;
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
