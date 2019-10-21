package com.euler.common;

import static com.euler.common.EulerUtils.areCoprime;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.math.LongMath;

public class EisenstenianTriples120 {
	public static class PrimitiveTriplesIterator	{
		private long m;
		private long n;
		public PrimitiveTriplesIterator()	{
			m=1;
			n=1;
		}
		public void next()	{
			do	{
				++n;
				if (n>=m)	{
					++m;
					n=1;
				}
			}	while ((((m-n)%3)==0)||!areCoprime(m,n));
		}
		public long a()	{
			return 2*m*n+n*n;
		}
		public long b()	{
			return m*m-n*n;
		}
		public long c()	{
			return m*m+m*n+n*n;
		}
		private long m()	{
			return m;
		}
	}
	
	public static List<Triangle> getSimpleTriplesUpTo(long maxBaseValue)	{
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		List<Triangle> result=new ArrayList<>();
		for (;;)	{
			iterator.next();
			if (iterator.m()>maxBaseValue) return result;
			result.add(new Triangle(iterator.a(),iterator.b(),iterator.c()));
		}
	}
	
	public static boolean isEisenstenian120Triple(long a,long b)	{
		long c2=a*a+b*b+a*b;
		long c=LongMath.sqrt(c2,RoundingMode.FLOOR);
		return (c*c)==c2;
	}
}
