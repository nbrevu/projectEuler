package com.euler;

import com.google.common.math.LongMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler322_2 {
	private final static long N=LongMath.pow(10l,7)-10l;
	private final static long MAX_M=LongMath.pow(10l,9);
	private final static IntSet PRIMES=HashIntSets.newImmutableSetOf(2,5);
	
	private static int[] translateToBase(long number,int base)	{
		int digits=(int)(1+Math.floor(Math.log(number)/Math.log(base)));
		int[] result=new int[digits];
		for (int i=0;i<digits;++i)	{
			result[i]=(int)(number%base);
			number/=base;
		}
		return result;
	}
	
	private static IntObjMap<int[]> getRepresentations(long number,IntSet bases)	{
		IntObjMap<int[]> result=HashIntObjMaps.newMutableMap();
		IntCursor cursor=bases.cursor();
		while (cursor.moveNext()) result.put(cursor.elem(),translateToBase(number,cursor.elem()));
		return result;
	}
	
	private static int chooseAdvantageousRepresentation(IntObjMap<int[]> representations)	{
		int result=-1;
		long bestValue=Long.MAX_VALUE;
		IntObjCursor<int[]> cursor=representations.cursor();
		while (cursor.moveNext())	{
			int prime=cursor.key();
			long value=1l;
			for (int p:cursor.value()) value*=prime-p;
			System.out.println("Para p="+prime+" hay "+value+" casos.");
			if (value<bestValue)	{
				result=prime;
				bestValue=value;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long cases=MAX_M-N;
		IntObjMap<int[]> representations=getRepresentations(MAX_M,PRIMES);
		chooseAdvantageousRepresentation(representations);
		{
			long val=LongMath.pow(10l,7)-10;
			System.out.println(val+":");
			IntObjMap<int[]> representations2=getRepresentations(val,PRIMES);
			chooseAdvantageousRepresentation(representations2);
		}
		{
			long val=LongMath.pow(10l,9);
			System.out.println(val+":");
			IntObjMap<int[]> representations2=getRepresentations(val,PRIMES);
			chooseAdvantageousRepresentation(representations2);
		}
		{
			long val=LongMath.pow(10l,12)-10;
			System.out.println(val+":");
			IntObjMap<int[]> representations2=getRepresentations(val,PRIMES);
			chooseAdvantageousRepresentation(representations2);
		}
		{
			long val=LongMath.pow(10l,18);
			System.out.println(val+":");
			IntObjMap<int[]> representations2=getRepresentations(val,PRIMES);
			chooseAdvantageousRepresentation(representations2);
		}
		// ZUTUN! No estoy seguro de cómo seguir. NACH PILTREN!!!!!
	}
}
