package com.euler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.threads.InputThreadStopper;
import com.euler.threads.InterruptableProgram;
import com.euler.threads.ProgramState;
import com.google.common.math.IntMath;

public class Euler790_3 extends InterruptableProgram<Euler790_3.Euler790State> {
	private final static int SIZE=50515093;
	private final static long SEED=290797l;
	private final static int HOUR_LIMIT=12;
	private final static int ITERATIONS=100000;
	
	private final static int PORTIONS=500;
	private final static Path TMP_FILE=Paths.get("E:\\tmp790.txt");
	
	public static class Euler790State implements ProgramState	{
		private int nextX;
		private int nextY;
		private long currentSum;
		@Override
		public void saveToFile(BufferedWriter writer) throws IOException {
			System.out.println(String.format("Saving to file. Next tile: %d, %d. Current sum: %d.",nextX,nextY,currentSum));
			writer.write(Long.toString(nextX));
			writer.newLine();
			writer.write(Long.toString(nextY));
			writer.newLine();
			writer.write(Long.toString(currentSum));
		}
		@Override
		public void readStateFromFile(BufferedReader reader) throws IOException {
			nextX=Integer.parseInt(reader.readLine());
			nextY=Integer.parseInt(reader.readLine());
			currentSum=Long.parseLong(reader.readLine());
		}
	}
	
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
	
	private Euler790State state;

	public Euler790_3() {
		super(new InputThreadStopper());
		state=getInitialState();
	}

	@Override
	public void main(Euler790State state) {
		this.state=state;
		try	{
			Rectangle[] operations=generateRectangles(ITERATIONS);
			Range[] ranges=Range.divide(SIZE,PORTIONS);
			while ((state.nextX<PORTIONS)&&(state.nextY<PORTIONS))	{
				System.out.println(String.format("x=%d, y=%d...",state.nextX,state.nextY));
				Rectangle subArea=new Rectangle(ranges[state.nextX],ranges[state.nextY]);
				ClockCanvas canvas=new ClockCanvas(HOUR_LIMIT,SIZE,subArea);
				for (int i=0;i<operations.length;++i)	{
					Rectangle operator=operations[i];
					if (operator.isFullyInside(subArea)) canvas.rotate();
					else if (subArea.intersects(operations[i])) canvas.increaseInRectangle(operations[i]);
				}
				state.currentSum+=canvas.getSum();
				++state.nextY;
				if (state.nextY>=PORTIONS)	{
					state.nextY=0;
					++state.nextX;
				}
				if (checkAndStop()) return;
			}
			System.out.println(state.currentSum);
		}	catch (IOException exc)	{
			System.out.println("Error de I/O");
		}
	}

	@Override
	public Path getFileName() {
		return TMP_FILE;
	}

	@Override
	public Euler790State getInitialState() {
		Euler790State result=new Euler790State();
		result.nextX=0;
		result.nextY=0;
		result.currentSum=0l;
		return result;
	}

	@Override
	public Euler790State getCurrentState() {
		return state;
	}
	
	// ZUTUN! TODO! TEHDÄ!!!!! Try the "rotate" approach when a tile is fully inside the rectangle. I believe this should happen often.
	// Try first in 790_4 to check whether the results are right.
	public static void main(String[] args) throws IOException,ReflectiveOperationException	{
		new Euler790_3().start(Euler790State.class);
	}
}
