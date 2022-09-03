package com.euler;

import java.util.function.LongConsumer;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler694 {
	// Too easy, but it's nice when I get it right at the very first attempt.
	private final static long LIMIT=LongMath.pow(10l,18);
	
	private static long cube(long in)	{
		return in*in*in;
	}
	
	private static boolean containsSquares(DivisorHolder divisors)	{
		for (IntCursor cursor=divisors.getFactorMap().values().cursor();cursor.moveNext();) if (cursor.elem()>=2) return true;
		return false;
	}
	
	private static LongSet getSmoothNumbers(LongSet primes,long limit)	{
		LongSet result=HashLongSets.newMutableSet();
		result.add(1l);
		for (LongCursor primesCursor=primes.cursor();primesCursor.moveNext();)	{
			long prime=primesCursor.elem();
			LongSet toAdd=HashLongSets.newMutableSet();
			for (LongCursor existingFactors=result.cursor();existingFactors.moveNext();)	{
				long newFactor=existingFactors.elem()*prime;
				while (newFactor<=limit)	{
					toAdd.add(newFactor);
					newFactor*=prime;
				}
			}
			toAdd.forEach((LongConsumer)result::add);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int limitRoot=(int)Math.floor(Math.pow(LIMIT,1d/3d));
		int[] firstPrimes=Primes.firstPrimeSieve(limitRoot);
		long result=0l;
		for (int i=1;i<=limitRoot;++i)	{
			DivisorHolder divisors=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			if (containsSquares(divisors)) continue;
			long thisLimit=LIMIT/cube(i);
			LongSet smoothNumbers=getSmoothNumbers(divisors.getFactorMap().keySet(),thisLimit);
			for (LongCursor cursor=smoothNumbers.cursor();cursor.moveNext();) result+=thisLimit/cursor.elem();
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
