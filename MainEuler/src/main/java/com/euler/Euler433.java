package com.euler;

import java.util.BitSet;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler433 {
	private final static int LIMIT=100000;
	
	private static int countSteps(int a,int b)	{
		int result=0;
		do	{
			int r=a%b;
			a=b;
			b=r;
			++result;
		}	while (b!=0);
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		/*
		 * 7599090523120 coprime pairs. This is not doable in a reasonable time.
		 * The case for N=100000 (result=93386546648) takes 2 minutes and a half. Times 2500, it's more than 4 days. Ooooh.
		 * 
		 * I still believe that the way to do this is counting the amount of pairs for which the amount of steps is N, but the formulas grow
		 * pretty quickly in complexity, and I need formulas for at least N=31.
		 * I'm assuming that the majority will have small values, but I still don't see a fast way to do it.
		 */
		int[] lastPrimes=Primes.lastPrimeSieve(LIMIT);
		// Additional steps to guarantee that a>b. Also includes the special cases a=b. 
		long result=((long)LIMIT*(LIMIT+1))/2;
		for (int a=2;a<=LIMIT;++a)	{
			long tmpResult=0;
			BitSet validNums=new BitSet(a);
			validNums.set(1,a);
			if (lastPrimes[a]!=0)	{
				LongSet primes=DivisorHolder.getFromFirstPrimes(a,lastPrimes).getFactorMap().keySet();
				for (LongCursor cursor=primes.cursor();cursor.moveNext();)	{
					int p=(int)cursor.elem();
					for (int j=p;j<a;j+=p) validNums.clear(j);
				}
			}
			long multiplier=LIMIT/a;
			for (int b=validNums.nextSetBit(0);b>=0;b=validNums.nextSetBit(b+1)) tmpResult+=countSteps(a,b);
			result+=2*multiplier*tmpResult;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
