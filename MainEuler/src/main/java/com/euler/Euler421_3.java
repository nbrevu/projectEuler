package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler421_3 {
	private final static long PRIME_LIMIT=LongMath.pow(10l,8);
	private final static long NUMBER_LIMIT=LongMath.pow(10l,11);
	
	private static class UnitRootFinder	{
		private final long p;
		private final int q;
		private final long baseExp;
		private final long[] placeholder;
		public UnitRootFinder(long p,int q)	{
			// p-1 must be a multiple of q (not checked here).
			this.p=p;
			this.q=q;
			baseExp=(p-1)/q;
			placeholder=new long[q];
		}
		public boolean trySeed(long s)	{
			placeholder[0]=EulerUtils.expMod(s,baseExp,p);
			for (int i=1;i<q;++i)	{
				if (placeholder[i-1]==1l) return false;
				placeholder[i]=(placeholder[i-1]*placeholder[0])%p;
			}
			return placeholder[q-1]==1l;
		}
		public long[] getRoots(long otherRoot)	{
			for (int i=0;i<q;++i) placeholder[i]=(placeholder[i]*otherRoot)%p;
			return placeholder;
		}
	}
	
	private static LongSet calculatePMinusOneRoots(long p,int q)	{
		UnitRootFinder finder=new UnitRootFinder(p,q);
		for (long n=2;n<p;++n) if (finder.trySeed(n)) return HashLongSets.newImmutableSet(finder.getRoots(p-1));
		throw new ArithmeticException("Can't find roots.");
	}
	
	private static LongSet singleton(long in)	{
		LongSet result=HashLongSets.newMutableSet();
		result.add(in);
		return result;
	}
	
	private static LongSet getBaseRoots(long p)	{
		if (p<=5) return singleton(p-1);
		boolean fizz=(p%3)==1;
		boolean buzz=(p%5)==1;
		if (fizz&buzz) return calculatePMinusOneRoots(p,15);
		else if (fizz) return calculatePMinusOneRoots(p,3);
		else if (buzz) return calculatePMinusOneRoots(p,5);
		else return singleton(p-1);
	}
	
	private static long sumAppearances(long initial,long prime,long maximum)	{
		long gapCount=(maximum-initial)/prime;	// Not exact, but we want the floor.
		return prime*(1+gapCount);	// Warding off by one errors off, then multiplying times the prime itself.
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		for (long p:Primes.listLongPrimes(PRIME_LIMIT)) for (LongCursor cursor=getBaseRoots(p).cursor();cursor.moveNext();) result+=sumAppearances(cursor.elem(),p,NUMBER_LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
