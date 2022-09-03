package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;

public class Euler802 {
	private final static long MOD=1_020_340_567l;
	private final static int LIMIT=IntMath.pow(10,7);
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
		long[] values=new long[1+LIMIT];
		values[1]=2l;
		long result=2l;
		long exp=1l;
		for (int i=2;i<=LIMIT;++i)	{
			exp=(exp+exp)%MOD;
			long thisResult=exp;
			for (LongCursor cursor=DivisorHolder.getFromFirstPrimes(i,firstPrimes).getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				int div=(int)cursor.elem();
				if (div!=i) thisResult-=values[div];
			}
			thisResult%=MOD;
			if (thisResult<0) thisResult+=MOD;
			values[i]=thisResult;
			result+=thisResult;
		}
		result%=MOD;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
