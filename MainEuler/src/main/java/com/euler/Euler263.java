package com.euler;

import java.util.BitSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler263 {
	private static boolean isPracticalAccordingToHypothesis(int n,DivisorHolder decomposition)	{
		/*
		LongIntMap factors=decomposition.getFactorMap();
		int pow2=factors.get(2l);
		if (pow2==0) return false;
		int maxAvailable=IntMath.pow(2,1+pow2);
		NavigableSet<Integer> factorClumps=new TreeSet<>();
		for (LongIntCursor cursor=factors.cursor();cursor.moveNext();) if (cursor.key()!=2l) factorClumps.add(IntMath.pow((int)cursor.key(),cursor.value()));
		while (!factorClumps.isEmpty())	{
			int factor=factorClumps.pollFirst();
			if (factor>=maxAvailable) return false;
			maxAvailable*=factor;
		}
		return true;
		*/
		// This didn't work, but wikipedia has helpfully provided me a better option (https://en.wikipedia.org/wiki/Practical_number#Characterization_of_practical_numbers).
		NavigableMap<Long,Integer> sortedPrimeMap=new TreeMap<>(decomposition.getFactorMap());
		long product=1l;
		for (Map.Entry<Long,Integer> entry:sortedPrimeMap.entrySet())	{
			long p=entry.getKey();
			if (p-1>product) return false;
			product*=(LongMath.pow(p,entry.getValue()+1)-1)/(p-1);
		}
		return true;
	}
	private static boolean isPractical(int n,DivisorHolder decomposition)	{
		LongSet divisors=decomposition.getUnsortedListOfDivisors();
		BitSet foundSums=new BitSet();
		foundSums.set(0);
		for (LongCursor cursor=divisors.cursor();cursor.moveNext();)	{
			int divisor=(int)cursor.elem();
			BitSet toAdd=new BitSet();
			for (int i=foundSums.nextSetBit(0);i>=0;i=foundSums.nextSetBit(i+1))	{
				int sum=i+divisor;
				if (sum>n) break;
				toAdd.set(sum);
			}
			foundSums.or(toAdd);
		}
		for (int i=1;i<=n;++i) if (!foundSums.get(i)) return false;
		return true;
	}
	
	public static void main(String[] args)	{
		int limit=IntMath.pow(10,5);
		int[] firstPrimes=Primes.firstPrimeSieve(limit);
		for (int i=2;i<=limit;++i)	{
			DivisorHolder decomposition=DivisorHolder.getFromFirstPrimes(i,firstPrimes);
			boolean case1=isPracticalAccordingToHypothesis(i,decomposition);
			boolean case2=isPractical(i,decomposition);
			if (case1&&!case2) System.out.println("El número "+i+" parece práctico, pero en realidad no lo es.");
			else if (case2&&!case1) System.out.println("El número "+i+" no parecía práctico, pero en realidad sí lo es.");
		}
	}
}
