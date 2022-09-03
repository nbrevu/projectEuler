package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.Primes;

public class Euler276 {
	// Aaand yet ANOTHER one that goes out on the first try!!!!!
	private final static int LIMIT=10000000;
	private final static int[] firstPrimes=Primes.firstPrimeSieve(LIMIT);
	
	// This function comes from PE forums... and it's a million times better than mine.
	private static long fast_totalTrianglesWithPerimeter(long N)	{
		return (N*N+6)/12-(N/4)*((N+2)/4);
	}
	
	/*
	private static long totalTrianglesWithPerimeter(long N)	{
		long x=N/6l;
		long h=0l,ll=0l;
		// Did you know? Java is stupid and doesn't like switch(long).
		switch ((int)(N%6l))	{
		case 0:
			ll=2*x-1;
			h=3*x-1;
			break;
		case 1:
		case 2:
			ll=2*x-1;
			h=3*x;
			break;
		case 3:
		case 4:
			ll=2*x;
			h=3*x+1;
			break;
		case 5:
			ll=2*x;
			h=3*x+2;
		}
		long l=ll+1l;
		long num=(h+3)*h-(ll+3)*ll;
		long toAdd=0l;
		long initialC,finalC;
		if (((N-h)%2)==0)	{
			toAdd+=(N-h)/2;
			initialC=(N-h)/2+1;
		}	else initialC=(N+1-h)/2;
		if (((N-l)%2)==0) finalC=(N-l)/2;
		else	{
			toAdd+=(N-l+1)/2;
			finalC=(N-l-1)/2;
		}
		long otherSum=toAdd+(finalC+initialC)*(finalC+1-initialC);
		return (num/2)-otherSum;
	}
	*/
	
	private static void addPrime(NavigableSet<Integer> previous,int factor)	{
		List<Integer> copy=new ArrayList<>(previous);
		for (int div:copy) previous.add(div*factor);
	}
	
	private static NavigableSet<Integer> getAllProperDivisors(int number)	{
		// This is inefficient in time, but efficient in time (no memoizing).
		NavigableSet<Integer> result=new TreeSet<>();
		result.add(1);
		for (;;)	{
			int prime=firstPrimes[number];
			if (prime==0)	{
				addPrime(result,number);
				result.pollFirst();
				result.pollLast();
				return result;
			}
			addPrime(result,prime);
			number/=prime;
		}
	}
	
	public static void main(String[] args)	{
		long[] cache=new long[1+LIMIT];
		long sum=0l;
		for (int i=3;i<=LIMIT;++i)	{
			long amount=fast_totalTrianglesWithPerimeter(i);
			for (int divisor:getAllProperDivisors(i)) amount-=cache[divisor];
			cache[i]=amount;
			sum+=amount;
		}
		System.out.println(sum);
	}
}
