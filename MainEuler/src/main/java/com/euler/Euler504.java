package com.euler;

import java.math.RoundingMode;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;

public class Euler504 {
	// Using https://en.wikipedia.org/wiki/Pick's_theorem.
	// So, another super easy one that I get in the first try :).
	private final static int LIMIT=100;
	
	private final static boolean isSquare(int in)	{
		int sq=IntMath.sqrt(in,RoundingMode.FLOOR);
		return (sq*sq)==in;
	}
	
	private static class LatticeQuadrilateral	{
		public int a,b,c,d;
		public LatticeQuadrilateral()	{
			a=0;
			b=0;
			c=0;
			d=0;
		}
		private int getAreaTimes2()	{
			return (a+c)*(b+d);
		}
		private static int getLatticePointsInSegment(int p1,int p2)	{
			return (int)(EulerUtils.gcd(p1,p2)-1);
		}
		private int getBoundaryPoints()	{
			return 4+getLatticePointsInSegment(a,b)+getLatticePointsInSegment(b,c)+getLatticePointsInSegment(c,d)+getLatticePointsInSegment(d,a);
		}
		public int getInteriorPoints()	{
			// TODO!
			// 2A=2i+b-2
			int doubleResult=getAreaTimes2()+2-getBoundaryPoints();
			if ((doubleResult%2)!=0) throw new IllegalArgumentException("La has cagado mucho, tío.");
			return doubleResult/2;
		}
	}
	
	public static void main(String[] args)	{
		int count=0;
		LatticeQuadrilateral quad=new LatticeQuadrilateral();
		for (quad.a=1;quad.a<=LIMIT;++quad.a) for (quad.b=1;quad.b<=LIMIT;++quad.b) for (quad.c=1;quad.c<=LIMIT;++quad.c) for (quad.d=1;quad.d<=LIMIT;++quad.d)	{
			int points=quad.getInteriorPoints();
			if (isSquare(points)) ++count;
		}
		System.out.println(count);
	}
}
