package com.euler;

import java.util.HashSet;
import java.util.Set;

import com.euler.common.Primes;

public class Euler320_4 {
	private static long primeAppearancesInFactorial(long prime,long factorial)	{
		long res=0;
		while (factorial>=prime)	{
			factorial/=prime;
			res+=factorial;
		}
		return res;
	}
	
	private static long findMin(long prime,long power)	{
		long pMinusOne=prime-1;
		long approx=power*pMinusOne;
		long correction=0;
		long prevCorrection=0;
		for (;;)	{
			long result=primeAppearancesInFactorial(prime,approx);
			long excess=result-power;
			if (excess==0)	{
				// We need to do this because there are several numbers that share the same result.
				correction=approx%prime;
				return approx-correction;
			}
			if (excess<0)	{
				// result-power<0, so result<power. We need to add.
				excess=-excess;;
				correction=excess*pMinusOne;
				if ((prevCorrection!=0)&&(correction>=prevCorrection)) correction=prevCorrection-1;
				if (correction==0) correction=1;
				prevCorrection=correction;
				approx+=correction;
				continue;
			}
			// result-power>0. We need to subtract. Or do we?
			long prevApprox=approx-1;
			long prevResult=primeAppearancesInFactorial(prime,prevApprox);
			if (prevResult<power) return approx;
			correction=excess*pMinusOne;
			if ((prevCorrection!=0)&&(correction>=prevCorrection)) correction=prevCorrection-1;
			if (correction==0) correction=1;
			prevCorrection=correction;
			approx-=correction;
		}
	}
	
	private static long minFactorialForPrime(long in,long p,long power)	{
		long appearances=primeAppearancesInFactorial(p,in);
		long totalAppearances=appearances*power;
		return findMin(p,totalAppearances);
	}
	
	private static long minFactorial(long firstPrimes[],long in,long power)	{
		long res=0;
		for (long i=2;i<=in;++i) if (firstPrimes[(int)i]==0) res=Math.max(res,minFactorialForPrime(in,i,power));
		return res;
	}
	
	private static long updateMinFactorial(long previous,long in,long power,Set<Long> factors)	{
		long res=previous;
		for (long i:factors) res=Math.max(res,minFactorialForPrime(in,i,power));
		return res;
	}
	
	private static Set<Long> getFactors(long in,long[] firstPrimeSieve)	{
		Set<Long> result=new HashSet<>();
		while (in>1)	{
			long factor=firstPrimeSieve[(int)in];
			if (factor==0)	{
				result.add(in);
				return result;
			}
			result.add(factor);
			in/=factor;
		}
		return result;
	}
	
	private final static long POWER=1234567890l;
	private final static int LIM_INF=10;
	private final static int LIM_SUP=1000000;
	private final static long MOD=1000000000000000000l;
	
	// Considering the previous versions, this one is INSULTINGLY fast.
	public static void main(String[] args)	{
		long[] firstPrimes=Primes.firstPrimeSieve((long)LIM_SUP);
		long prev=minFactorial(firstPrimes,LIM_INF,POWER);
		long res=prev;
		for (long i=1+LIM_INF;i<=LIM_SUP;++i)	{
			prev=updateMinFactorial(prev,i,POWER,getFactors(i,firstPrimes));
			res=(res+prev)%MOD;
		}
		System.out.println(res);
	}
}
