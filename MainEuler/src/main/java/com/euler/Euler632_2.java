package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler632_2 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long MOD=1000000007l;
	
	private static int getPrimeFactors(long in,long[] primeSieve)	{
		Set<Long> factors=new HashSet<>();
		while (in>1)	{
			long p=primeSieve[(int)in];
			if (p==0) p=in;
			factors.add(p);
			in/=p;
		}
		return factors.size();
	}
	
	public static long[] getSquareFactors(long limit)	{
		// We can calculate the size more accurately. But in the end this is enough and it's not really wasteful.
		long[] result=new long[12];
		long sieveMax=LongMath.sqrt(limit,RoundingMode.DOWN);
		long[] primeSieve=Primes.firstPrimeSieve(sieveMax);
		for (long i=1;i<=sieveMax;++i)	{
			int primeFactors=getPrimeFactors(i,primeSieve);
			long sq=i*i;
			long counter=EulerUtils.getSquareFreeNumbers(limit/sq);
			result[primeFactors]+=counter;
		}
		return result;
	}
	
	private static BigInteger getProduct(long[] map)	{
		BigInteger result=BigInteger.ONE;
		for (long value:map)	{
			if (value==0l) break;
			BigInteger factor=BigInteger.valueOf(value);
			result=result.multiply(factor);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long[] squareFactors=getSquareFactors(LIMIT);
		// This can be done more efficiently using longs, but I want the whole BigInteger because I like my numbers the same way I like my meals: KING SIZE.
		BigInteger bigResult=getProduct(squareFactors);
		BigInteger result=bigResult.mod(BigInteger.valueOf(MOD));
		long tac=System.nanoTime();
		System.out.println("Big result: "+bigResult);
		System.out.println("Result: "+result);
		double seconds=(tac-tic)/1e9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
