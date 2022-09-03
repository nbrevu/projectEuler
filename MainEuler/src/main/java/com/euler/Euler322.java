package com.euler;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler322 {
	private static class BinCoeffDivisibilityChecker	{
		private final IntObjMap<int[]> representations;
		public BinCoeffDivisibilityChecker(long fixedN,IntSet primes)	{
			representations=HashIntObjMaps.newMutableMap();
			primes.forEach((int prime)->representations.put(prime,translateToBase(fixedN,prime)));
		}
		public boolean isDivisible(long m)	{
			IntObjCursor<int[]> cursor=representations.cursor();
			while (cursor.moveNext())	{
				int[] repM=translateToBase(m,cursor.key());
				if (!isBinCoeffDivisibleByPrime(cursor.value(),repM)) return false;
			}
			return true;
		}
		private static int[] translateToBase(long number,int base)	{
			int digits=(int)(1+Math.floor(Math.log(number)/Math.log(base)));
			int[] result=new int[digits];
			for (int i=0;i<digits;++i)	{
				result[i]=(int)(number%base);
				number/=base;
			}
			if ((number!=0l)||(result[digits-1]==0)) throw new RuntimeException("D'oh!");
			return result;
		}
		private static boolean isBinCoeffDivisibleByPrime(int[] nRep,int[] mRep)	{
			// Lucas' Theorem FTW!
			for (int i=0;i<nRep.length;++i) if (nRep[i]>mRep[i]) return true;
			return false;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long n=LongMath.pow(10l,7)-10l;
		long m=LongMath.pow(10l,9);
		int count=0;
		BinCoeffDivisibilityChecker checker=new BinCoeffDivisibilityChecker(n,HashIntSets.newImmutableSetOf(2,5));
		for (long i=n;i<m;++i) if (checker.isDivisible(i)) ++count;
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(count+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
