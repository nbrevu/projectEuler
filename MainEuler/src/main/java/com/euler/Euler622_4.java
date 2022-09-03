package com.euler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler622_4 {
	// https://en.wikipedia.org/wiki/Faro_shuffle
	private static class CycleIterator	{
		private final long in;
		public CycleIterator(long in)	{
			this.in=in;
		}
		public boolean doesCycleEqual(int candidate)	{
			long halfCycle=in/2;
			long iterating=2;
			int counter=0;
			for (;;)	{
				iterating=iterate(iterating,halfCycle);
				++counter;
				if (iterating==2) return counter==candidate;
				else if (counter>candidate) return false;
			}
		}
		private long iterate(long in,long halfCycle)	{
			if (in<=halfCycle) return 2l*in-1l;
			else return 2l*(in-halfCycle);
		}
	}
	
	private static Map<Long,Integer> factor(long in,List<Long> primes)	{
		Map<Long,Integer> factors=new HashMap<>();
		for (Long p:primes)	{
			long pp=p.longValue();
			if (pp*pp>in) break;
			else while ((in%pp)==0)	{
				EulerUtils.increaseCounter(factors,pp);
				in/=pp;
			}
		}
		if (in>1) EulerUtils.increaseCounter(factors,in);
		return factors;
	}
	
	private static void combine(Map<Long,Integer> result,Map<Long,Integer> toAdd)	{
		for (Map.Entry<Long,Integer> entry:toAdd.entrySet()) EulerUtils.increaseCounter(result,entry.getKey(),entry.getValue());
	}
	
	public static long[] getAllDivisors(Map<Long,Integer> factors)	{
		int howMany=countDivisors(factors);
		long[] result=new long[howMany];
		result[0]=1;
		int writingIndex=1;
		for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
			int whereToStop=writingIndex;
			long[] powers=getPowers(entry.getKey(),entry.getValue());
			for (int i=0;i<whereToStop;++i)	{
				long num=result[i];
				for (long times:powers)	{
					result[writingIndex]=num*times;
					++writingIndex;
				}
			}
		}
		return result;
	}
	private static int countDivisors(Map<?,Integer> in)	{
		int result=1;
		for (int exponent:in.values()) result*=1+exponent;
		return result;
	}
	private static long[] getPowers(long base,int exponent)	{
		long[] result=new long[exponent];
		result[0]=base;
		for (int i=1;i<exponent;++i) result[i]=base*result[i-1];
		return result;
	}
	
	public static void main(String[] args)	{
		List<Long> primes=Primes.listLongPrimes(LongMath.pow(2l,15)+100);
		long n1=LongMath.pow(2l,30)-1;
		long n2=n1+2;	// n1*n2 = (2^30-1)(2^30+1)=2^60-1
		Map<Long,Integer> factors=factor(n1,primes);
		Map<Long,Integer> factors2=factor(n2,primes);
		combine(factors,factors2);
		long sum=0;
		for (long divisor:getAllDivisors(factors))	{
			long candidate=1+divisor;
			CycleIterator iterator=new CycleIterator(candidate);
			if (iterator.doesCycleEqual(60)) sum+=candidate;
		}
		System.out.println(sum);
	}
}
