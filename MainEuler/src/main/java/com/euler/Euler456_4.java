package com.euler;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;

public class Euler456_4 {
	/*
	 * YEEEEEEEEEEES, finally it returns the correct value for the proposed case. Now I can start deconstructing this algorithm into something
	 * workable with a main O(n) loop (the whole algorithm is still O(n*log n) because of the initial sorting).
	 */
	private final static int POINTS=2000000;
	
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
	
	private static class IndividualAngleInfo	{
		private final int count;
		private final long cumulativeCount;
		private final int indexPlusPi;
		private final boolean isOppositeExact;
		public IndividualAngleInfo(int count,long cumulativeCount,int indexPlusPi,boolean isOppositeExact)	{
			this.count=count;
			this.cumulativeCount=cumulativeCount;
			this.indexPlusPi=indexPlusPi;
			this.isOppositeExact=isOppositeExact;
		}
	}
	
	private static class CompleteAngleInfo	{
		private final IndividualAngleInfo[] angleInfo;
		private CompleteAngleInfo(IndividualAngleInfo[] angleInfo)	{
			this.angleInfo=angleInfo;
		}
		private static CompleteAngleInfo getAngleInfo(int points)	{
			PointGenerator gen=new PointGenerator();
			SortedMap<RationalAngle,Integer> pointAnglesMap=new TreeMap<>();
			for (int i=0;i<points;++i) EulerUtils.increaseCounter(pointAnglesMap,gen.next());
			RationalAngle[] angles=pointAnglesMap.keySet().toArray(RationalAngle[]::new);
			IndividualAngleInfo[] result=new IndividualAngleInfo[angles.length];
			long cumSum=0;
			for (int i=0;i<result.length;++i)	{
				RationalAngle thisAngle=angles[i];
				RationalAngle otherAngle=thisAngle.opposite();
				int thisValue=pointAnglesMap.get(thisAngle);
				cumSum+=thisValue;
				boolean isOppositeExact=false;
				int opposite=Arrays.binarySearch(angles,otherAngle);
				if (opposite<0) opposite=-1-opposite;
				else isOppositeExact=true;
				if (opposite<i) opposite=result.length-1;
				else --opposite;
				/*
				 * These checks are in place to ensure that my conditions are calculated correctly... and apparently they are.
				RationalAngle maxOppositeAngle=angles[opposite];
				if (thisAngle.opposite().equals(maxOppositeAngle)) throw new IllegalArgumentException("Mal.");
				else	{
					double a1=Math.atan2(thisAngle.y,thisAngle.x);
					double a2=Math.atan2(maxOppositeAngle.y,maxOppositeAngle.x);
					if (a1<0) a1+=2*Math.PI;
					if (a2<0) a2+=2*Math.PI;
					if (a2<a1) throw new IllegalArgumentException("Mal 2.");
					else if (a2-a1>=Math.PI) throw new IllegalArgumentException("Mal 3.");
				}
				if (opposite!=result.length-1)	{
					RationalAngle nextOpposite=angles[1+opposite];
					if (!thisAngle.opposite().equals(nextOpposite))	{
						double a1=Math.atan2(thisAngle.y,thisAngle.x);
						double a2=Math.atan2(nextOpposite.y,nextOpposite.x);
						if (a1<0) a1+=2*Math.PI;
						if (a2<0) a2+=2*Math.PI;
						if (a2<a1+Math.PI)	{
							throw new IllegalArgumentException("Mal 4.");
						}
					}
				}
				*/
				result[i]=new IndividualAngleInfo(thisValue,cumSum,opposite,isOppositeExact);
			}
			return new CompleteAngleInfo(result);
		}
		public int getSize()	{
			return angleInfo.length;
		}
		public int getValue(int index)	{
			return angleInfo[index].count;
		}
		public long getCumulativeValue(int index)	{
			if (index<0) return 0;
			return angleInfo[Math.min(index,angleInfo.length)].cumulativeCount;
		}
		public long getCumulativeValue(int index,int indexToCheck)	{
			if (index<0) return 0;
			if (angleInfo[indexToCheck].isOppositeExact) ++index;
			return angleInfo[Math.min(index,angleInfo.length)].cumulativeCount;
		}
		public int getOppositeIndex(int index)	{
			return angleInfo[index].indexPlusPi;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CompleteAngleInfo info=CompleteAngleInfo.getAngleInfo(POINTS);
		long result=0l;
		System.out.println(info.getSize());
		for (int i=0;i<info.getSize();++i)	{
			int kI=info.getOppositeIndex(i);
			if (kI>=info.getSize()-1) break;
			for (int j=i+1;j<=kI;++j)	{
				int kJ=info.getOppositeIndex(j);
				if (kJ>info.getSize()-1) break;
				/*
				 * Scheiße, bad results for N=40000 :(. The results for 8 and 600 are correct, though.
				 * Interestingly, for 8 and 600 there aren't "repeated" angles.
				 * Oooook. The problem comes from the "opposite" angles, and I THINK I got it.
				 * I believe that the problem, kind of unexpectedly, is in info.getCumulativeValue(kI). We aren't subtracting the case where
				 * kI is exactly the opposite of i!! Fuck. I need some more information. Maybe just a "isOppositeExact" boolean...
				 */
				result+=(long)info.getValue(i)*(long)info.getValue(j)*(long)(info.getCumulativeValue(kJ)-info.getCumulativeValue(kI,i));
			}
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
