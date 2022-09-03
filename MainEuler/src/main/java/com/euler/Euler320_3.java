package com.euler;

import com.euler.common.Primes;

public class Euler320_3 {
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
	
	private static long minFactorial(boolean composites[],long in,long power)	{
		long res=0;
		for (long i=2;i<=in;++i) if (!composites[(int)i])	{
			long appearances=primeAppearancesInFactorial(i,in);
			long totalAppearances=appearances*power;
			long minimum=findMin(i,totalAppearances);
			res=Math.max(res,minimum);
		}
		System.out.println(""+in+": "+res);
		return res;
	}
	
	private final static long POWER=1234567890l;
	private final static int LIM_INF=10;
	private final static int LIM_SUP=1000000;
	private final static long MOD=1000000000000000000l;
	
	public static void main(String[] args)	{
		boolean composites[]=Primes.sieve(LIM_SUP);
		long res=0;
		for (long i=LIM_INF;i<=LIM_SUP;++i) res=(res+minFactorial(composites,i,POWER))%MOD;
		System.out.println(res);
	}
}
