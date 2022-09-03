package com.euler;

import java.util.function.LongConsumer;

import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler563 {
	private final static long LIMIT=9*LongMath.pow(10l,18);
	
	private static void addPrime(LongSet result,long prime,long limit)	{
		LongSet toAdd=HashLongSets.newMutableSet();
		long limitBefore=limit/prime;
		for (LongCursor cursor=result.cursor();cursor.moveNext();)	{
			long value=cursor.elem();
			while (value<=limitBefore)	{
				value*=prime;
				toAdd.add(value);
			}
		}
		toAdd.forEach((LongConsumer)result::add);
	}
	
	public static void main(String[] args)	{
		/*
		 * There are just 23538402 cases. VERY reasonable.
		 * However a lot of them will have MANY decompositions. Without pruning, this could take a lot of time.
		 */
		int[] firstPrimes=new int[] {2,3,5,7,11,13,17,19,23};
		LongSet smooth=HashLongSets.newMutableSet(new long[] {1});
		for (int prime:firstPrimes) addPrime(smooth,prime,LIMIT);
		System.out.println(smooth.size());
	}
}
