package com.euler;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;

public class Euler456_3 {
	private final static int POINTS=8;
	
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
		public IndividualAngleInfo(int count,long cumulativeCount,int indexPlusPi)	{
			this.count=count;
			this.cumulativeCount=cumulativeCount;
			this.indexPlusPi=indexPlusPi;
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
				int thisValue=pointAnglesMap.get(thisAngle);
				cumSum+=thisValue;
				int opposite=Arrays.binarySearch(angles,thisAngle.opposite());
				if (opposite<0) opposite=-1-opposite;
				if (opposite<i) opposite=result.length-1;
				else --opposite;
				result[i]=new IndividualAngleInfo(thisValue,cumSum,opposite);
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
		public long getIntervalCount(int includedMin,int includedMax)	{
			return getCumulativeValue(includedMax)-getCumulativeValue(includedMin-1);
		}
		public int getOppositeIndex(int index)	{
			return angleInfo[index].indexPlusPi;
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * OK, this looks promising. Next steps of the algorithm:
		 * 1) Calculate g(0), which must be done iteratively.
		 * 2) Iterate and calculate g(i+1)-g(i) with the proposed difference method.
		 * 3) Stop when g(i)=0.
		 * That's it?
		 */
		long tic=System.nanoTime();
		CompleteAngleInfo info=CompleteAngleInfo.getAngleInfo(POINTS);
		long result=0l;
		long g=0;
		int kI=info.getOppositeIndex(0);
		for (int j=1;j<=kI;++j) g+=info.getValue(j)*(info.getCumulativeValue(info.getOppositeIndex(j))-info.getCumulativeValue(kI));
		result+=g;
		for (int i=1;i<info.getSize();++i)	{
			int prevKi=kI;
			kI=info.getOppositeIndex(i);
			long t1=info.getCumulativeValue(kI)-info.getCumulativeValue(prevKi)-info.getValue(i);
			long t2=info.getIntervalCount(i+1,kI)*info.getCumulativeValue(i)-info.getIntervalCount(i,prevKi)*info.getCumulativeValue(i-1);
			g+=t1-t2;
			// D'oh. Algo estoy haciendo mal. Revisar las fórmulas.
			if (g<0) throw new RuntimeException("Mierda.");
			else if (g==0) break;
			result+=g;
		}
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
