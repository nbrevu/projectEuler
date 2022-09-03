package com.euler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.euler.common.EulerUtils;

public class Euler769_3 {
	private final static long N=1000;
	
	private static class Tuple2	{
		public final long p;
		public final long q;
		public Tuple2(long p,long q)	{
			this.p=p;
			this.q=q;
		}
		@Override
		public String toString()	{
			return String.format("p=%d, q=%d, g=%d",p,q,5*q-2*q);
		}
	}
	
	private static class Tuple3	{
		public final long x;
		public final long y;
		public final long z;
		public Tuple3(long x,long y,long z)	{
			this.x=x;
			this.y=y;
			this.z=z;
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(x+y+z);
		}
		@Override
		public boolean equals(Object other)	{
			Tuple3 tOther=(Tuple3)other;
			return (x==tOther.x)&&(y==tOther.y)&&(z==tOther.z);
		}
		@Override
		public String toString()	{
			return String.format("x=%d, y=%d, z=%d",x,y,z);
		}
	}
	
	public static void main(String[] args)	{
		Map<Tuple3,List<Tuple2>> values=new HashMap<>();
		for (long p=1;p<=N;++p) for (long q=1;q<=N;++q) if (EulerUtils.areCoprime(p,q))	{
			// long g=5*p-2*q;
			long pp=p*p;
			long qq=q*q;
			long pq=p*q;
			long x=qq-3*pp;
			long y=5*pp-2*pq;
			long z=-3*pp+5*pq-qq;
			if ((x<0)&&(y<0)) throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
			if (Math.signum(x)!=Math.signum(y)) continue;
			long g=Math.abs(EulerUtils.gcd(x,y));
			x=Math.abs(x/g);
			y=Math.abs(y/g);
			z=Math.abs(z/g);
			long xx=x*x;
			long xy=x*y;
			long yy=y*y;
			long zz=z*z;
			if ((xx+5*xy+3*yy)!=zz) System.out.println("Fifa.");
			System.out.println(String.format("p=%d, q=%d => x=%d, y=%d, z=%d (gcd=%d).",p,q,x,y,z,g));
			if (z<=N) values.computeIfAbsent(new Tuple3(x,y,z),(Tuple3 unused)->new ArrayList<>()).add(new Tuple2(p,q));
		}
		System.out.println(values.size());
		for (Map.Entry<Tuple3,List<Tuple2>> entry:values.entrySet()) System.out.println(entry.getKey()+" => "+entry.getValue()+".");
	}
}
