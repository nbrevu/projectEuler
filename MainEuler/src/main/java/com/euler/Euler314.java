package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;

public class Euler314 {
	private final static int SIZE=50;
	
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
		result.add(new Segment(0,1));
		for (int i=1;i<=maxSize;++i) for (int j=1;j<=maxSize;++j) if (EulerUtils.areCoprime(i,j)) result.add(new Segment(j,i));
		result.add(new Segment(1,0));
		return result;
	}
	
	private static class ShapeFinder	{
		private final int n;
		private final Segment[] segments;
		private final Segment[] tmpHolder;
		private List<Segment> bestSolution;
		private double bestRatio;
		public ShapeFinder(int n)	{
			this.n=n;
			segments=getAllSegments(n).toArray(Segment[]::new);
			tmpHolder=new Segment[2*n];
			bestSolution=null;
			bestRatio=Double.NEGATIVE_INFINITY;
		}
		private void storeSolution(int currentSize)	{
			double length=0;
			double area=0;
			int curX=n;
			for (int i=0;i<currentSize;++i)	{
				Segment s=tmpHolder[i];
				length+=s.length;
				curX-=s.dx;
				area+=(curX+(0.5*s.dx))*s.dy;
			}
			double ratio=area/length;
			if ((bestSolution==null)||(ratio>bestRatio))	{
				bestSolution=new ArrayList<>();
				for (int i=0;i<currentSize;++i) bestSolution.add(tmpHolder[i]);
				bestRatio=ratio;
				System.out.println(bestRatio);
			}
		}
		public Pair<List<Segment>,Double> findBestSolution()	{
			findBestSolutionRecursive(n,n,0,0);
			return new Pair<>(bestSolution,bestRatio);
		}
		private void findBestSolutionRecursive(int x,int y,int writeIndex,int readIndex)	{
			for (int i=readIndex;i<segments.length;++i)	{
				Segment s=segments[i];
				if (s.dx*y>s.dy*x) break;
				int nextX=x-s.dx;
				int nextY=y-s.dy;
				if ((nextX<0)||(nextY<0)) continue;
				tmpHolder[writeIndex]=s;
				if ((nextX==0)&&(nextY==0)) storeSolution(1+writeIndex);
				else findBestSolutionRecursive(nextX,nextY,1+writeIndex,i);
			}
		}
	}
	
	/*
	 * This doesn't look super-difficult, but maybe the run time is excessive. It would be best to do a brute force attempt on smaller grids,
	 * to get an idea of what the shape looks like.
	 */
	public static void main(String[] args)	{
		ShapeFinder finder=new ShapeFinder(SIZE);
		Pair<List<Segment>,Double> solution=finder.findBestSolution();
		double result=solution.second;
		System.out.println(solution.first);
		System.out.println(result);
	}
}
