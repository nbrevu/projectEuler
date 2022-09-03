package com.euler;

import com.euler.common.EulerUtils;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler319_2 {
	private final static long MOD=1_000_000_000l;
	private final static long N=10_000_000_000l;
	
	private static class TCalculator	{
		private final long mod;
		private final long mod2;
		private final LongLongMap tCache;
		private final LongLongMap powCache;
		public TCalculator(long mod)	{
			this.mod=mod;
			mod2=2*mod;
			tCache=HashLongLongMaps.newMutableMap();
			powCache=HashLongLongMaps.newMutableMap();
			tCache.put(1l,1l);
		}
		private long getPow(long n)	{
			return powCache.computeIfAbsent(n,(long p)->	{
				long p3=EulerUtils.expMod(3l,p+1,mod2);
				long p2=EulerUtils.expMod(2l,p+1,mod);
				long result=(p3+1)/2-p2;
				if (result<0) result+=mod;
				return result;
			});
		}
		public long getT(long n)	{
			if (tCache.containsKey(n)) return tCache.get(n);
			long result=getPow(n);
			long div=2;
			long subN=n/div;
			while (subN>0)	{
				long count=n/subN-n/(subN+1);
				if (count>0)	{
					result-=count*getT(subN);
					result%=mod;
					if (result<0) result+=mod;
				}
				div=n/subN+1;
				subN=n/div;
			}
			tCache.put(n,result);
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		TCalculator calcu=new TCalculator(MOD);
		long result=calcu.getT(N);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
