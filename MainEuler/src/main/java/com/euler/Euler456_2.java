package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class Euler456_2 {
	private final static int POINTS=2_000_000;
	
	private static class RationalAngle implements Comparable<RationalAngle>	{
		public final int x;
		public final int y;
		public final int quadrant;
		/*
		 * Quadrant:
		 * 0 -> x+ axis.
		 * 1 -> first quadrant.
		 * 2 -> y+ axis.
		 * 3 -> second quadrant.
		 * 4 -> x- axis.
		 * 5 -> third quadrant.
		 * 6 -> y- axis.
		 * 7 -> fourth quadrant.
		 * It's assumed that (x,y) != (0,0).
		 */
		private static int signum(int x)	{
			if (x<0) return -1;
			else if (x>0) return 1;
			else return 0;
		}
		private static int getQuadrant(int x,int y)	{
			int sx=signum(x);
			int sy=signum(y);
			switch (sx)	{
				case -1:
					/*
					switch (sy)	{
						case -1: return 5;
						case 0: return 4;
						case 1: return 3;
					}
					*/
					return 4-sy;
				case 0:
					switch (sy)	{
						case -1: return 6;
						case 1: return 2;
					}
				case 1:
					/*
					switch (sy)	{
						case -1: return 7;
						case 0: return 0;
						case 1: return 1;
					}
					*/
					return (sy+8)%8;
			}
			throw new IllegalArgumentException();	// (0,0) is not allowed.
		}
		public RationalAngle(int x,int y)	{
			int g=EulerUtils.gcd(Math.abs(x),Math.abs(y));
			this.x=x/g;
			this.y=y/g;
			quadrant=getQuadrant(this.x,this.y);
		}
		@Override
		public int compareTo(RationalAngle other) {
			int qDiff=quadrant-other.quadrant;
			if (qDiff!=0) return qDiff;
			// At this point we know that the quadrant/axis is the same.
			if ((quadrant%2)==0) return 0;	// Both points are in an axis. They represent the same angle.
			// Compare tg(angle). We need to compare (y/x) with (y'/x') -> y*x' with y'*x.
			return y*other.x-other.y*x;	// Since ranges are smaller than [-2^14, 2^14], no overflows can happen.
		}
		@Override
		public int hashCode()	{
			return x+y+quadrant;
		}
		@Override
		public boolean equals(Object other)	{
			RationalAngle raOther=(RationalAngle)other;
			return (raOther.x==x)&&(raOther.y==y)&&(raOther.quadrant==quadrant);
		}
		public RationalAngle opposite()	{
			return new RationalAngle(-x,-y);
		}
	}
	
	private static class PointGenerator	{
		private int x;
		private int y;
		public PointGenerator()	{
			x=1;
			y=1;
		}
		public RationalAngle next()	{
			x*=1248;
			x%=32323;
			y*=8421;
			y%=30103;
			return new RationalAngle(x-16161,y-15051);
		}
	}
	
	private static class OppositeAngle	{
		@SuppressWarnings("unused")
		public final RationalAngle angle;
		public final int position;
		public OppositeAngle(RationalAngle angle,int position)	{
			this.angle=angle;
			this.position=position;
		}
	}
	
	private static class SortedPointList	{
		private final RationalAngle[] pointAngles;
		private final OppositeAngle[] oppositeAngles;
		public SortedPointList(int size)	{
			PointGenerator gen=new PointGenerator();
			SortedMultiset<RationalAngle> pointAnglesSet=TreeMultiset.create();
			for (int i=0;i<POINTS;++i) pointAnglesSet.add(gen.next());
			pointAngles=pointAnglesSet.toArray(RationalAngle[]::new);
			oppositeAngles=new OppositeAngle[size];
			for (int i=0;i<size;++i)	{
				RationalAngle otherAngle=pointAngles[i].opposite();
				int position=Arrays.binarySearch(pointAngles,otherAngle);
				oppositeAngles[i]=new OppositeAngle(otherAngle,position);
			}
		}
		/*
		 * ZUTUN! I think I'm going to need:
		 * - A method that gives me the list of indices with the same exact angle.
		 * - A method that gives me the first angle after half a circumference away from a given starting point.
		 * - A method that gives me the last angle before half a circumference away from a given starting point.
		 * - Then, of course, I need to refine the iteration scheme.
		 */
	}
	
	public static void main(String[] args)	{
		/*
		 * This is doable in O(n^2log(n)). I'll try tomorrow. 4 seconds just to generate the array... but it's peanuts compared to the real run time.
		 * It might be a good idea to use rational values for the angles (i.e. storing the ratios and being very careful about signs) to avoid
		 * precision errors. This is promising but it's far from being completed. 
		 */
		long tic=System.nanoTime();
		SortedPointList points=new SortedPointList(POINTS);
		System.out.println(points.oppositeAngles.length);
		for (int i=0;i<points.oppositeAngles.length;++i)	{
			int otherIndex=points.oppositeAngles[i].position;
			System.out.println("Opuesto de "+i+": "+otherIndex+".");
			if (otherIndex<0) break;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
