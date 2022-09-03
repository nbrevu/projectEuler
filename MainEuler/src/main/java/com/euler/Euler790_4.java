package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.math.IntMath;

public class Euler790_4 {
	private final static int SIZE=50515093;
	private final static long SEED=290797l;
	private final static int HOUR_LIMIT=12;
	private final static int ITERATIONS=100000;
	
	private final static int PORTIONS=500;
	
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
		public Rectangle(Range xRange,Range yRange)	{
			this(xRange.start,xRange.end,yRange.start,yRange.end);
		}
		public long getArea()	{
			long dx=x1+1-x0;
			long dy=y1+1-y0;
			return dx*dy;
		}
		// Returns true if the argument is fully inside this object.
		public boolean isFullyInside(Rectangle other)	{
			return (x0<=other.x0)&&(x1>=other.x1)&&(y0<=other.y0)&&(y1>=other.y1);
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
		public boolean intersects(Rectangle other)	{
			long minX=Math.max(x0,other.x0);
			long maxX=Math.min(x1,other.x1);
			long minY=Math.max(y0,other.y0);
			long maxY=Math.min(y1,other.y1);
			return ((minX<=maxX)&&(minY<=maxY));
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
		private int rotations;
		public ClockCanvas(int hourLimit,long size,Rectangle initial)	{
			mainSets=new ArrayList<>(hourLimit);
			tmpToAdd=new ArrayList<>(hourLimit);
			tmpToRemove=new ArrayList<>(hourLimit);
			for (int i=0;i<hourLimit;++i)	{
				mainSets.add(new HashSet<>());
				tmpToAdd.add(new ArrayList<>());
				tmpToRemove.add(new ArrayList<>());
			}
			mainSets.get(hourLimit-1).add(initial);
			rotations=0;
		}
		public void rotate()	{
			rotations=(rotations+1)%mainSets.size();
		}
		public long getSum()	{
			long result=0;
			int currentValue=1+rotations;
			for (int i=0;i<mainSets.size();++i)	{
				long thisResult=0;
				for (Rectangle r:mainSets.get(i)) thisResult+=r.getArea();
				result+=currentValue*thisResult;
				if (currentValue>=mainSets.size()) currentValue=1;
				else ++currentValue;
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
	
	private static class Range	{
		public final int start;
		public final int end;
		public Range(int start,int end)	{
			this.start=start;
			this.end=end;
		}
		public static Range[] divide(int limit,int portions)	{
			Range[] result=new Range[portions];
			int size=IntMath.divide(limit,portions,RoundingMode.UP);
			int last=0;
			for (int i=0;i<portions;++i)	{
				int next=Math.min(last+size,limit);
				result[i]=new Range(last,next-1);
				last=next;
			}
			return result;
		}
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Rectangle[] operations=generateRectangles(ITERATIONS);
		long result=0;
		Range[] ranges=Range.divide(SIZE,PORTIONS);
		for (int x=0;x<PORTIONS;++x) for (int y=0;y<PORTIONS;++y)	{
			System.out.println(String.format("x=%d, y=%d...",x,y));
			Rectangle subArea=new Rectangle(ranges[x],ranges[y]);
			ClockCanvas canvas=new ClockCanvas(HOUR_LIMIT,SIZE,subArea);
			for (int i=0;i<operations.length;++i)	{
				Rectangle operator=operations[i];
				if (operator.isFullyInside(subArea)) canvas.rotate();
				else if (subArea.intersects(operations[i])) canvas.increaseInRectangle(operations[i]);
			}
			result+=canvas.getSum();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
