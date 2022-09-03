package com.euler.experiments;

import java.util.HashSet;
import java.util.Set;

import com.google.common.math.LongMath;

public class CountingSquareDigits {
	private final static long LIMIT=LongMath.pow(10l,5);
	
	public static void main(String[] args)	{
		Set<Integer> digits=new HashSet<>();
		for (long i=0;i<=LIMIT;++i)	{
			long sq=i*i;
			while (sq>0)	{
				digits.add((int)(sq%10));
				sq/=10;
			}
			if (digits.size()==2) System.out.println(String.format("%d^2=%d.",i,i*i));
			digits.clear();
		}
	}
}
