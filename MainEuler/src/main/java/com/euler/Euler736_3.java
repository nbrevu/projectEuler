package com.euler;

import java.math.BigInteger;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Euler736_3 {
	private final static BigInteger LIMIT=BigInteger.TEN.pow(6);
	
	private static class LatticePoint implements Comparable<LatticePoint>	{
		public final BigInteger x;
		public final BigInteger y;
		private final boolean isOdd;
		public LatticePoint(long x,long y)	{
			this(BigInteger.valueOf(x),BigInteger.valueOf(y),true);
		}
		private LatticePoint(BigInteger x,BigInteger y,boolean isOdd)	{
			this.x=x;
			this.y=y;
			this.isOdd=isOdd;
		}
		public boolean isOddFinalValue()	{
			return isOdd&&(x.equals(y));
		}
		public boolean smallEnough()	{
			return (x.compareTo(LIMIT)<0)&&(y.compareTo(LIMIT)<0);
		}
		public LatticePoint r()	{
			return new LatticePoint(x.add(BigInteger.ONE),y.add(y),!isOdd);
		}
		public LatticePoint s()	{
			return new LatticePoint(x.add(x),y.add(BigInteger.ONE),!isOdd);
		}
		@Override
		public int hashCode()	{
			return x.hashCode()+y.hashCode()+Boolean.hashCode(isOdd);
		}
		@Override
		public boolean equals(Object other)	{
			LatticePoint lpOther=(LatticePoint)other;
			return (isOdd==lpOther.isOdd)&&x.equals(lpOther.x)&&y.equals(lpOther.y);
		}
		@Override
		public int compareTo(LatticePoint other) {
			int compareX=x.compareTo(other.x);
			if (compareX!=0) return compareX;
			int compareY=y.compareTo(other.y);
			if (compareY!=0) return compareY;
			return Boolean.compare(isOdd,other.isOdd);
		}
		@Override
		public String toString()	{
			return String.format("%d,%d,%s",x,y,Boolean.toString(isOdd));
		}
	}
	
	public static void main(String[] args)	{
		/*-
		 * Let f(x)=smallest final value from an odd sequence when starting with (x,2x).
		 * 
		 * f(1)=4
		 * f(2)=36
		 * f(3)=204
		 * f(4)=8420
		 * f(5)=1296, so f(x) IT'S NOT A GROWING FUNCTION
		 * f(6)=6180
		 * f(7)=57476
		 * f(8)=131216
		 * f(9)=18448
		 * f(10)=81956
		 * f(11)=Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
		 */
		NavigableSet<LatticePoint> values=new TreeSet<>();
		values.add(new LatticePoint(11,22));
		LatticePoint solution;
		for (;;)	{
			LatticePoint lp=values.pollFirst();
			LatticePoint lpr=lp.r();
			// System.out.println(String.format("%s.r()=%s.",lp,lpr));
			if (lpr.isOddFinalValue())	{
				solution=lpr;
				break;
			}
			LatticePoint lps=lp.s();
			// System.out.println(String.format("%s.s()=%s.",lp,lps));
			if (lps.isOddFinalValue())	{
				solution=lps;
				break;
			}
			if (lpr.smallEnough()) values.add(lpr);
			if (lps.smallEnough()) values.add(lps);
		}
		System.out.println(solution.x);
	}
}
