package com.euler;

import java.math.RoundingMode;
import java.util.stream.LongStream;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler484_2 {
	// private final static long LIMIT=5*LongMath.pow(10l,15);
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private static class PrimeInfo	{
		public final long[] powers;
		public PrimeInfo(long prime)	{
			LongStream.Builder builder=LongStream.builder();
			long prod=prime;
			do	{
				prod*=prime;
				builder.accept(prod);
			}	while ((LIMIT/prod)>=prime);
			powers=builder.build().toArray();
		}
	}
	
	private static class PowerfulNumberCounter	{
		private final PrimeInfo[] data;
		public PowerfulNumberCounter(PrimeInfo[] data)	{
			this.data=data;
		}
		public long count(long limit)	{
			return countRecursive(0,limit);
		}
		private long countRecursive(int currentIndex,long currentLimit)	{
			long result=0;
			for (int i=currentIndex;i<data.length;++i)	{
				long[] powers=data[i].powers;
				if (powers[0]>currentLimit) break;
				for (int j=0;j<powers.length;++j)	{
					long p=powers[j];
					if (p>currentLimit) break;
					result+=1+countRecursive(1+i,currentLimit/p);
				}
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		// This is silly, but I want to count the powerful numbers. If the total is below 10^12 or so, this is probably bruteforceable.
		// "There are 153417852 powerful numbers below 5000000000000000." :DDDDD.
		PrimeInfo[] info=Primes.listLongPrimes(LongMath.sqrt(LIMIT,RoundingMode.DOWN)).stream().map(PrimeInfo::new).toArray(PrimeInfo[]::new);
		PowerfulNumberCounter counter=new PowerfulNumberCounter(info);
		long result=counter.count(LIMIT);
		System.out.println("There are "+result+" powerful numbers below "+LIMIT+".");
	}
}
