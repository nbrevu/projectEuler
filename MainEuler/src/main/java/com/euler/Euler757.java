package com.euler;

import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler757 {
	public static void main(String[] args)	{
		int n=1_000_000;
		int[] lastPrimes=Primes.firstPrimeSieve(n);
		int counter=0;
		for (int i=2;i<=n;++i)	{
			DivisorHolder holder=DivisorHolder.getFromFirstPrimes(i,lastPrimes);
			long[] factors=holder.getUnsortedListOfDivisors().toLongArray();
			Arrays.sort(factors);
			LongSet sums=HashLongSets.newMutableSet();
			for (int j=0;j<factors.length;++j)	{
				long f1=factors[j];
				long f2=factors[factors.length-1-j];
				if (f1>f2) break;
				sums.add(f1+f2);
			}
			boolean isStealth=false;
			for (LongCursor cursor=sums.cursor();cursor.moveNext();) if (sums.contains(cursor.elem()-1))	{
				isStealth=true;
				break;
			}
			if (isStealth)	{
				++counter;
				System.out.println("Found "+i+".");
			}
		}
		System.out.println("Total: "+counter+".");
	}
}
