package com.euler;

import java.math.BigInteger;
import java.util.Arrays;

import com.euler.common.BigIntegerUtils.BigCombinatorialNumberCache;
import com.euler.common.Primes;
import com.google.common.math.LongMath;

public class Euler423_3 {
	private final static int IN=50;
	
	private static int[] getM(int max)	{
		boolean[] composites=Primes.sieve(max);
		int[] result=new int[1+max];
		result[0]=0;
		int index=1;
		for (int i=2;i<=max;++i) if (composites[i])	{
			result[index]=i-1;
			++index;
		}
		Arrays.fill(result,index,max+1,max);
		return result;
	}
	
	public static void main(String[] args)	{
		BigCombinatorialNumberCache cache=new BigCombinatorialNumberCache(IN-1);
		BigInteger result=BigInteger.ZERO;
		BigInteger five=BigInteger.valueOf(5l);
		BigInteger six=BigInteger.valueOf(6l);
		int[] MAX_J=getM(IN);
		for (int k=1;k<=IN;++k)	{
			int m=MAX_J[k];
			result=result.add(five.pow(k-1).multiply(cache.get(m,k)));
		}
		result=result.multiply(six);
		System.out.println(result);
		System.out.println(832833871l);
		System.out.println(result.mod(BigInteger.valueOf(LongMath.pow(10l,9)+7)));
	}
}
