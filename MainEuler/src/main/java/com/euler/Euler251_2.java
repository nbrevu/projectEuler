package com.euler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler251_2 {
	private final static int MAX_SUM=110000000;
	
	private static class Factors	{
		private final Map<Long,Integer> factors;
		public Factors(Map<Long,Integer> factors)	{
			this.factors=factors;
		}
		public long getNumber()	{
			long result=1;
			for (Map.Entry<Long,Integer> entry:factors.entrySet()) result*=LongMath.pow(entry.getKey(),entry.getValue());
			return result;
		}
		public Factors removeDuplicated()	{
			Map<Long,Integer> result=new HashMap<>();
			Set<Long> toRemove=new HashSet<>();
			for (Map.Entry<Long,Integer> entry:factors.entrySet())	{
				int currentExp=entry.getValue();
				if (currentExp>1)	{
					int duplicated=currentExp/2;
					int remainder=currentExp%2;
					result.put(entry.getKey(),duplicated);
					if (remainder==0) toRemove.add(entry.getKey());
					else entry.setValue(remainder);
				}
			}
			for (Long key:toRemove) factors.remove(key);
			return new Factors(result);
		}
		public void addFactors(Factors in)	{
			for (Map.Entry<Long,Integer> entry:in.factors.entrySet()) EulerUtils.increaseCounter(factors,entry.getKey(),entry.getValue());
		}
		public long[] getAllDivisors()	{
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
			// Arrays.sort(result);	// Sorting only makes sense if we are going to use finesse. Currently we aren't.
			return result;
		}
		private static int countDivisors(Map<?,Integer> in)	{
			int result=1;
			for (int exponent:in.values()) result*=1+exponent;
			return result;
		}
		private long[] getPowers(long base,int exponent)	{
			long[] result=new long[exponent];
			result[0]=base;
			for (int i=1;i<exponent;++i) result[i]=base*result[i-1];
			return result;
		}
	}
	
	private static class Factorer	{
		private final long[] firstPrimes;
		public Factorer(long maxFactor)	{
			firstPrimes=Primes.firstPrimeSieve(maxFactor);
		}
		public Factors factorNumber(long in)	{
			Map<Long,Integer> result=new HashMap<>();
			while (in>1)	{
				long prime=firstPrimes[(int)in];
				if (prime==0)	{
					EulerUtils.increaseCounter(result,in);
					break;
				}
				EulerUtils.increaseCounter(result,prime);
				in/=prime;
			}
			return new Factors(result);
		}
	}
	
	public static void main(String[] args)	{
		int counter=0;
		int maxK=MAX_SUM/6;
		int maxToFactor=8*maxK+5;
		Factorer factorer=new Factorer(maxToFactor);
		double maxLog=63*Math.log(2);
		for (int k=0;k<=maxK;++k)	{
			if ((k%1000000)==0) System.out.println(""+k+"...");
			int a=3*k+2;
			Factors duplicate=factorer.factorNumber(k+1);
			Factors single=factorer.factorNumber(8*k+5);
			Factors duplicatesInSecond=single.removeDuplicated();
			duplicate.addFactors(duplicatesInSecond);
			long singleNumber=single.getNumber();
			long doubleNumber=duplicate.getNumber();
			for (long b:duplicate.getAllDivisors())	{
				long q=doubleNumber/b;
				double log=2*Math.log(q)+Math.log(singleNumber);
				if (log>=maxLog) continue;
				long c=q*q*singleNumber;
				long sum=a+b+c;
				if (sum<=MAX_SUM) ++counter;
			}
		}
		System.out.println(counter);
	}
}
