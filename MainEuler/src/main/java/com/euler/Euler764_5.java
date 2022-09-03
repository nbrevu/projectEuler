package com.euler;

import java.math.BigInteger;

import com.euler.common.EulerUtils;
import com.euler.common.PythagoreanTriples.ExtendedPythagoreanTriple;
import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;

public class Euler764_5 {
	private final static BigInteger LIMIT=BigInteger.TEN.pow(7);

	private final static BigInteger FOUR=BigInteger.valueOf(4l);
	private final static BigInteger EIGHT=BigInteger.valueOf(8l);
	
	private static class SpecialTriple	{
		public final BigInteger x;
		public final BigInteger y;
		public final BigInteger z;
		public SpecialTriple(BigInteger x,BigInteger y,BigInteger z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
	}
	
	private static SpecialTriple standardTransform(ExtendedPythagoreanTriple triple)	{
		BigInteger b=BigInteger.valueOf(triple.b);
		BigInteger c=BigInteger.valueOf(triple.c);
		BigInteger z=b.multiply(b).add(c.multiply(c));
		if (z.compareTo(LIMIT)>0) return null;
		BigInteger x=BigInteger.valueOf(triple.m).multiply(BigInteger.valueOf(triple.n)).multiply(c);
		BigInteger y=BigInteger.valueOf(triple.a);
		return new SpecialTriple(x,y,z);
	}
	
	private static SpecialTriple squareTransform(long m,long n)	{
		BigInteger bm=BigInteger.valueOf(m);
		BigInteger m2=bm.multiply(bm);
		BigInteger bn=BigInteger.valueOf(n);
		BigInteger n2=bn.multiply(bn);
		BigInteger z=FOUR.multiply(m2.add(n2));
		if (z.compareTo(LIMIT)>0) return null;
		BigInteger y2=EIGHT.multiply(bm).multiply(bn);
		BigInteger y=y2.sqrt();
		BigInteger x=m2.subtract(n2).abs();
		return new SpecialTriple(x,y,z);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		BigInteger result=BigInteger.ZERO;
		// First case: standard triples (y is odd).
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		iterator.next();
		int counter=0;
		for (;;)	{
			SpecialTriple triple=standardTransform(iterator.getExtendedTriple());
			if (triple==null)	{
				if (iterator.isSmallestN()) break;
				else iterator.nextM();
			}	else	{
				++counter;
				result=result.add(triple.x).add(triple.y).add(triple.z);
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
				SpecialTriple triple=squareTransform(m2,n2);
				if (triple==null)	{
					if ((n==initialN)&&isMEven) canContinue=false;
					break;
				}
				++counter;
				result=result.add(triple.x).add(triple.y).add(triple.z);
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(counter);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
