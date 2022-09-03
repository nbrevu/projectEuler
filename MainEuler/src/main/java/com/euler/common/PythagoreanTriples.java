package com.euler.common;

import static com.euler.common.EulerUtils.*;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.TreeMultimap;
import com.google.common.math.LongMath;

public class PythagoreanTriples {
	public static class ExtendedPythagoreanTriple	{
		public final long a;
		public final long b;
		public final long c;
		public final long m;
		public final long n;
		public ExtendedPythagoreanTriple(long a,long b,long c,long m,long n)	{
			this.a=a;
			this.b=b;
			this.c=c;
			this.m=m;
			this.n=n;
		}
	}
	public static class PrimitiveTriplesIterator	{
		private long m;
		private long n;
		public PrimitiveTriplesIterator()	{
			m=1;
			n=1;
		}
		public void nextM()	{
			++m;
			n=(m%2)+1;
		}
		public void next()	{
			do	{
				n+=2;
				if (n>=m)	{
					++m;
					n=(m%2)+1;
				}
			}	while (!areCoprime(m,n));
		}
		public long a()	{
			return m*m-n*n;
		}
		public long b()	{
			return 2*m*n;
		}
		public long c()	{
			return m*m+n*n;
		}
		public ExtendedPythagoreanTriple getExtendedTriple()	{
			return new ExtendedPythagoreanTriple(a(),b(),c(),m,n);
		}
		public boolean isSmallestN()	{
			/*
			 * Return true if and only if this triple is the first one for the current value of N. This is useful in case we want to iterate
			 * up until certain limit which happens to be a combination of a, b and c. The triples are not generated in exact order, but if
			 * you arrive to a triple that is too high AND this condition is satisfied, it's a pretty safe bet to say that you're finished. 
			 */
			return n<=2;
		}
	}
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
