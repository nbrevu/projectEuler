package com.euler;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;

public class Euler784_4 {
	private final static int LIMIT=2*IntMath.pow(10,6);
	
	// This is the same as the previous version, but "unfolding" the Alpertron code to adequate it to this problem.
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] firstPrimes=Primes.lastPrimeSieve(1+LIMIT);
		DivisorHolder[] decompositions=new DivisorHolder[2+LIMIT];
		decompositions[1]=new DivisorHolder();
		for (int i=2;i<=1+LIMIT;++i) decompositions[i]=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
		long sum=0;
		for (int p=2;p<=LIMIT;++p)	{
			DivisorHolder decomp=DivisorHolder.combine(decompositions[p-1],decompositions[p+1]);
			for (LongCursor cursor=decomp.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				long q=cursor.elem()-p;
				if (q>p) sum+=p+q;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
