package com.euler;

import java.util.Map;
import java.util.TreeMap;

public class Euler288 {
	// "Number 629527 appeared in position 1, but also in position 6308949. Therefore, there is a cycle of length 6308948."
	private final static long K1=290797;
	private final static long K2=50515093;
	
	private static class NumberGenerator	{
		public long n;
		public NumberGenerator()	{
			n=K1;
		}
		public long generate()	{
			long result=n;
			n=(n*n)%K2;
			return result;
		}
	}
	
	private static class CycleFinder	{
		public static class Match	{
			public final int previous;
			public final int current;
			public Match(int previous,int current)	{
				this.previous=previous;
				this.current=current;
			}
		}
		private int currentIndex;
		private final Map<Long,Integer> history;
		
		public CycleFinder()	{
			history=new TreeMap<>();
			currentIndex=0;
		}
		
		public Match newNumber(long n)	{
			Integer olderAppearance=history.get(n);
			if (olderAppearance!=null) return new Match(olderAppearance,currentIndex);
			else	{
				history.put(n,currentIndex);
				++currentIndex;
				return null;
			}
		}
	}
	
	public static void main(String[] args)	{
		CycleFinder cf=new CycleFinder();
		NumberGenerator gen=new NumberGenerator();
		for (;;)	{
			long number=gen.generate();
			CycleFinder.Match match=cf.newNumber(number);
			if (match!=null)	{
				System.out.println("Number "+number+" appeared in position "+match.previous+", but also in position "+match.current+". Therefore, there is a cycle of length "+(match.current-match.previous)+".");
				return;
			}
		}
	}
}
