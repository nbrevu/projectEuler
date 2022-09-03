package com.euler.common;

import java.math.RoundingMode;
import java.util.Arrays;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class MeisselLehmerPrimeCounter {
	public static int CACHE_LIMIT=2_000_000;	// Feel free to change!
	// Traducido de http://acganesh.com/posts/2016-12-23-prime-counting.html.
	// Ahora hay que irse a: https://web.archive.org/web/20190419124442/http://acganesh.com/posts/2016-12-23-prime-counting.html.
	// O a https://acgan.sh/posts/2016-12-23-prime-counting.html.
	private final IntObjMap<LongLongMap> phiCache;
	private final LongLongMap piCache;
	private final long[] primes;
	private final long limit;
	public MeisselLehmerPrimeCounter(long limit)	{
		phiCache=HashIntObjMaps.newMutableMap();
		piCache=HashLongLongMaps.newMutableMap();
		primes=Primes.listLongPrimes(limit).stream().mapToLong(Long::longValue).toArray();
		this.limit=limit;
	}
	
	private long phi(long x,int a)	{
		if (a<1) return x;
		LongLongMap cache1=phiCache.computeIfAbsent(a,(int unused)->HashLongLongMaps.newMutableMap());
		return cache1.computeIfAbsent(x,(long xx)->{
			if (a==1) return (xx+1)/2;
			else return phi(xx,a-1)-phi(xx/primes[a-1],a-1);
		});
	}
	
	private long piDirect(long x)	{
		if (x<=1) return 0;
		int position=Arrays.binarySearch(primes,x);
		return (position>=0)?(1+position):(-1-position); 
	}
	
	private long sqrt(long x)	{
		return LongMath.sqrt(x,RoundingMode.DOWN);
	}
	
	private long cube(long x)	{
		return x*x*x;
	}
	
	private long cubeRoot(long x)	{
		long result=(long)Math.floor(Math.pow(x,1d/3d));
		// This is kind of horrible but I need it because floating point is a bitch. The above line returns 4 for an input of 125.
		while (cube(result)>x) --result;
		while (cube(result+1)<=x) ++result;
		return result;
	}
	
	private long quarticRoot(long x)	{
		return sqrt(sqrt(x));
	}
	
	public long piWithCacheClearing(long x)	{
		if (piCache.size()>=CACHE_LIMIT)	{
			/*
			int removed=phiCache.values().stream().mapToInt(LongLongMap::size).sum();
			System.out.println("Removing "+piCache.size()+" pi cache entries and "+removed+" phi cache entries...");
			*/
			System.out.println("Clearing cache!");
			piCache.clear();
			phiCache.values().forEach(LongLongMap::clear);
		}
		return pi(x);
	}
	
	public long pi(long x)	{
		long result=piCache.getOrDefault(x,-1l);
		if (result>=0) return result;
		if (x<=limit) return piDirect(x);
		/*
		int a=(int)pi((long)Math.floor(Math.pow(x,1d/4d)));
		int b=(int)pi((long)Math.floor(Math.pow(x,1d/2d)));
		int c=(int)pi((long)Math.floor(Math.pow(x,1d/3d)));
		*/
		int a=(int)pi(quarticRoot(x));
		int b=(int)pi(sqrt(x));
		int c=(int)pi(cubeRoot(x));
		result=phi(x,a)+(((long)(b+a-2)*(long)(b-a+1))/2);
		for (int i=a+1;i<=b;++i)	{
			long w=x/primes[i-1];
			long bi=pi(LongMath.sqrt(w,RoundingMode.DOWN));
			result-=pi(w);
			if (i<=c) for (int j=i;j<=(int)bi;++j) result=result-pi(w/primes[j-1])+j-1;
		}
		piCache.put(x,result);
		return result;
		
		
		/*
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
		*/
	}
	
	public long[] getPrimes()	{
		// Returns the primes up to the calculated limit. Useful to avoid calculating them twice in some problems.
		return primes;
	}
	
	public static void main(String[] args)	{
		long limit=LongMath.pow(10l,14);
		long tic=System.nanoTime();
		MeisselLehmerPrimeCounter pc=new MeisselLehmerPrimeCounter(1+LongMath.sqrt(limit,RoundingMode.UP));
		for (int i=1;i<=14;++i) System.out.println(pc.pi(LongMath.pow(10l,i)));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
