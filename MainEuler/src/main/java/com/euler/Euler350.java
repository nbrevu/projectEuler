package com.euler;

import java.util.function.LongUnaryOperator;

import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler350 {
	private final static int G=IntMath.pow(10,6);
	private final static long L=LongMath.pow(10l,12);
	private final static long N=LongMath.pow(10l,18);
	
	private final static long MOD=LongMath.pow(101l,4);
	
	// Apparently this is wrong, which is understandable.
	public static void main(String[] args)	{
		LongLongMap expCache=HashLongLongMaps.newMutableMap();
		long[] lastPrimes=Primes.lastPrimeSieve((long)G);
		long[] divisors=new long[1+G];
		long[] gcdSets=new long[1+G];
		gcdSets[1]=1l;
		for (int i=2;i<=G;++i)	{
			DivisorHolder decomp=DivisorHolder.getFromFirstPrimes(i,lastPrimes);
			long divs=decomp.getAmountOfDivisors();
			long n=expCache.computeIfAbsent(divs,(LongUnaryOperator)((long x)->EulerUtils.expMod(x,N,MOD)));
			for (LongCursor cursor=decomp.getUnsortedListOfDivisors().cursor();cursor.moveNext();)	{
				int div=(int)cursor.elem();
				if ((div!=1)&&(div!=i)) n+=MOD-(divisors[i/div]*gcdSets[div])%MOD;
			}
			gcdSets[i]=n%MOD;
		}
		long result=0l;	// Something with L, maybe actually another binary exp? Or it's (probably) a big number which happens to be a multiple of MOD?
		for (int i=1;i<=G;++i)	{
			long occurrences=(L/i-G+1)%MOD;
			result+=occurrences*gcdSets[i];
			result%=MOD;
		}
		System.out.println(result);
	}
}
