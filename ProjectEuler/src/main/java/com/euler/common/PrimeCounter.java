package com.euler.common;

import java.math.RoundingMode;
import java.util.Arrays;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class PrimeCounter {
	// Traducido de http://acganesh.com/posts/2016-12-23-prime-counting.html.
	private final IntObjMap<LongLongMap> phiCache;
	private final LongLongMap piCache;
	private final long[] primes;
	private final long limit;
	public PrimeCounter(long limit)	{
		phiCache=HashIntObjMaps.newMutableMap();
		piCache=HashLongLongMaps.newMutableMap();
		primes=Primes.listLongPrimes(limit).stream().mapToLong(Long::longValue).toArray();
		this.limit=limit;
	}
	
	private long phi(long x,int a)	{
		LongLongMap cache1=phiCache.computeIfAbsent(a,(int unused)->HashLongLongMaps.newMutableMap());
		return cache1.computeIfAbsent(x,(long xx)->{
			if (a==1) return (xx+1)/2;
			else return phi(xx,a-1)-phi(xx/primes[a-1],a-1);
		});
	}
	
	private long piDirect(long x)	{
		int position=Arrays.binarySearch(primes, x);
		return (position>=0)?(1+position):(-1-position); 
	}
	
	public long pi(long x)	{
		return piCache.computeIfAbsent(x,(long xx)->{
			if (xx<=limit) return piDirect(xx);
			int a=(int)pi((long)Math.floor(Math.pow(xx,1d/4d)));
			int b=(int)pi((long)Math.floor(Math.pow(xx,1d/2d)));
			int c=(int)pi((long)Math.floor(Math.pow(xx,1d/3d)));
			long result=phi(xx,a)+(((long)(b+a-2)*(long)(b-a+1))/2);
			for (int i=a+1;i<=b;++i)	{
				long w=xx/primes[i-1];
				long bi=pi(LongMath.sqrt(w,RoundingMode.DOWN));
				result-=pi(w);
				if (i<=c) for (int j=i;j<=(int)bi;++j) result=result-pi(w/primes[j-1])+j-1;
			}
			return result;
		});
	}
	
	public static void main(String[] args)	{
		long limit=LongMath.pow(10l,12);
		long tic=System.nanoTime();
		PrimeCounter pc=new PrimeCounter(1+LongMath.sqrt(limit,RoundingMode.UP));
		System.out.println(pc.pi(limit));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
