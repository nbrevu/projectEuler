package com.euler.common;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.AlternatePrimeCounter.AlternatePrimeCounts;
import com.google.common.math.IntMath;
import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public abstract class GenericLucyHedgehogSieve<T> {
	// Individual value of the function to sum.
	protected abstract T f(long in);
	// Sum of f from x=2 to <in>. DOES NOT INCLUDE 1!
	protected abstract T s(long in);
	// Operations needed: subtract and multiply.
	protected abstract T subtract(T min,T sub);
	protected abstract T multiply(T a,T b);
	// Compare the values of s(x-1) and s(x) to check whether x is prime.
	protected abstract boolean containsPrime(T sXMinusOne,T sX);
	
	private final List<Long> v;
	private final LongObjMap<T> s;
	
	protected GenericLucyHedgehogSieve(long upTo)	{
		long r=LongMath.sqrt(upTo,RoundingMode.DOWN);
		// This can also be replaced with an array.
		v=new ArrayList<>((int)(2*r));
		for (long i=1;i<=r;++i) v.add(upTo/i);
		long v_1=v.get(v.size()-1)-1;
		while (v_1>0)	{
			v.add(v_1);
			--v_1;
		}
		s=HashLongObjMaps.newMutableMap();
		for (long vi:v) s.put(vi,s(vi));
		for (long p=2;p<=r;++p)	{
			long q=p;	// Needed because of Java scoping rules.
			T sp=s.get(p-1);
			if (containsPrime(sp,s.get(p)))	{
				long p2=p*p;
				for (long vi:v) if (vi<p2) break;
				else s.compute(vi,(long unused,T currentValue)->	{
					T innerValue=subtract(s.get(vi/q),sp);
					T toSubtract=multiply(f(q),innerValue);
					return subtract(currentValue,toSubtract);
				});
			}
		}
	}
	
	public T sumF(long upTo)	{
		return s.get(upTo);
	}
	
	public static void main(String[] args)	{
		int pow=9;
		int n=IntMath.pow(10,pow);
		class PrimeInfoHolder	{
			public final long primeCount;
			public final long prime4k1Count;
			public final long prime4k3Count;
			public PrimeInfoHolder(long primeCount,long prime4k1Count,long prime4k3Count)	{
				this.primeCount=primeCount;
				this.prime4k1Count=prime4k1Count;
				this.prime4k3Count=prime4k3Count;
			}
		}
		PrimeInfoHolder fNonPrime=new PrimeInfoHolder(0,0,0);
		PrimeInfoHolder f2=new PrimeInfoHolder(1,0,0);
		PrimeInfoHolder f4k1=new PrimeInfoHolder(1,1,0);
		PrimeInfoHolder f4k3=new PrimeInfoHolder(1,0,1);
		GenericLucyHedgehogSieve<PrimeInfoHolder> sieve=new GenericLucyHedgehogSieve<>(n)	{
			@Override
			protected PrimeInfoHolder f(long in) {
				if (in==1) return fNonPrime;
				else if (in==2) return f2;
				switch ((int)(in%4))	{
					case 1:return f4k1;
					case 3:return f4k3;
					default:return fNonPrime;
				}
			}
			@Override
			protected PrimeInfoHolder s(long in) {
				long fullCount=in-1;
				long count4k1=(in-1)/4;
				long count4k3=(in+1)/4;
				return new PrimeInfoHolder(fullCount,count4k1,count4k3);
			}
			@Override
			protected PrimeInfoHolder subtract(PrimeInfoHolder min,PrimeInfoHolder sub) {
				return new PrimeInfoHolder(min.primeCount-sub.primeCount,min.prime4k1Count-sub.prime4k1Count,min.prime4k3Count-sub.prime4k3Count);
			}
			@Override
			protected PrimeInfoHolder multiply(PrimeInfoHolder a,PrimeInfoHolder b) {
				return new PrimeInfoHolder(a.primeCount*b.primeCount,a.prime4k1Count*b.prime4k1Count,a.prime4k3Count*b.prime4k3Count);
			}
			@Override
			protected boolean containsPrime(PrimeInfoHolder sXMinusOne,PrimeInfoHolder sX) {
				return (sX.primeCount>sXMinusOne.primeCount);
			}
		};
		AlternatePrimeCounter anotherSieve=new AlternatePrimeCounter(n);
		boolean[] composites=Primes.sieve(1+n);
		for (int i=1;i<=pow;++i)	{
			int x=IntMath.pow(10,i);
			int primes=0;
			int primes4k1=0;
			int primes4k3=0;
			for (int j=2;j<=x;++j) if (!composites[j])	{
				++primes;
				int m=j%4;
				if (m==1) ++primes4k1;
				else if (m==3) ++primes4k3;
			}
			PrimeInfoHolder calculated=sieve.sumF(x);
			AlternatePrimeCounts calculated2=anotherSieve.sumF(x);
			System.out.println(String.format("Values up to %d:",x));
			System.out.println(String.format("\tTotal prime count: calculated=%d, calculated (alternate)=%d, real=%d.",calculated.primeCount,calculated2.primeCount,primes));
			System.out.println(String.format("\t4k+1 prime count: calculated=%d, calculated (alternate)=%d, real=%d.",calculated.prime4k1Count,calculated2.getSum4k1(),primes4k1));
			System.out.println(String.format("\t4k+3 prime count: calculated=%d, calculated (alternate)=%d, real=%d.",calculated.prime4k3Count,calculated2.getSum4k3(),primes4k3));
		}
	}
}
