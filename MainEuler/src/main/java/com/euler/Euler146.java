package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.euler.common.Primes;

public class Euler146 {
	private final static int LIMIT=150000000;
	/*
	private final static List<Integer> COMBINATIONS=Arrays.asList(10,220,430,640,1180,1390,1600,1810,1960,2170,2380,2590,140,350,560,770,920,1130,1340,1550,2090,2300,2510,2720);
	private final static int MODULUS=2730;
	*/
	private final static int[] COMBINATIONS=new int[] {10,80,130,200};
	private final static int MODULUS=210;
	private final static int[] EXCESSES=new int[] {1,3,7,9,13,27};
	private final static List<Integer> RABIN_MILLER_WITNESSES=Arrays.asList(2,3,5,7,11,13,17);
	private final static int[] SMALL_PRIMES=Primes.listIntPrimes(1000).stream().skip(4).mapToInt(Integer::intValue).toArray();
	private static boolean isValid(long n,Primes.RabinMiller rabinMiller)	{
		long n2=n*n;
		for (int i:EXCESSES)	{
			long trial=n2+i;
			for (int p:SMALL_PRIMES) if ((trial%p)==0) return false;
			else if (p>n) break;
			if (!rabinMiller.isPrime(BigInteger.valueOf(trial), RABIN_MILLER_WITNESSES)) return false;
		}
		return true;
	}
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Primes.RabinMiller rm=new Primes.RabinMiller();
		long sum=0;
		for (int i:COMBINATIONS) for (long n=i;n<LIMIT;n+=MODULUS)	{
			if (isValid(n,rm))	{
				System.out.println(""+n+"...");
				sum+=n;
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(sum);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
