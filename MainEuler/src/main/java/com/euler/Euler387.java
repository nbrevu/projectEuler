package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.euler.common.Primes;

public class Euler387 {
	// Also on the first try. This was easy :).
	private final static int DIGITS=13;
	
	private static List<Long> primes=Primes.listLongPrimes(10000000l);
	
	private static boolean isPrime(long number)	{
		if (number<=1) return false;
		for (long p:primes) if (p*p>number) return true;
		else if ((number%p)==0) return false;
		return true;
	}
	
	private static class HarshadNumber	{
		private final long number;
		private final long digitSum;
		public HarshadNumber(long number,long digitSum)	{
			this.number=number;
			this.digitSum=digitSum;
		}
		public List<HarshadNumber> getChildren()	{
			long newNumber=number*10;
			long newSum=digitSum;
			List<HarshadNumber> children=new ArrayList<>();
			children.add(new HarshadNumber(newNumber,newSum));
			for (int i=1;i<10;++i)	{
				++newNumber;
				++newSum;
				if ((newNumber%newSum)==0l) children.add(new HarshadNumber(newNumber,newSum));
			}
			return children;
		}
		public List<Long> strongChildren()	{
			if (!isPrime(number/digitSum)) return Collections.emptyList();
			List<Long> result=new ArrayList<>();
			long newNum=10*number;
			List<Long> candidates=Arrays.asList(newNum+1,newNum+3,newNum+7,newNum+9);
			for (long l:candidates) if (isPrime(l)) result.add(l);
			return result;
		}
	}
	
	public static List<HarshadNumber> getInitialList()	{
		List<HarshadNumber> list=new ArrayList<>(9);
		for (long i=1l;i<=9l;++i) list.add(new HarshadNumber(i,i));
		return list;
	}
	
	public static List<HarshadNumber> getNextGeneration(List<HarshadNumber> list)	{
		List<HarshadNumber> result=new ArrayList<>();
		for (HarshadNumber num:list) result.addAll(num.getChildren());
		return result;
	}
	
	public static void main(String[] args)	{
		List<HarshadNumber> list=null;
		long sum=0l;
		for (int i=1;i<=DIGITS;++i)	{
			if (i==1) list=getInitialList();
			else list=getNextGeneration(list);
			for (HarshadNumber num:list) for (long l:num.strongChildren()) sum+=l;
		}
		System.out.println(sum);
	}
}
