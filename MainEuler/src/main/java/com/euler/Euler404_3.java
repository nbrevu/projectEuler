package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.PythagoreanTriples.PrimitiveTriplesIterator;
import com.google.common.math.LongMath;

public class Euler404_3 {
	private final static long N=LongMath.pow(10l,3);
	
	private static class ModifiedTriple	{
		public final long x;
		public final long y;
		public final long z;
		public final long a;
		public final long b;
		public final long c;
		public ModifiedTriple(long x,long y,long z)	{
			this.x=x;
			this.y=y;
			this.z=z;
			a=x*y;
			long b0=x*z;
			long c0=2*y*z;
			if (b0<c0)	{
				b=b0;
				c=c0;
			}	else	{
				b=c0;
				c=b0;
			}
		}
		public boolean isValid()	{
			return c<2*a;
		}
		public static ModifiedTriple getTypeA(long m,long n,long z)	{
			long x=Math.abs(2*m-n);
			long y=m+2*n;
			if (((x%2)==0)&&((y%2)==0)) throw new IllegalStateException();
			else if ((x%2)==0)	{
				long s=x;
				x=y;
				y=s/2;
			}	else if ((y%2)==0) y/=2;
			else throw new IllegalStateException();
			return EulerUtils.areCoprime(x,y)?new ModifiedTriple(x,y,z):null;
		}
		public static ModifiedTriple getTypeB(long m,long n,long z)	{
			long x=2*m+n;
			long y=Math.abs(m-2*n);
			if (((x%2)==0)&&((y%2)==0)) throw new IllegalStateException();
			else if ((x%2)==0)	{
				long s=x;
				x=y;
				y=s/2;
			}	else if ((y%2)==0) y/=2;
			else throw new IllegalStateException();
			return EulerUtils.areCoprime(x,y)?new ModifiedTriple(x,y,z):null;
		}
	}
	
	public static void main(String[] args)	{
		long result=0;
		PrimitiveTriplesIterator iterator=new PrimitiveTriplesIterator();
		for (;;)	{
			iterator.next();
			long m=iterator.a();
			long n=iterator.b();
			long z=iterator.c();
			ModifiedTriple tripleA=ModifiedTriple.getTypeA(m,n,z);
			ModifiedTriple tripleB=ModifiedTriple.getTypeB(m,n,z);
			System.out.println(String.format("Pythagorean triple: (%d,%d,%d).",m,n,z));
			if (tripleA==null) System.out.println("\tCan't modify for type A.");
			else System.out.println(String.format("\tModified triple type A: x=%d, y=%d, z=%d, a=%d, b=%d, c=%d (%s).",tripleA.x,tripleA.y,tripleA.z,tripleA.a,tripleA.b,tripleA.c,tripleA.isValid()?"valid":"non valid"));
			if (tripleB==null) System.out.println("\tCan't modify for type B.");
			else System.out.println(String.format("\tModified triple type B: x=%d, y=%d, z=%d, a=%d, b=%d, c=%d (%s).",tripleB.x,tripleB.y,tripleB.z,tripleB.a,tripleB.b,tripleB.c,tripleB.isValid()?"valid":"non valid"));
			boolean someValid=false;
			boolean someBelow=false;
			if ((tripleA!=null)&&tripleA.isValid())	{
				someValid=true;
				if (tripleA.a<=N) ++result;
				if (tripleA.a<=2*N) someBelow=true;
			}
			if ((tripleB!=null)&&tripleB.isValid())	{
				someValid=true;
				if (tripleB.a<=N) ++result;
				if (tripleB.a<=2*N) someBelow=true;
			}
			if (someValid&&!someBelow) break;
		}
		System.out.println(result);
	}
}
