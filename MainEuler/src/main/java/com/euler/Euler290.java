package com.euler;

import java.util.HashMap;
import java.util.Map;

public class Euler290 {
	private final static int N=18;
	
	private static class Searcher	{
		private Map<Integer,Long> cache;
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
			int id=((in*137)+u)*240+v+120;
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
