package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;

public class Euler580_8 {
	private final static long LIMIT=LongMath.pow(10l,16);
	
	/*
	 * Maybe this code is not easy to understand without the results from the PowerType class, but this class hasn't been necessary in the end.
	 * The code for Euler580_7 uses it, but it's about thrice as slow! This class, on the other hand, avoids sorting and hashing of arrays.
	 */
	private static class ModifiedMöbiusCoefficientCalculator	{
		private final long[] lastPrimes;
		public ModifiedMöbiusCoefficientCalculator(long limit)	{
			lastPrimes=Primes.lastPrimeSieve(limit);
		}
		public int getMöbiusCoefficient(long num)	{
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(num,lastPrimes);
			int primes1=0;
			int primes3=0;
			boolean has2=false;
			for (LongIntCursor cursor=decomposition.getFactorMap().cursor();cursor.moveNext();)	{
				long m=(cursor.key())%4;
				int exp=cursor.value();
				if (m==1)	{
					if (exp>=2) return 0;
					else ++primes1;
				}	else	{
					if (exp>=3) return 0;
					if (exp==2)	{
						if (has2) return 0;
						else has2=true;
					}
					++primes3;
				}
			}
			/*
			 * If we got here, there are "primes1" primes of the form 4k+1, and "primes3" primes of the form 4k+3.
			 * Each prime appears only one, except if "has2" is true, in whose case one of the 4k+3 primes appears twice.
			 */
			int result=has2?1:(1-primes3);
			return (((primes1+primes3)%2)==0)?result:-result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long s=LongMath.sqrt(LIMIT,RoundingMode.DOWN);
		ModifiedMöbiusCoefficientCalculator calculator=new ModifiedMöbiusCoefficientCalculator(s);
		long result=LIMIT/4;
		if ((LIMIT%4)!=0) ++result;
		for (long i=3;i<s;i+=2)	{
			int möbius=calculator.getMöbiusCoefficient(i);
			if (möbius==0) continue;
			long i2=i*i;
			long howMany=LIMIT/(4*i2);
			long rem=LIMIT%(4*i2);
			if (rem>=i2) ++howMany;
			result+=howMany*möbius;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
