package com.euler;

import java.math.RoundingMode;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler745 {
	private final static long LIMIT=LongMath.pow(10l,14);
	
	private final static long MOD=1_000_000_007l;
	
	public static void main(String[] args)	{
		double tic=System.nanoTime();
		int sq=(int)LongMath.sqrt(LIMIT,RoundingMode.DOWN);
		int[] firstPrimes=Primes.firstPrimeSieve(sq);
		long[] subtracted=new long[1+(int)sq];
		long result=0l;
		for (int i=sq;i>=1;--i)	{
			long i2=i*(long)i;
			long actualCount=LIMIT/i2;
			actualCount-=subtracted[i];
			if (i>1) for (LongCursor cursor=DivisorHolder.getFromFirstPrimes(i,firstPrimes).getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				int divisor=(int)cursor.elem();
				if (divisor!=i) subtracted[divisor]+=actualCount;
			}
			result+=i2*actualCount;
			result%=MOD;
		}
		double tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
