package com.euler;

import java.util.NavigableSet;
import java.util.TreeSet;

public class Euler736 {
	private static class LatticePoint implements Comparable<LatticePoint>	{
		public final long x;
		public final long y;
		private final boolean isOdd;
		public LatticePoint(long x,long y)	{
			this(x,y,true);
		}
		private LatticePoint(long x,long y,boolean isOdd)	{
			this.x=x;
			this.y=y;
			this.isOdd=isOdd;
		}
		public boolean isOddFinalValue()	{
			return isOdd&&(x==y);
		}
		public LatticePoint r()	{
			return new LatticePoint(x+1,y+y,!isOdd);
		}
		public LatticePoint s()	{
			return new LatticePoint(x+x,y+1,!isOdd);
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(x)+Long.hashCode(y)+Boolean.hashCode(isOdd);
		}
		@Override
		public boolean equals(Object other)	{
			LatticePoint lpOther=(LatticePoint)other;
			return (x==lpOther.x)&&(y==lpOther.y)&&(isOdd==lpOther.isOdd);
		}
		@Override
		public int compareTo(LatticePoint other) {
			int compareX=Long.compare(x,other.x);
			if (compareX!=0) return compareX;
			int compareY=Long.compare(y,other.y);
			if (compareY!=0) return compareY;
			return Boolean.compare(isOdd,other.isOdd);
		}
		@Override
		public String toString()	{
			return String.format("%d,%d,%s",x,y,Boolean.toString(isOdd));
		}
	}
	
	public static void main(String[] args)	{
		NavigableSet<LatticePoint> values=new TreeSet<>();
		values.add(new LatticePoint(3,6));
		LatticePoint solution;
		for (;;)	{
			LatticePoint lp=values.pollFirst();
			LatticePoint lpr=lp.r();
			System.out.println(String.format("%s.r()=%s.",lp,lpr));
			if (lpr.isOddFinalValue())	{
				solution=lpr;
				break;
			}
			LatticePoint lps=lp.s();
			System.out.println(String.format("%s.s()=%s.",lp,lps));
			if (lps.isOddFinalValue())	{
				solution=lps;
				break;
			}
			values.add(lpr);
			values.add(lps);
		}
		System.out.println(solution.x);
	}
}
