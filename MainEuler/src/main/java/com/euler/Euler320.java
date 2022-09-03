package com.euler;

import java.math.BigInteger;

import com.euler.common.Primes;

public class Euler320 {
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
		BigInteger approx=power.divide(prime.subtract(BigInteger.ONE));
		for (;;)	{
			BigInteger product=prime.multiply(approx);
			BigInteger result=primeAppearancesInFactorial(prime,product);
			if (result.equals(power)) return product;
			if (result.compareTo(power)<0) approx=approx.add(power.subtract(result));
			else	{
				BigInteger resultMinus=primeAppearancesInFactorial(prime,product.subtract(prime));
				if (resultMinus.compareTo(power)<0) return approx;
				approx=approx.add(result.subtract(power));
			}
		}
	}
	
	private static BigInteger minFactorial(boolean composites[],long in,BigInteger power)	{
		BigInteger res=BigInteger.ZERO;
		for (long i=2;i<in;++i) if (!composites[(int)i])	{
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
	private final static int LIM_SUP=1000;
	private final static BigInteger MOD=BigInteger.valueOf(1000000000000000000l);
	
	public static void main(String[] args)	{
		boolean composites[]=Primes.sieve(LIM_SUP);
		BigInteger res=BigInteger.ZERO;
		for (long i=LIM_INF;i<=LIM_SUP;++i) res=res.add(minFactorial(composites,i,POWER)).mod(MOD);
		System.out.println(res.toString());
	}
}
