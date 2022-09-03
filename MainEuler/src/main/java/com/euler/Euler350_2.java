package com.euler;

import java.util.function.LongUnaryOperator;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler350_2 {
	private final static int G=IntMath.pow(10,6);
	private final static long L=LongMath.pow(10l,12);
	private final static long N=LongMath.pow(10l,18);
	
	private final static long MOD=LongMath.pow(101l,4);
	
	public static void main(String[] args)	{
		LongLongMap expCache=HashLongLongMaps.newMutableMap();
		long[] divisors=new long[1+G];
		long[] gcdSets=new long[1+G];
		for (int i=1;i<=G;++i) for (int j=1;(i*j)<=G;++j) ++divisors[i*j];
		for (int i=1;i<=G;++i)	{
			gcdSets[i]+=expCache.computeIfAbsent(divisors[i],(LongUnaryOperator)((long x)->EulerUtils.expMod(x,N,MOD)));
			gcdSets[i]%=MOD;
			for (int j=2;i*j<=G;++j)	{
				gcdSets[i*j]+=MOD-(gcdSets[i]*divisors[j]%MOD);
				gcdSets[i*j]%=MOD;
			}
		}
		long result=0l;
		for (int i=1;i<=G;++i)	{
			long occurrences=(L/i-G+1)%MOD;
			result+=occurrences*gcdSets[i];
			result%=MOD;
		}
		System.out.println(result);
	}
}
