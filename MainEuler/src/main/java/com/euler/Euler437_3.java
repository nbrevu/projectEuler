package com.euler;

import java.util.stream.IntStream;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler437_3 {
	// I took far too long to find a solution to this, considering that this is actually a reasonably simple problem.
	private final static int LIMIT=IntMath.pow(10,8);
	
	private static boolean isPrimitiveRoot(long root,long prime,int[] firstPrimeSieve)	{
		// Explanation: https://math.stackexchange.com/a/133720.
		long pMinus=prime-1;
		DivisorHolder totientFactors=DivisorHolder.getFromFirstPrimes((int)pMinus,firstPrimeSieve);
		LongSet sigils=totientFactors.getFactorMap().keySet();
		for (LongCursor cursor=sigils.cursor();cursor.moveNext();) if (EulerUtils.expMod(root,pMinus/cursor.elem(),prime)==1) return false;
		return true;
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] firstPrimeSieve=Primes.firstPrimeSieve(LIMIT);
		int[] primesFrom7=IntStream.range(7,LIMIT).filter((int x)->firstPrimeSieve[x]==0).toArray();
		// We start from 7 because the sqrt(5) might be problematic. 5 has a "Fibonacci" primitive root: 3. 2 and 3 don't have primitive roots.
		long result=5;
		for (long p:primesFrom7)	{
			/*
			 * We need to get (1+sqrt(5))/2 modulo p. Theoretically I should also look for (1-sqrt(5))/2, but apparently this is enough!
			 * It's funny because this could have been a bug, but well, it works.
			 */
			long div2=(p+1)/2;	// Multiplying times this is equivalent to dividing by 2 (modulo p).
			for (LongCursor cursor=EulerUtils.squareRootModuloPrime(5,p).cursor();cursor.moveNext();)	{
				long candidate=((1+cursor.elem())*div2)%p;
				if (isPrimitiveRoot(candidate,p,firstPrimeSieve))	{
					result+=p;
					break;	// Stop checking candidates.
				}
			}
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
