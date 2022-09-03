package com.euler.experiments;

import java.math.RoundingMode;

import com.euler.common.Primes;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Lucy {
	private static long sumPrimes(long upTo)	{
		long r=LongMath.sqrt(upTo,RoundingMode.DOWN);
		long lastValue=upTo/r;
		int arraySize=(int)(r+lastValue-1);
		long[] v=new long[arraySize];
		int index=0;
		for (long i=1;i<=r;++i)	{
			v[index]=upTo/i;
			++index;
		}
		long v1=v[index-1];
		do	{
			--v1;
			v[index]=v1;
			++index;
		}	while (v1>1);
		LongLongMap s=HashLongLongMaps.newMutableMap();
		for (long vi:v) s.put(vi,(vi*(vi+1))/2-1);
		for (long p=2;p<=r;++p) if (s.get(p)>s.get(p-1))	{
			long sp=s.get(p-1);
			long p2=p*p;
			for (long vi:v) if (vi<p2) break;
			else s.addValue(vi,p*(sp-s.get(vi/p)));
		}
		return s.get(upTo);
	}
	
	// WOW, it works. But it's much slower than Meissel-Lehmer. It also uses less memory!
	private static long countPrimes(long upTo)	{
		long r=LongMath.sqrt(upTo,RoundingMode.DOWN);
		long lastValue=upTo/r;
		int arraySize=(int)(r+lastValue-1);
		long[] v=new long[arraySize];
		int index=0;
		for (long i=1;i<=r;++i)	{
			v[index]=upTo/i;
			++index;
		}
		long v1=v[index-1];
		do	{
			--v1;
			v[index]=v1;
			++index;
		}	while (v1>1);
		LongLongMap s=HashLongLongMaps.newMutableMap();
		for (long vi:v) s.put(vi,vi-1);
		for (long p=2;p<=r;++p) if (s.get(p)>s.get(p-1))	{
			long sp=s.get(p-1);
			long p2=p*p;
			for (long vi:v) if (vi<p2) break;
			else s.addValue(vi,sp-s.get(vi/p));
		}
		return s.get(upTo);
	}

	public static void main(String[] args)	{
		/*-
		4
		25
		168
		1229
		9592
		78498
		664579
		5761455
		50847534
		455052511
		4118054813
		37607912018
		346065536839
		3204941750802
		Lucy's algorithm: 1206.768570525 seconds.
		4
		25
		168
		1229
		9592
		78498
		664579
		5761455
		50847534
		455052511
		4118054813
		37607912018
		346065536839
		3204941750802
		Meissel-Lehmer: 520.168591523 seconds.
		
		However, Lucy's algorithm is only O(sqrt(n)) in memory. Meissel-Lehmer chokes on 4G for 10^14.
		 */
		System.out.println(sumPrimes(10000000));
		{
			long tic=System.nanoTime();
			for (int i=1;i<=14;++i) System.out.println(countPrimes(LongMath.pow(10l,i)));
			long tac=System.nanoTime();
			double seconds=1e-9*(tac-tic);
			System.out.println("Lucy's algorithm: "+seconds+" seconds.");
		}
		{
			long tic=System.nanoTime();
			for (int i=1;i<=14;++i) System.out.println(Primes.countPrimes(LongMath.pow(10l,i)));
			long tac=System.nanoTime();
			double seconds=1e-9*(tac-tic);
			System.out.println("Meissel-Lehmer: "+seconds+" seconds.");
		}
	}
}
