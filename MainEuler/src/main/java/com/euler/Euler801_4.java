package com.euler;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes;
import com.euler.common.Primes.RabinMiller;
import com.google.common.math.LongMath;

public class Euler801_4 {
	private final static long START=LongMath.pow(10l,16)+1;
	private final static long END=START+LongMath.pow(10l,6);
	
	private final static int[] INT_WITNESSES=new int[] {2,3,5,7,11,13,17,19,23};
	private final static BigInteger[] WITNESSES=Arrays.stream(INT_WITNESSES).mapToObj(BigInteger::valueOf).toArray(BigInteger[]::new);
	
	public static void main(String[] args)	{
		long[] primes=Primes.listLongPrimesAsArray(LongMath.sqrt(END,RoundingMode.DOWN));
		RabinMiller primeTest=new RabinMiller();
		int count=0;
		long otherCount=0;
		for (long p=START;p<=END;p+=2) if ((p%3==0)||(p%5==0)||(p%7==0)) continue;
		else if (primeTest.isPrime(BigInteger.valueOf(p),WITNESSES))	{
			System.out.println(String.format("%d is prime.",p));
			++count;
			long n=p-1;
			DivisorHolder div=new DivisorHolder();
			for (long q:primes) if ((n%q)==0)	{
				int exp=0;
				do	{
					++exp;
					n/=q;
				}	while ((n%q)==0);
				div.addFactor(q,exp);
				if (n==1) break;
			}	else if (q*q>n)	{
				div.addFactor(n,1);
				break;
			}
			System.out.println(String.format("\t%d=%s.",p-1,div.toString()));
			long amount=div.getAmountOfDivisors();
			System.out.println(String.format("\t%d has %d divisors.",p-1,amount));
			otherCount+=amount;
		}
		System.out.println();
		System.out.println(String.format("There are %d primes, with a total of %d divisors to consider.",count,otherCount));
	}
}
