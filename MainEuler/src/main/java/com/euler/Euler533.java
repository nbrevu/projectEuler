package com.euler;

import java.util.List;

import com.euler.common.Primes;

public class Euler533 {
	private final static long LIMIT=100;
	
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
	
	// This is now a long, but it will be changed to a different structure.
	private static long getMaxNumberForCarmichaelExactValue(long value,List<Long> primes)	{
		long result=1;
		for (long prime:primes) result*=getMaxPrimePowerForCarmichaelExactValue(value,prime);
		return result;
	}
	
	private static long searchForMaxCarmichaelArgument(long maxValue)	{
		List<Long> primes=Primes.listLongPrimes(maxValue-1);
		long result=1;
		long maxIndex=0;
		for (long i=maxValue;i>=2;--i)	{
			long tmpResult=getMaxNumberForCarmichaelExactValue(i,primes);
			if (tmpResult>result)	{
				maxIndex=i;
				result=tmpResult;
				System.out.println("DAS NEUE MAXIMUMS, ODER? FÜRS "+maxIndex+" HABE ICH "+result+" GEFUNDEN!!!!!");
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long result=searchForMaxCarmichaelArgument(LIMIT);
		System.out.println(result);
	}
}
