package com.euler;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.euler.common.EulerUtils.Pair;
import com.google.common.collect.Lists;

public class Euler385 {
	private final static long LIMIT=1000;
	
	private static class Triangle	{
		private final static Comparator<Pair<Long,Long>> SORTER=(Pair<Long,Long> a,Pair<Long,Long> b)->	{
			long a1=a.first;
			long a2=a.second;
			long b1=b.first;
			long b2=b.second;
			int result=Long.compare(a1,b1);
			return (result!=0)?result:Long.compare(a2,b2);
		};
		public final long a;
		public final long b;
		public final long c;
		public final long d;
		public final long e;
		public final long f;
		private Triangle(long a,long b,long c,long d,long e,long f)	{
			this.a=a;
			this.b=b;
			this.c=c;
			this.d=d;
			this.e=e;
			this.f=f;
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d)-(%d,%d)-(%d,%d)",a,b,c,d,e,f);
		}
		@Override
		public int hashCode()	{
			return Objects.hash(a,b,c,d,e,f);
		}
		@Override
		public boolean equals(Object other)	{
			Triangle tOther=(Triangle)other;
			return (a==tOther.a)&&(b==tOther.b)&&(c==tOther.c)&&(d==tOther.d)&&(e==tOther.e)&&(f==tOther.f);
		}
		public static Triangle of(long a,long b,long c,long d,long e,long f)	{
			List<Pair<Long,Long>> pairs=Lists.newArrayList(new Pair<>(a,b),new Pair<>(c,d),new Pair<>(e,f));
			pairs.sort(SORTER);
			Pair<Long,Long> p1=pairs.get(0);
			Pair<Long,Long> p2=pairs.get(1);
			Pair<Long,Long> p3=pairs.get(2);
			return new Triangle(p1.first,p1.second,p2.first,p2.second,p3.first,p3.second);
		}
		public long area()	{
			long prod=a*d-b*c;
			if ((prod%2)!=0) throw new RuntimeException("OH NOES. Área semientera.");
			return Math.abs(3*prod/2);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Set<Triangle> triangles=new HashSet<>();
		for (long a=-LIMIT;a<=LIMIT;++a) for (long b=-LIMIT;b<=LIMIT;++b) for (long c=-LIMIT;c<=LIMIT;++c)	{
			long num=b*(2*a+c);
			long den=a+2*c;
			if ((den==0)||((num%den)!=0)) continue;
			long d=-num/den;
			if (Math.abs(d)>LIMIT) continue;
			long e=-a-c;
			long f=-b-d;
			if ((Math.abs(e)>LIMIT)||(Math.abs(f)>LIMIT)) continue;
			long eq2=d*d+b*d-a*c-c*c-a*a+b*b;
			if (eq2==-39) triangles.add(Triangle.of(a,b,c,d,-a-c,-b-d));
		}
		long result=0;
		for (Triangle t:triangles)	{
			System.out.println(t+".");
			result+=t.area();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
