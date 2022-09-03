package com.euler;

import java.util.HashMap;
import java.util.Map;

public class Euler290_2 {
	private final static int N=18;
	
	private static class Searcher	{
		private static class Identifier	{
			public final int n;
			public final int a;
			public final int b;
			public Identifier(int n,int a,int b)	{
				this.n=n;
				this.a=a;
				this.b=b;
			}
			@Override
			public boolean equals(Object other)	{
				// Add an instanceof? Yeah, like I'm going to use a different type in this program...
				Identifier iOther=(Identifier)other;
				return (n==iOther.n)&&(a==iOther.a)&&(b==iOther.b);
			}
			@Override
			public int hashCode()	{
				return Integer.hashCode(n)+Integer.hashCode(a)+Integer.hashCode(b);
			}
		}
		// For each (n,a,b), stores the amount of numbers with at most n digits so that digitSum(n)=digitSum(137*n+a)+b.
		private Map<Identifier,Long> cache;
		public Searcher()	{
			cache=new HashMap<>();
		}
		public long search(int in)	{
			return search(in,0,0);
		}
		private int digitSum(int in)	{
			int result=0;
			while (in>0)	{
				result+=(in%10);
				in/=10;
			}
			return result;
		}
		private long search(int in,int u,int v)	{
			Identifier id=new Identifier(in,u,v);
			Long result=cache.get(id);
			if (result!=null) return result.longValue();
			else if (in==1)	{
				result=0l;
				for (int i=0;i<10;++i) if ((digitSum(137*i+u)+v)==i) ++result;
			}	else	{
				result=0l;
				for (int i=0;i<10;++i)	{
					int nextN=137*i+u;
					int nextU=nextN/10;
					int nextV=(nextN%10)+v-i;
					result+=search(in-1,nextU,nextV);
				}
			}
			cache.put(id,result);
			return result;
		}
	}
	
	public static void main(String[] args)	 {
		System.out.println(new Searcher().search(N));
	}
}
