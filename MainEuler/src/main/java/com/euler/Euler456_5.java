package com.euler;

import java.util.Arrays;

import com.euler.common.EulerUtils;

public class Euler456_5 {
	/*
	Algoritmo O(n^2):
	16170: 176148929514
	16171: 176181857060
	10000: 41657749588
	8000: 21327672464
	6000: 8995854868
	7000: 14287818478
	6500: 11436659276
	6250: 10167667772
	6375: 10789872956
	6438: 11112862334
	6469: 11274571140
	6484: 11353193230
	6476: 11311305660
	6472: 11290221632
	6470: 11279794764
	6471: 11284969416
	
	Algoritmo O(n^3):
	16170: 176148889185
	16171: ni lo intento, va a salir mal :P.
	10000: 41657739664	<- Ya tengo algo con lo que trabajar :).
	8000: 21327668487
	6000: 8995854868
	7000: 14287815011
	6500: 11436656079
	6250: 10167667772
	6375: 10789872956
	6438: 11112862334
	6469: 11274571140
	6484: 11353190037
	6476: 11311302470
	6472: 11290218445
	6470: 11279791578
	6471: 11284966230
	 */
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
			return (raOther.x==x)&&(raOther.y==y);//&&(raOther.quadrant==quadrant);
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
	
	// The first "repeated" angle is in position 16171. Is that the culprit of the wrong result?
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int points=6471;
		RationalAngle[] angles=new RationalAngle[points];
		RationalAngle[] oppAngles=new RationalAngle[points];
		PointGenerator gen=new PointGenerator();
		for (int i=0;i<points;++i) angles[i]=gen.next();
		Arrays.sort(angles);
		for (int i=0;i<points;++i)	{
			oppAngles[i]=angles[i].opposite();
			if (oppAngles[i].compareTo(angles[i])<0) oppAngles[i]=null;
		}
		long count=0;
		for (int i=0;;++i) {
			RationalAngle a=angles[i];
			RationalAngle oA=oppAngles[i];
			if (oA==null) break;
			for (int j=i+1;j<points;++j)	{
				RationalAngle b=angles[j];
				RationalAngle oB=oppAngles[j];
				if (a.equals(b)) continue;
				else if (b.compareTo(oA)>=0) break;
				for (int k=j+1;k<points;++k)	{
					RationalAngle c=angles[k];
					if (c.compareTo(oA)<=0) continue;
					else if ((oB==null)||(c.compareTo(oB)<0)) ++count;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(count);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
