package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Euler321 {
	// We solve the modified Pell equation x^2 - 8�y^2 = -7. For each solution (x,y), y-1 is the number we seek.
	
	// I certainly didn't expect this to be right at the first try...
	private final static int GOAL=40;
	
	private static class BasePellSolution	{
		public final long x;
		public final long y;
		private final BasePellSolution initial;
		public BasePellSolution()	{
			x=3l;
			y=1l;
			initial=this;
		}
		private BasePellSolution(long x,long y,BasePellSolution initial)	{
			this.x=x;
			this.y=y;
			this.initial=initial;
		}
		public BasePellSolution nextSolution()	{
			long newX=initial.x*x+8*initial.y*y;
			long newY=initial.x*y+initial.y*x;
			return new BasePellSolution(newX,newY,initial);
		}
		@Override
		public String toString()	{
			return "(x="+x+", y="+y+")";
		}
	}
	
	private static class SpecificPellSolution implements Comparable<SpecificPellSolution>	{
		public final long x;
		public final long y;
		public SpecificPellSolution(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		public SpecificPellSolution nextSolution(BasePellSolution baseSol)	{
			long newX=baseSol.x*x+8*baseSol.y*y;
			long newY=baseSol.x*y+baseSol.y*x;
			return new SpecificPellSolution(newX,newY);
		}
		@Override
		public int hashCode()	{
			return (int)(31*x+y);
		}
		@Override
		public boolean equals(Object other)	{
			SpecificPellSolution spsOther=(SpecificPellSolution)other;
			return (x==spsOther.x)&&(y==spsOther.y);
		}
		@Override
		public int compareTo(SpecificPellSolution other) {
			long diff=x-other.x;
			if (diff!=0) return (int)Math.signum(diff);
			return (int)Math.signum(y-other.y);
		}
		@Override
		public String toString()	{
			return "(x="+x+", y="+y+")";
		}
	}
	
	public static void main(String[] args)	{
		BasePellSolution pell=new BasePellSolution();
		List<BasePellSolution> pellSols=new ArrayList<>();
		pellSols.add(pell);
		for (int i=0;i<3;++i) pellSols.add(pellSols.get(i).nextSolution());
		NavigableSet<SpecificPellSolution> queue=new TreeSet<>();
		queue.add(new SpecificPellSolution(5,2));
		queue.add(new SpecificPellSolution(11,4));
		queue.add(new SpecificPellSolution(31,11));
		queue.add(new SpecificPellSolution(65,23));
		queue.add(new SpecificPellSolution(181,64));
		long sum=0;
		for (int i=0;i<GOAL;++i)	{
			SpecificPellSolution specific=queue.pollFirst();
			sum+=specific.y-1;
			for (BasePellSolution sol:pellSols) queue.add(specific.nextSolution(sol));
		}
		System.out.println(sum);
	}
}
