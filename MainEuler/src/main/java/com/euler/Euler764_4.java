package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.PythagoreanTriples.ExtendedPythagoreanTriple;
import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;
import com.google.common.math.LongMath;

public class Euler764_4 {
	private final static long LIMIT=LongMath.pow(10l,16);
	private final static long MOD=LongMath.pow(10l,9);
	
	private static class SpecialTriple	{
		public final long x;
		public final long y;
		public final long z;
		public SpecialTriple(long x,long y,long z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
	}
	
	private static SpecialTriple standardTransform(ExtendedPythagoreanTriple triple)	{
		long z=triple.b*triple.b+triple.c*triple.c;
		if (z>LIMIT) return null;
		long x=triple.m*triple.n*triple.c;
		long y=triple.a;
		return new SpecialTriple(x,y,z);
	}
	
	private static SpecialTriple squareTransform(long m,long n,long baseM,long baseN)	{
		long m2=m*m;
		long n2=n*n;
		long c=m2+n2;
		long z=4*c;
		if (z>LIMIT) return null;
		long y=2*baseM*baseN;
		long x=Math.abs(m2-n2);
		return new SpecialTriple(x,y,z);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		// First case: standard triples (y is odd).
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		iterator.next();
		for (;;)	{
			SpecialTriple triple=standardTransform(iterator.getExtendedTriple());
			if (triple==null)	{
				if (iterator.isSmallestN()) break;
				else iterator.nextM();
			}	else	{
				result+=triple.x+triple.y+triple.z;
				result%=MOD;
				iterator.next();
			}
		}
		// Second case: "square" triples (y is even).
		boolean canContinue=true;
		for (int m=2;canContinue;++m)	{
			boolean isMEven=(m%2)==0;
			long m2=m*m;
			if (isMEven) m2>>=1;
			int initialN=isMEven?1:2;
			for (int n=initialN;n<m;n+=2) if (EulerUtils.areCoprime(m,n))	{
				long n2=n*n;
				if (!isMEven) n2>>=1;
				SpecialTriple triple=squareTransform(m2,n2,m,n);
				if (triple==null)	{
					if ((n==initialN)&&isMEven) canContinue=false;
					break;
				}
				result+=triple.x+triple.y+triple.z;
				result%=MOD;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
