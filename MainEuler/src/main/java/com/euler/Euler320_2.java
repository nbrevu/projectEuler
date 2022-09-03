package com.euler;

import java.math.BigInteger;

import com.euler.common.Primes;

public class Euler320_2 {
	private static long primeAppearancesInFactorial(long prime,long factorial)	{
		long res=0;
		while (factorial>=prime)	{
			factorial/=prime;
			res+=factorial;
		}
		return res;
	}
	
	private static BigInteger primeAppearancesInFactorial(BigInteger prime,BigInteger factorial)	{
		BigInteger res=BigInteger.ZERO;
		while (factorial.compareTo(prime)>=0)	{
			factorial=factorial.divide(prime);
			res=res.add(factorial);
		}
		return res;
	}
	
	// ZUTUN! Esto est� MUY mal, aparentemente.
	private static BigInteger findMin(BigInteger prime,BigInteger power)	{
		BigInteger pMinusOne=prime.subtract(BigInteger.ONE);
		BigInteger approx=power.multiply(pMinusOne);
		BigInteger correction=BigInteger.ZERO;
		BigInteger prevCorrection=null;
		for (;;)	{
			BigInteger result=primeAppearancesInFactorial(prime,approx);
			BigInteger excess=result.subtract(power);
			if (excess.equals(BigInteger.ZERO))	{
				correction=approx.mod(prime);
				return approx.subtract(correction);
			}
			if (excess.signum()<0)	{
				// result-power<0, so result<power. We need to add.
				excess=excess.negate();
				correction=excess.multiply(pMinusOne);
				if ((prevCorrection!=null)&&(correction.compareTo(prevCorrection)>=0)) correction=prevCorrection.subtract(BigInteger.ONE);
				if (correction.equals(BigInteger.ZERO)) correction=BigInteger.ONE;
				prevCorrection=correction;
				approx=approx.add(correction);
				continue;
			}
			// result-power>0. We need to subtract. Or do we?
			BigInteger prevApprox=approx.subtract(BigInteger.ONE);
			BigInteger prevResult=primeAppearancesInFactorial(prime,prevApprox);
			if (prevResult.compareTo(power)<0) return approx;
			correction=excess.multiply(pMinusOne);
			if ((prevCorrection!=null)&&(correction.compareTo(prevCorrection)>=0)) correction=prevCorrection.subtract(BigInteger.ONE);
			if (correction.equals(BigInteger.ZERO)) correction=BigInteger.ONE;
			prevCorrection=correction;
			approx=approx.subtract(correction);
		}
	}
	
	private static BigInteger minFactorial(boolean composites[],long in,BigInteger power)	{
		BigInteger res=BigInteger.ZERO;
		for (long i=2;i<=in;++i) if (!composites[(int)i])	{
			BigInteger appearances=BigInteger.valueOf(primeAppearancesInFactorial(i,in));
			BigInteger totalAppearances=appearances.multiply(power);
			BigInteger minimum=findMin(BigInteger.valueOf(i),totalAppearances);
			if (res.compareTo(minimum)<0) res=minimum;
		}
		System.out.println(""+in+": "+res.toString());
		return res;
	}
	
	private final static BigInteger POWER=BigInteger.valueOf(1234567890l);
	private final static int LIM_INF=10;
	private final static int LIM_SUP=1000000;
	private final static BigInteger MOD=BigInteger.valueOf(1000000000000000000l);
	
	public static void main(String[] args)	{
		boolean composites[]=Primes.sieve(LIM_SUP);
		BigInteger res=BigInteger.ZERO;
		for (long i=LIM_INF;i<=LIM_SUP;++i) res=res.add(minFactorial(composites,i,POWER)).mod(MOD);
		System.out.println(res.toString());
	}
}
