package com.euler;

import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;

import com.euler.common.BernoulliNumberModCache;
import com.euler.common.EulerUtils;
import com.euler.common.PowerfulNumberGenerator;
import com.euler.common.PowerfulNumberGenerator.PowerfulNumber;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler639 {
	private final static long MOD=1_000_000_007l;
	
	private static class HCache	{
		private final int pow;
		private final long mod;
		private final LongLongMap cache;
		public HCache(int pow,long mod)	{
			this.pow=pow;
			this.mod=mod;
			cache=HashLongLongMaps.newMutableMap();
		}
		public long get(long prime)	{
			return cache.computeIfAbsent(prime,(long p)->	{
				long n1=EulerUtils.expMod(prime,pow,mod);
				long n2=(n1*n1)%mod;
				return (mod+n1-n2)%mod;
			});
		}
	}
	
	private static class MultiplicativeAdder	{
		private final long limit;
		private final long mod;
		private final PowerfulNumberGenerator generator;
		private final BernoulliNumberModCache faulhaberCalculator;
		public MultiplicativeAdder(long limit,long mod)	{
			this.limit=limit;
			this.mod=mod;
			generator=new PowerfulNumberGenerator(limit);
			faulhaberCalculator=new BernoulliNumberModCache(mod);
		}
		public long getS(int k)	{
			HCache cache=new HCache(k,mod);
			LongUnaryOperator sumG=(long x)->faulhaberCalculator.getFaulhaberSum(k,x);
			ToLongFunction<PowerfulNumber> h=(PowerfulNumber num)->	{
				long result=1l;
				for (LongCursor cursor=num.primes.keySet().cursor();cursor.moveNext();)	{
					result*=cache.get(cursor.elem());
					result%=mod;
				}
				return result;
			};
			return generator.sumMultiplicativeFunction(sumG,h,limit,mod);
		}
	}
	
	/*
	 * It seems like, with f(p)=p^k, I can take g(x)=x^k and h(p)=p^k-p^2k.
	 * 
	 * So I would need:
	 * - A class with Faulhaber formulas (BernoulliNumberModCache seems to be exactly what I want here).
	 * - A cache for values of h for primes.
	 */
	public static void main(String[] args)	{
		// FUNCIONA. VOY A LLORAR.
		MultiplicativeAdder adder10=new MultiplicativeAdder(10,MOD);
		System.out.println(adder10.getS(1));
		MultiplicativeAdder adder100=new MultiplicativeAdder(100,MOD);
		System.out.println(adder100.getS(1));
		System.out.println(adder100.getS(2));
		MultiplicativeAdder adder10000=new MultiplicativeAdder(10000,MOD);
		System.out.println(adder10000.getS(1));
	}
}
