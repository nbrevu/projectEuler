package com.euler;

import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.math.IntMath;

public class Euler736_2 {
	private final static int LIMIT=IntMath.pow(10,7);
	
	private static class LatticePoint implements Comparable<LatticePoint>	{
		public final long x;
		public final long y;
		private final String sequence;
		public LatticePoint(long x,long y)	{
			this(x,y,"");
		}
		private LatticePoint(long x,long y,String sequence)	{
			this.x=x;
			this.y=y;
			this.sequence=sequence;
		}
		public boolean isOdd()	{
			return (sequence.length()%2)==0;	// Yes, 0, not 1.
		}
		public boolean isOddAndFinalValue()	{
			return isOdd()&&(x==y);
		}
		public boolean smallEnough()	{
			return (x<LIMIT)&&(y<LIMIT);
		}
		public LatticePoint r()	{
			return new LatticePoint(x+1,y+y,sequence+"r");
		}
		public LatticePoint s()	{
			return new LatticePoint(x+x,y+1,sequence+"s");
		}
		@Override
		public int hashCode()	{
			return Long.hashCode(x)+Long.hashCode(y)+sequence.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			LatticePoint lpOther=(LatticePoint)other;
			return (isOdd()==lpOther.isOdd())&&(x==lpOther.x)&&(y==lpOther.y);
		}
		@Override
		public int compareTo(LatticePoint other) {
			int compareX=Long.compare(x,other.x);
			if (compareX!=0) return compareX;
			int compareY=Long.compare(y,other.y);
			if (compareY!=0) return compareY;
			return Boolean.compare(isOdd(),other.isOdd());
		}
		@Override
		public String toString()	{
			return String.format("%d,%d,%s",x,y,sequence);
		}
	}
	
	public static void main(String[] args)	{
		/*-
		 * Let f(x)=smallest final value from an odd sequence when starting with (x,2x).
		f(1)=(4,4,ss). -> Caso particular (desequilibrio r/s).
		f(2)=(36,36,rrsrss). -> 3r+3s. 36=(2*2)*2^3 + 4.
		f(3)=(204,204,rrrssrsrss). -> 5r+5s. 204=(3*2)*2^5 + 12.
		f(4)=(8420,8420,rrrsrsrrsrsrsrsssrss). -> 10r+10s. 8420=(4*2)*2^10 + 228.
		f(5)=(1296,1296,rrrrsrrssrssss). -> 7r+7s. 1296=(5*2)*2^7 + 16.
		f(6)=(6180,6180,rrrrrsrrsssrsssrss). -> 9r+9s. 6180=(6*2)*2^9 + 36.
		f(7)=(57476,57476,rrrrrrsrsrsrrssrsssssrss). 12r+12s. 57476=(7*2)*2^12 + 132.
		f(8)=(131216,131216,rrrrrrrsrsrrsssssrrssrssss). 13r+13s. 131216=(8*2)*2^13 + 144.
		f(9)=(18448,18448,rrrrrrrrrssssssrssss). 10r+10s. 18448=(9*2)*2^10 + 16.
		f(10)=(81956,81956,rrrrrrrrrrsssssssrsssrss). 12r+12s. 81956=(10*2)*2^12 + 36.
		 */
		for (int i=11;i<=11;++i)	{
			NavigableSet<LatticePoint> values=new TreeSet<>();
			values.add(new LatticePoint(i,i+i));
			LatticePoint solution;
			for (;;)	{
				LatticePoint lp=values.pollFirst();
				LatticePoint lpr=lp.r();
				// System.out.println(String.format("%s.r()=%s.",lp,lpr));
				if (lpr.isOddAndFinalValue())	{
					solution=lpr;
					break;
				}
				LatticePoint lps=lp.s();
				// System.out.println(String.format("%s.s()=%s.",lp,lps));
				if (lps.isOddAndFinalValue())	{
					solution=lps;
					break;
				}
				if (lpr.smallEnough()) values.add(lpr);
				if (lps.smallEnough()) values.add(lps);
			}
			System.out.println(String.format("f(%d)=(%s).",i,solution.toString()));
		}
	}
}
