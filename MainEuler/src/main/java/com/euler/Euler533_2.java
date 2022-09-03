package com.euler;

import java.util.List;

import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler533_2 {
	private final static long LIMIT=20000000;
	private final static long MOD=LongMath.pow(10l,9);
	
	private static class BigNumberSummary	{
		private final long mod;
		private final double log;
		public BigNumberSummary()	 {
			mod=1l;
			log=0;
		}
		public BigNumberSummary(long n)	{
			mod=n;
			log=Math.log(n);
		}
		public BigNumberSummary(BigNumberSummary n1,BigNumberSummary n2,long limit)	{
			mod=(n1.mod*n2.mod)%limit;
			log=n1.log+n2.log;
		}
		@Override
		public String toString()	{
			return Long.toString(mod);
		}
		@Override
		public boolean equals(Object other)	{
			BigNumberSummary bnOther=(BigNumberSummary)other;
			return (mod==bnOther.mod)&&(log==bnOther.log);
		}
		@Override
		public int hashCode()	{
			return 31*Long.hashCode(mod)+Double.hashCode(log);
		}
		public double getLog()	{
			return log;
		}
	}
	
	private static long getMaxPrimePowerForCarmichaelExactValue(long value,long prime)	{
		if (prime==2l)	{
			if ((value%2l)==1l) return 1l;
			else if ((value%4l)==2l) return 8l;
			long result=8l;
			long carmichael=2l;
			for (;;)	{
				carmichael+=carmichael;
				if ((value%carmichael)!=0l) return result;
				result+=result;
			}
		}	else	{
			long result=prime;
			long carmichael=prime-1;
			if ((value%carmichael)!=0l) return 1l;
			for (;;)	{
				carmichael*=prime;
				if ((value%carmichael)!=0l) return result;
				result*=prime;
			}
		}
	}
	
	private static BigNumberSummary getMaxNumberForCarmichaelExactValue(long value,List<Long> primes,long mod)	{
		BigNumberSummary result=new BigNumberSummary();
		for (long prime:primes)	{
			long maxForPrime=getMaxPrimePowerForCarmichaelExactValue(value,prime);
			BigNumberSummary term=new BigNumberSummary(maxForPrime);
			result=new BigNumberSummary(result,term,mod);
		}
		return result;
	}
	
	private static BigNumberSummary searchForMaxCarmichaelArgument(long maxValue,long mod)	{
		maxValue-=maxValue%2;
		List<Long> primes=Primes.listLongPrimes(maxValue-1);
		BigNumberSummary result=new BigNumberSummary();
		long maxIndex=0;
		long lowerLimit=maxValue/2;
		for (long i=maxValue;i>=lowerLimit;i-=2)	{
			if ((i%10000)==0) System.out.println(""+i+"...");
			BigNumberSummary tmpResult=getMaxNumberForCarmichaelExactValue(i,primes,mod);
			if (tmpResult.getLog()>result.getLog())	{
				maxIndex=i;
				result=tmpResult;
				System.out.println("DAS NEUE MAXIMUMS, ODER? FÜRS "+maxIndex+" HABE ICH "+result+" GEFUNDEN!!!!!");
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		BigNumberSummary result=searchForMaxCarmichaelArgument(LIMIT,MOD);
		System.out.println(result);
	}
}
