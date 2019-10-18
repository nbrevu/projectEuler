package com.euler;

import java.util.SortedSet;
import java.util.TreeSet;

import com.euler.common.Timing;
import com.google.common.math.LongMath;

public class Euler140 {
	private final static int GOAL=30;
	
	private final static long TENTATIVE_LIMIT=LongMath.pow(10l,13);
	
	private static class QuadraticSolution	{
		// public final static QuadraticSolution[] BASE_SOLUTIONS=new QuadraticSolution[] {new QuadraticSolution(2l,-7l),new QuadraticSolution(0l,-1l),new QuadraticSolution(0l,1l),new QuadraticSolution(-4l,5l),new QuadraticSolution(-3l,2l),new QuadraticSolution(-3l,-2l)};
		public final static QuadraticSolution[] BASE_SOLUTIONS=new QuadraticSolution[] {new QuadraticSolution(2l,-7l),new QuadraticSolution(152l,343l),new QuadraticSolution(296l,665l),new QuadraticSolution(2l,7l),new QuadraticSolution(5l,14l),new QuadraticSolution(21l,50l)};
		public final long x;
		public final long y;
		public QuadraticSolution(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		public QuadraticSolution next()	{
			// return new QuadraticSolution(-9*x-4*y-14,-20*x-9*y-28);
			return new QuadraticSolution(161*x+72*y+224,360*x+161*y+504);
		}
		public boolean isWithinLimits(long limit)	{
			return (Math.abs(x)<limit)&&(Math.abs(y)<limit);
		}
	}
	
	private static long solve()	{
		SortedSet<Long> solutions=new TreeSet<>();
		for (QuadraticSolution q:QuadraticSolution.BASE_SOLUTIONS) while (q.isWithinLimits(TENTATIVE_LIMIT))	{
			solutions.add(q.x);
			q=q.next();
		}
		long result=0;
		int i=0;
		for (long l:solutions)	{
			result+=l;
			++i;
			if (i>=GOAL) return result;
		}
		throw new RuntimeException("Not enough solutions");
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler140::solve);
	}
}
