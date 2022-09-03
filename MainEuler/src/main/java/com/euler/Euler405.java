package com.euler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.EulerUtils.Pair;
import com.google.common.math.LongMath;

public class Euler405 {
	private final static int MAX_SIZE=12;
	private final static long DIMENSION=LongMath.pow(2l,MAX_SIZE);
	
	private static class Coordinate	{
		private final long x;
		private final long y;
		private final int hashCode;
		public Coordinate(long x,long y)	{
			this.x=x;
			this.y=y;
			hashCode=(int)(DIMENSION*y+x);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Coordinate cOther=(Coordinate)other;
			return (x==cOther.x)&&(y==cOther.y);
		}
		@Override
		public String toString()	{
			return "("+format(x)+","+format(y)+")";
		}
		private static String format(long value)	{
			if (value==0) return "0";
			int trailing=Long.numberOfTrailingZeros(value);
			long residue=value>>trailing;
			if (residue==1) return "2^"+trailing;
			else return residue+"·2^"+trailing;
		}
	}
	
	private static class Rectangle	{
		private final long leftmostX;
		private final long bottomY;
		private final boolean isHorizontal;
		public Rectangle(long leftmostX,long bottomY,boolean isHorizontal)	{
			this.leftmostX=leftmostX;
			this.bottomY=bottomY;
			this.isHorizontal=isHorizontal;
		}
		public Pair<Rectangle[],Coordinate[]> divide(long size)	{
			Rectangle[] rectangles=new Rectangle[4];
			Coordinate[] newCorners=new Coordinate[6];
			if (isHorizontal)	{
				long left1=leftmostX+size;
				long left3=leftmostX+3*size;
				long bot1=bottomY+size;
				long bot2=bottomY+2*size;
				rectangles[0]=new Rectangle(leftmostX,bottomY,false);
				rectangles[1]=new Rectangle(left1,bottomY,true);
				rectangles[2]=new Rectangle(left1,bot1,true);
				rectangles[3]=new Rectangle(left3,bottomY,false);
				newCorners[0]=new Coordinate(left1,bottomY);
				newCorners[1]=new Coordinate(left1,bot1);
				newCorners[2]=new Coordinate(left1,bot2);
				newCorners[3]=new Coordinate(left3,bottomY);
				newCorners[4]=new Coordinate(left3,bot1);
				newCorners[5]=new Coordinate(left3,bot2);
			}	else	{
				long bot1=bottomY+size;
				long bot3=bottomY+3*size;
				long left1=leftmostX+size;
				long left2=leftmostX+2*size;
				rectangles[0]=new Rectangle(leftmostX,bottomY,true);
				rectangles[1]=new Rectangle(leftmostX,bot1,false);
				rectangles[2]=new Rectangle(left1,bot1,false);
				rectangles[3]=new Rectangle(leftmostX,bot3,true);
				newCorners[0]=new Coordinate(leftmostX,bot1);
				newCorners[1]=new Coordinate(left1,bot1);
				newCorners[2]=new Coordinate(left2,bot1);
				newCorners[3]=new Coordinate(leftmostX,bot3);
				newCorners[4]=new Coordinate(left1,bot3);
				newCorners[5]=new Coordinate(left2,bot3);
			}
			return new Pair<>(rectangles,newCorners);
		}
	}
	
	public static void main(String[] args)	{
		Set<Coordinate> pendingCorners=new HashSet<>();
		List<Rectangle> currentRectangles=new ArrayList<>();
		currentRectangles.add(new Rectangle(0l,0l,true));
		long thisDimension=DIMENSION;
		long counter=0;
		for (int i=1;i<=MAX_SIZE;++i)	{
			thisDimension/=2;
			List<Rectangle> nextRectangles=new ArrayList<>(currentRectangles.size());
			for (Rectangle rect:currentRectangles)	{
				Pair<Rectangle[],Coordinate[]> children=rect.divide(thisDimension);
				for (Rectangle child:children.first) nextRectangles.add(child);
				for (Coordinate newCorner:children.second) if (pendingCorners.contains(newCorner))	{
					++counter;
					pendingCorners.remove(newCorner);
				}	else pendingCorners.add(newCorner);
			}
			System.out.println("\tTengo "+pendingCorners.size()+" esquinas almacenadas...");
			currentRectangles=nextRectangles;
			System.out.println("f("+i+")="+counter+".");
		}
	}
}
