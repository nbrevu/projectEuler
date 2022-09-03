package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;

public class Euler314_6 {
	/*
	 * Some additional hypotheses to cull the search space:
	 * 1) The figure has symmetry with respect to the diagonals.
	 * 	This means that I might consider only slopes where dy>=dx.
	 * 	The last segment is very likely to be (1,1).
	 * 	The iteration can stop when I reach the diagonal. In our coordinates, this happens when dx+dy=N, I believe.
	 * 2) The first N/2 segments (approximately) are vertical ones.
	 * 3) The segments don't get *too* big. Maybe the biggest denominator is around sqrt(N).
	 * 4) The shape is likely to be something like a square where the corners are exactly rounded. Like a cantellated circle.
	 * 	Results for N=25 shows that it's a bit below that, although still within a distance less than 1 square.
	 */
	private final static int SIZE=250;
	
	// FINALLY, this combination of settings gives me the actual solution.
	/*-
	[<1,11>, <1,7>, <1,5>, <1,4>, <2,7>, <1,3>, <1,3>, <2,5>, <3,7>, <1,2>, <1,2>, <1,2>, <4,7>, <2,3>, <2,3>, <3,4>, <3,4>, <6,7>, <1,1>, <1,1>, <1,1>]
	132.52756425686314
	Elapsed 3101.0342495 seconds.
	 */
	private final static int STRAIGHT_SEGMENT=SIZE/2-3;
	private final static double MAX_DEVIATION=0.1;
	private final static double MIN_DEVIATION=-2.2;
	
	private static class Segment implements Comparable<Segment>	{
		public final int dx;
		public final int dy;
		public final double length;
		public Segment(int dx,int dy)	{
			this.dx=dx;
			this.dy=dy;
			length=Math.sqrt(dx*dx+dy*dy);
		}
		@Override
		public int compareTo(Segment other)	{
			return Integer.compare(dx*other.dy,dy*other.dx);
		}
		@Override
		public String toString()	{
			return String.format("<%d,%d>",dx,dy);
		}
	}
	
	private static NavigableSet<Segment> getAllSegments(int maxSize)	{
		NavigableSet<Segment> result=new TreeSet<>();
		for (int i=1;i<=maxSize;++i) for (int j=1;j<=i;++j) if (EulerUtils.areCoprime(i,j)) result.add(new Segment(j,i));
		return result;
	}
	
	private static class ShapeFinder	{
		private final static Segment DIAGONAL=new Segment(1,1);
		private final int n;
		private final int straightSegmentSize;
		private final double minDeviation;
		private final double maxDeviation;
		private final int radius;
		private final Segment[] segments;
		private final Segment[] tmpHolder;
		private List<Segment> bestSolution;
		private double bestRatio;
		public ShapeFinder(int n,int straightSegmentSize,double minDeviation,double maxDeviation)	{
			this.n=n;
			this.straightSegmentSize=straightSegmentSize;
			this.minDeviation=minDeviation;
			this.maxDeviation=maxDeviation;
			radius=n-straightSegmentSize;
			// segments=getAllSegments(IntMath.sqrt(n,RoundingMode.UP)).toArray(Segment[]::new);
			segments=getAllSegments(11).toArray(Segment[]::new);
			tmpHolder=new Segment[2*n];
			bestSolution=null;
			bestRatio=Double.NEGATIVE_INFINITY;
		}
		private void storeSolution(int currentSize)	{
			double length=2*straightSegmentSize;
			double area=n*straightSegmentSize;
			int curX=n;
			int curY=straightSegmentSize;
			for (int i=0;i<currentSize;++i)	{
				Segment s=tmpHolder[i];
				length+=s.length;
				curX-=s.dx;
				curY+=s.dy;
				area+=(curX+(0.5*s.dx))*s.dy;
			}
			if (curX==curY+1)	{
				Segment s=DIAGONAL;
				length+=s.length;
				curX-=s.dx;
				area+=(curX+(0.5*s.dx))*s.dy;
			}
			for (int i=currentSize-1;i>=0;--i)	{
				Segment reverse=tmpHolder[i];
				length+=reverse.length;
				curX-=reverse.dy;
				area+=(curX+(0.5*reverse.dy))*reverse.dx;
			}
			double ratio=area/length;
			if ((bestSolution==null)||(ratio>bestRatio))	{
				bestSolution=new ArrayList<>();
				for (int i=0;i<currentSize;++i) bestSolution.add(tmpHolder[i]);
				bestRatio=ratio;
				System.out.println(bestRatio+" => "+bestSolution);
			}
		}
		public Pair<List<Segment>,Double> findBestSolution()	{
			findBestSolutionRecursive(n,straightSegmentSize,0,0);
			return new Pair<>(bestSolution,bestRatio);
		}
		private void findBestSolutionRecursive(int x,int y,int writeIndex,int readIndex)	{
			for (int i=readIndex;i<segments.length;++i)	{
				Segment s=segments[i];
				if (s.dx*(n-y)>s.dy*x) break;
				int nextX=x-s.dx;
				int nextY=y+s.dy;
				if ((nextX<0)||(nextY>n)) continue;
				int xx=nextX-straightSegmentSize;
				int yy=nextY-straightSegmentSize;
				double dist=Math.sqrt((xx*xx)+(yy*yy));
				double deviation=dist-radius;
				if ((deviation>maxDeviation)||(deviation<minDeviation)) continue;
				tmpHolder[writeIndex]=s;
				if ((nextX==nextY)||(nextX==nextY+1)) storeSolution(1+writeIndex);
				else findBestSolutionRecursive(nextX,nextY,1+writeIndex,i);
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ShapeFinder finder=new ShapeFinder(SIZE,STRAIGHT_SEGMENT,MIN_DEVIATION,MAX_DEVIATION);
		Pair<List<Segment>,Double> solution=finder.findBestSolution();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		double result=solution.second;
		System.out.println(solution.first);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}