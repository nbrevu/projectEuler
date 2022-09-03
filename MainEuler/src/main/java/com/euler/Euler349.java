package com.euler;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler349 {
	// Another one at the first try, and this one was satisfying because of how impossible it looked.
	private final static int START_SEARCH=100000;
	private final static int SEARCH_LIMIT=1000000;
	private final static int LOOP_LENGTH=104;
	private final static long GOAL=1000000000000000000l;
	
	private static enum Heading	{
		RIGHT,UP,LEFT,DOWN;
		public Heading rotateLeft()	{
			switch (this)	{
			case RIGHT:return UP;
			case UP:return LEFT;
			case LEFT:return DOWN;
			case DOWN:return RIGHT;
			default:return null;
			}
		}
		public Heading rotateRight()	{
			switch (this)	{
			case RIGHT:return DOWN;
			case UP:return RIGHT;
			case LEFT:return UP;
			case DOWN:return LEFT;
			default:return null;
			}
		}
	}
	
	private static class Position	{
		public final long x;
		public final long y;
		private static Table<Long,Long,Position> cache=HashBasedTable.create();
		private Position(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		public static Position of(long x,long y)	{
			Position inCache=cache.get(x,y);
			if (inCache==null)	{
				inCache=new Position(x,y);
				cache.put(x,y,inCache);
			}
			return inCache;
		}
		public Position advance(Heading direction)	{
			switch (direction)	 {
			case RIGHT:return of(x+1,y);
			case UP:return of(x,y+1);
			case LEFT:return of(x-1,y);
			case DOWN:return of(x,y-1);
			default:return null;
			}
		}
		public Position distanceFrom(Position p)	{
			return of(x-p.x,y-p.y);
		}
	}
	
	private static class AntPosition	{
		public final Position position;
		public final Heading heading;
		public final long blackSquareCount;
		public AntPosition(Position position,Heading heading)	{
			this.position=position;
			this.heading=heading;
			blackSquareCount=0;
		}
		private AntPosition(Position position,Heading heading,long blackSquareCount)	{
			this.position=position;
			this.heading=heading;
			this.blackSquareCount=blackSquareCount;
		}
		public AntPosition next(Set<Position> blackSquares)	{
			Heading nextHeading;
			if (blackSquares.contains(position))	{
				blackSquares.remove(position);
				nextHeading=heading.rotateRight();
			}	else	{
				blackSquares.add(position);
				nextHeading=heading.rotateLeft();
			}
			Position nextPosition=position.advance(nextHeading);
			return new AntPosition(nextPosition,nextHeading,blackSquares.size());
		}
	}
	
	private static boolean detectLoop(AntPosition cur,AntPosition prev,AntPosition prev2)	{
		if ((cur.heading!=prev.heading)||(prev.heading!=prev2.heading)) return false;
		if ((cur.blackSquareCount-prev.blackSquareCount)!=(prev.blackSquareCount-prev2.blackSquareCount)) return false;
		Position distance1=cur.position.distanceFrom(prev.position);
		Position distance2=prev.position.distanceFrom(prev2.position);
		return distance1==distance2;
	}
	
	public static void main(String[] args)	{
		Set<Position> blackSquares=new HashSet<>();
		AntPosition[] history=new AntPosition[SEARCH_LIMIT];
		AntPosition current=new AntPosition(Position.of(0,0),Heading.RIGHT);
		for (int i=0;i<START_SEARCH;++i)	{
			history[i]=current;
			current=current.next(blackSquares);
		}
		for (int i=START_SEARCH;i<SEARCH_LIMIT;++i)	{
			history[i]=current;
			AntPosition prev=history[i-LOOP_LENGTH];
			AntPosition prev2=history[i-2*LOOP_LENGTH];
			AntPosition prev3=history[i-3*LOOP_LENGTH];
			AntPosition prev4=history[i-4*LOOP_LENGTH];
			if (detectLoop(current,prev,prev2)&&detectLoop(prev,prev2,prev3)&&detectLoop(prev2,prev3,prev4))	{
				long remaining=GOAL-i;
				long toAdd=LOOP_LENGTH-(remaining%(long)LOOP_LENGTH);
				long milestone=i-toAdd;
				remaining=GOAL-milestone;
				if (((remaining)%(long)LOOP_LENGTH)!=0) System.out.println("Creo que la he cagado.");
				long total=history[(int)milestone].blackSquareCount;
				long loopDiff=total-history[(int)milestone-LOOP_LENGTH].blackSquareCount;
				System.out.println(total+loopDiff*(remaining/(long)LOOP_LENGTH));
				return;
			}
		}
		System.out.println("No loop detected.");
	}
}
