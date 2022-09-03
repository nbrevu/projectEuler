package com.euler;

import static com.euler.common.EulerUtils.areCoprime;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.google.common.math.LongMath;

public class Euler279 {
	public final static long LIMIT=LongMath.pow(10l,8);
	
	private static class Triangle	{
		public final long a;
		public final long b;
		public final long c;
		private Triangle(long a,long b,long c)	{
			this.a=a;
			this.b=b;
			this.c=c;
		}
		public static Triangle getFor(long a,long b,long c)	{
			long g=EulerUtils.gcd(EulerUtils.gcd(a,b),c);
			if (g!=1)	{
				a/=g;
				b/=g;
				c/=g;
			}
			long[] sides=new long[] {a,b,c};
			Arrays.sort(sides);
			return new Triangle(sides[0],sides[1],sides[2]);
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(a+b+c);
		}
		@Override
		public boolean equals(Object other)	{
			Triangle tOther=(Triangle)other;
			return (a==tOther.a)&&(b==tOther.b)&&(c==tOther.c);
		}
	}
	
	public static abstract class IntegerTriangleGenerator	{
		protected abstract Triangle getFor(long m,long n);
		protected abstract long getMaxM(long limit);
		public Set<Triangle> getBaseTrianglesWithPerimeterLimit(long limit)	{
			Set<Triangle> result=new HashSet<>();
			long maxM=getMaxM(limit);
			for (long m=2;m<=maxM;++m)	{
				for (long n=((m%2)==0)?1:2;n<m;n+=2) if (areCoprime(m,n))	{
					Triangle t=getFor(m,n);
					if (t.a+t.b+t.c<=limit) result.add(t);
				}
			}
			return result;
		}
	}
	
	public static class Eisenstein60 extends IntegerTriangleGenerator	{
		public final static Eisenstein60 INSTANCE=new Eisenstein60();
		@Override
		protected Triangle getFor(long m,long n)	{
			long m2=m*m;
			long n2=n*n;
			long mn=m*n;
			long a=m2+n2-mn;
			long b=2*mn-n2;
			long c=m2-n2;
			return Triangle.getFor(a,b,c);
		}
		@Override
		protected long getMaxM(long limit) {
			return LongMath.sqrt((3*limit)/2,RoundingMode.DOWN);
		}
	}
	
	public static class Pythagorean extends IntegerTriangleGenerator	{
		public final static Pythagorean INSTANCE=new Pythagorean();
		@Override
		protected Triangle getFor(long m,long n)	{
			long m2=m*m;
			long n2=n*n;
			long a=m2-n2;
			long b=2*m*n;
			long c=m2+n2;
			return Triangle.getFor(a,b,c);
		}
		@Override
		protected long getMaxM(long limit) {
			return LongMath.sqrt(limit/2,RoundingMode.DOWN);
		}
	}
	
	public static class Eisenstein120 extends IntegerTriangleGenerator	{
		public final static Eisenstein120 INSTANCE=new Eisenstein120();
		@Override
		protected Triangle getFor(long m,long n)	{
			long m2=m*m;
			long n2=n*n;
			long mn=m*n;
			long a=m2+n2+mn;
			long b=2*mn+n2;
			long c=m2-n2;
			return Triangle.getFor(a,b,c);
		}
		@Override
		protected long getMaxM(long limit) {
			return LongMath.sqrt((3*limit)/2,RoundingMode.DOWN);
		}
	}
	
	private static long countIntegerTriangles(long limit)	{
		IntegerTriangleGenerator[] generators=new IntegerTriangleGenerator[]{Eisenstein60.INSTANCE,Pythagorean.INSTANCE,Eisenstein120.INSTANCE};
		long result=0l;
		for (IntegerTriangleGenerator gen:generators)	{
			Set<Triangle> baseTriangles=gen.getBaseTrianglesWithPerimeterLimit(3*limit);
			for (Triangle t:baseTriangles)	{
				long perimeter=t.a+t.b+t.c;
				result+=limit/perimeter;
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=countIntegerTriangles(LIMIT);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result+".");
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
