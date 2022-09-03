package com.euler.common;

import java.math.RoundingMode;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public abstract class SumOfTotientCalculator {
	// Slight variation from https://mathproblems123.wordpress.com/2018/05/10/sum-of-the-euler-totient-function/
	private final LongLongMap cache;
	protected SumOfTotientCalculator()	{
		cache=HashLongLongMaps.newMutableMap();
		cache.put(0l,0l);
		cache.put(1l,1l);
	}
	public final long getTotientSum(long in)	{
		return cache.computeIfAbsent(in,this::calculateTotientSum);
	}
	protected abstract long calculateTotientSum(long n);
	
	private static class CalculatorWithoutMod extends SumOfTotientCalculator	{
		@Override
		protected long calculateTotientSum(long n)	{
			long result=(n*(n+1))/2;
			long sn=LongMath.sqrt(n,RoundingMode.DOWN);
			for (long m=2;m<=sn;++m) result-=getTotientSum(n/m);
			long curLimInf=1+sn;
			long d=n/curLimInf;
			long curLimSup=n/d;
			for (;;)	{
				long howMany=1+curLimSup-curLimInf;
				result-=getTotientSum(d)*howMany;
				--d;
				if (d<=0) break;
				curLimInf=1+curLimSup;
				curLimSup=n/d;
			}
			return result;
		}
	}
	
	private static class CalculatorWithMod extends SumOfTotientCalculator	{
		private final long mod;
		protected CalculatorWithMod(long mod)	{
			super();
			this.mod=mod;
		}
		@Override
		protected long calculateTotientSum(long n)	{
			long a,b;
			if ((n%2)==0)	{
				a=(n/2)%mod;
				b=(n+1)%mod;
			}	else	{
				a=n%mod;
				b=((n+1)/2)%mod;
			}
			long result=(a*b)%mod;
			long sn=LongMath.sqrt(n,RoundingMode.DOWN);
			for (long m=2;m<=sn;++m)	{
				result+=mod-getTotientSum(n/m);
				result%=mod;
			}
			long curLimInf=1+sn;
			long d=n/curLimInf;
			long curLimSup=n/d;
			for (;;)	{
				long howMany=(1+curLimSup-curLimInf)%mod;
				long prod=(getTotientSum(d)*howMany)%mod;
				result+=mod-prod;
				result%=mod;
				--d;
				if (d<=0) break;
				curLimInf=1+curLimSup;
				curLimSup=n/d;
			}
			return result;
		}
	}
	
	public static SumOfTotientCalculator getWithoutMod()	{
		return new CalculatorWithoutMod();
	}
	
	public static SumOfTotientCalculator getWithMod(long mod)	{
		return new CalculatorWithMod(mod);
	}
}
