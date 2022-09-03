package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;

import com.euler.common.BernoulliNumberModCache;
import com.euler.common.EulerUtils;
import com.euler.common.PowerfulNumberGenerator;
import com.euler.common.PowerfulNumberGenerator.MultiplicativeFunctionScheme;
import com.euler.common.PowerfulNumberGenerator.PowerfulNumber;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class Euler639_2 {
	private final static long MOD=1_000_000_007l;
	private final static long LIMIT=LongMath.pow(10l,12);
	private final static int MAX_POWER=50;
	
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
		private MultiplicativeFunctionScheme getMultiplicativeFunctionScheme(int k)	{
			LongUnaryOperator sumG=(long x)->faulhaberCalculator.getFaulhaberSum(k,x);
			HCache cache=new HCache(k,mod);
			ToLongFunction<PowerfulNumber> h=(PowerfulNumber num)->	{
				long result=1l;
				for (LongCursor cursor=num.primes.keySet().cursor();cursor.moveNext();)	{
					result*=cache.get(cursor.elem());
					result%=mod;
				}
				return result;
			};
			return new MultiplicativeFunctionScheme(sumG,h);
		}
		public long getSum(int maxK)	{
			List<MultiplicativeFunctionScheme> schemes=new ArrayList<>(maxK);
			for (int i=1;i<=maxK;++i) schemes.add(getMultiplicativeFunctionScheme(i));
			return generator.sumMultiplicativeFunctions(schemes,limit,mod);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		MultiplicativeAdder calculator=new MultiplicativeAdder(LIMIT,MOD);
		long result=calculator.getSum(MAX_POWER);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
