package com.euler.common;

import static com.euler.common.EulerUtils.areCoprime;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.TreeMultimap;
import com.google.common.math.LongMath;

public class PythagoreanTriples {
	public static class SimplePythagoreanTriple	{
		public long a;
		public long b;
		public SimplePythagoreanTriple()	{}
		public SimplePythagoreanTriple(long a,long b)	{
			this.a=a;
			this.b=b;
		}
	}
	
	public static List<SimplePythagoreanTriple> getSimpleTriplesUpTo(long maxBaseValue)	{
		List<SimplePythagoreanTriple> res=new ArrayList<>();
		for (long m=2;m<=maxBaseValue;++m) for (long n=((m%2)==0)?1:2;n<m;n+=2) if (areCoprime(m,n))	{
			long a=m*m-n*n;
			long b=2*m*n;
			res.add(new SimplePythagoreanTriple(a,b));
		}
		return res;
	}
	
	// This method is quite specific for 583, but it can actually be used for many problems.
	public static TreeMultimap<Long,Long> getExtendedSetOfTriples(long limit,long maxBaseValue)	{
		TreeMultimap<Long,Long> res=TreeMultimap.create();
		for (long m=2;m<=maxBaseValue;++m) for (long n=((m%2)==0)?1:2;n<m;n+=2) if (areCoprime(m,n))	{
			long a=m*m-n*n;
			long b=2*m*n;
			long sum=a+b;
			long maxMultiplier=limit/sum;
			for (long i=1;i<=maxMultiplier;++i)	{
				long aa=i*a;
				long bb=i*b;
				res.put(aa,bb);
				res.put(bb,aa);
			}
		}
		return res;
	}
	
	public static List<SimplePythagoreanTriple> getSimpleTriplesWithPerimeterLimit(long limit)	{
		List<SimplePythagoreanTriple> res=new ArrayList<>();
		boolean lastOdd=false;
		boolean lastEven=false;
		for (long m=2;;++m)	{
			if ((lastOdd&&((m%2)==1))||(lastEven&&((m%2)==0))) continue;
			boolean any=false;
			for (long n=((m%2)==0)?1:2;n<m;n+=2) if (areCoprime(m,n))	{
				long m2=m*m;
				long n2=n*n;
				long a=m2-n2;
				long b=2*m*n;
				long c=m2+n2;
				if (a+b+c<=limit)	{
					res.add(new SimplePythagoreanTriple(a,b));
					any=true;
				}
			}
			if (!any)	{
				if ((m%2)==1) lastOdd=true;
				else lastEven=true;
				if (lastOdd&&lastEven) break;
			}
		}
		return res;
	}
	
	public static TreeMultimap<Long,Long> getExtendedSetOfTriples(long limit)	{
		long maxBaseValue=LongMath.sqrt(limit,RoundingMode.CEILING);
		return getExtendedSetOfTriples(limit,maxBaseValue);
	}
	
	public static boolean isPythagoreanTriple(long a,long b)	{
		long c2=a*a+b*b;
		long c=LongMath.sqrt(c2,RoundingMode.FLOOR);
		return (c*c)==c2;
	}
}
