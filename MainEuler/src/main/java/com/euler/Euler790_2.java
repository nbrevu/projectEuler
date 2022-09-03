package com.euler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Euler790_2 {
	private final static long SIZE=50515093l;
	private final static long SEED=290797l;
	private final static int HOUR_LIMIT=12;
	private final static int ITERATIONS=100000;
	
	private static class RectangleIntersectionResult	{
		public final Rectangle intersection;
		public final List<Rectangle> nonIntersectingParts;
		public RectangleIntersectionResult(Rectangle intersection,List<Rectangle> nonIntersectingParts)	{
			this.intersection=intersection;
			this.nonIntersectingParts=nonIntersectingParts;
		}
	}
	
	private static class Rectangle	{
		public final long x0;
		public final long x1;
		public final long y0;
		public final long y1;
		public Rectangle(long xA,long xB,long yA,long yB)	{
			if (xA<=xB)	{
				x0=xA;
				x1=xB;
			}	else	{
				x0=xB;
				x1=xA;
			}
			if (yA<=yB)	{
				y0=yA;
				y1=yB;
			}	else	{
				y0=yB;
				y1=yA;
			}
		}
		public long getArea()	{
			long dx=x1+1-x0;
			long dy=y1+1-y0;
			return dx*dy;
		}
		public RectangleIntersectionResult intersect(Rectangle other)	{
			long minX=Math.max(x0,other.x0);
			long maxX=Math.min(x1,other.x1);
			long minY=Math.max(y0,other.y0);
			long maxY=Math.min(y1,other.y1);
			if ((minX>maxX)||(minY>maxY)) return null;
			Rectangle properIntersection=new Rectangle(minX,maxX,minY,maxY);
			List<Rectangle> otherPieces=new ArrayList<>();
			// First we "sandwich" the X part.
			if (other.x0<minX) otherPieces.add(new Rectangle(other.x0,minX-1,other.y0,other.y1));
			if (maxX<other.x1) otherPieces.add(new Rectangle(maxX+1,other.x1,other.y0,other.y1));
			// Now we can work in the minX..maxX intersection, and we "sandwich" the Y in this range.
			if (other.y0<minY) otherPieces.add(new Rectangle(minX,maxX,other.y0,minY-1));
			if (maxY<other.y1) otherPieces.add(new Rectangle(minX,maxX,maxY+1,other.y1));
			return new RectangleIntersectionResult(properIntersection,otherPieces);
		}
		@Override
		public String toString()	{
			return String.format("(%d,%d)x(%d,%d)",x0,x1,y0,y1);
		}
	}
	
	private static class RandomGenerator	{
		private long value;
		public RandomGenerator()	{
			value=SEED;
		}
		public long next()	{
			long result=value;
			value=(value*value)%SIZE;
			return result;
		}
	}
	
	public static Rectangle[] generateRectangles(int size) {
		Rectangle[] result=new Rectangle[size];
		RandomGenerator gen=new RandomGenerator();
		for (int i=0;i<size;++i)	{
			long xA=gen.next();
			long xB=gen.next();
			long yA=gen.next();
			long yB=gen.next();
			result[i]=new Rectangle(xA,xB,yA,yB);
		}
		return result;
	}
	
	public static class ClockCanvas	{
		private final List<Set<Rectangle>> mainSets;
		private final List<List<Rectangle>> tmpToAdd;
		private final List<List<Rectangle>> tmpToRemove;
		public ClockCanvas(int hourLimit,long size)	{
			mainSets=new ArrayList<>(hourLimit);
			tmpToAdd=new ArrayList<>(hourLimit);
			tmpToRemove=new ArrayList<>(hourLimit);
			for (int i=0;i<hourLimit;++i)	{
				mainSets.add(new HashSet<>());
				tmpToAdd.add(new ArrayList<>());
				tmpToRemove.add(new ArrayList<>());
			}
			Rectangle initial=new Rectangle(0,size-1,0,size-1);
			mainSets.get(hourLimit-1).add(initial);
		}
		public long getSum()	{
			long result=0;
			for (int i=0;i<mainSets.size();++i)	{
				long thisResult=0;
				for (Rectangle r:mainSets.get(i)) thisResult+=r.getArea();
				result+=(i+1)*thisResult;
			}
			return result;
		}
		public void increaseInRectangle(Rectangle newArea)	{
			tmpToAdd.forEach(Collection::clear);
			tmpToRemove.forEach(Collection::clear);
			for (int i=0;i<mainSets.size();++i)	{
				List<Rectangle> toAdd=tmpToAdd.get(i);
				List<Rectangle> toAddNext=tmpToAdd.get((i+1)%tmpToAdd.size());
				List<Rectangle> toRemove=tmpToRemove.get(i);
				for (Rectangle r:mainSets.get(i))	{
					RectangleIntersectionResult intersectionSummary=newArea.intersect(r);
					if (intersectionSummary!=null)	{
						toRemove.add(r);
						toAdd.addAll(intersectionSummary.nonIntersectingParts);
						toAddNext.add(intersectionSummary.intersection);
					}
				}
			}
			for (int i=0;i<mainSets.size();++i)	{
				Set<Rectangle> subSet=mainSets.get(i);
				subSet.removeAll(tmpToRemove.get(i));
				subSet.addAll(tmpToAdd.get(i));
			}
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		ClockCanvas canvas=new ClockCanvas(HOUR_LIMIT,SIZE);
		Rectangle[] operations=generateRectangles(ITERATIONS);
		// for (Rectangle r:operations) canvas.increaseInRectangle(r);
		for (int i=0;i<operations.length;++i)	{
			System.out.println(i+"...");
			canvas.increaseInRectangle(operations[i]);
		}
		long result=canvas.getSum();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
