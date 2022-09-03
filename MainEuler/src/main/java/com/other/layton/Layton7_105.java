package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_105 {
	private final static int WIDTH=7;
	private final static int HEIGHT=7;
	
	private enum Move	{
		RIGHT(0,1),UP(-1,0),LEFT(0,-1),DOWN(1,0);
		
		public final int dx;
		public final int dy;
		Move(int dx,int dy)	{
			this.dx=dx;
			this.dy=dy;
		}
	}
	
	private static class Board	{
		public final Point initialPoint;
		private final Point goal;
		private final Set<Point> holes;
		private final Set<Point> walls;
		private Board(Point initialPoint,Point goal,Set<Point> holes,Set<Point> walls)	{
			this.initialPoint=initialPoint;
			this.goal=goal;
			this.holes=holes;
			this.walls=walls;
		}
		public boolean canMove(Point position,Move move)	{
			Point nextPos=movePoint(position,move);
			return !holes.contains(nextPos);
		}
		public boolean isGoal(Point point)	{
			return goal.equals(point);
		}
		public Point movePoint(Point position,Move move)	{
			Point nextPoint=doMove(position,move);
			return (isPossible(nextPoint))?nextPoint:position;
		}
		private Point doMove(Point position,Move move)	{
			return new Point(position.x+move.dx,position.y+move.dy);
		}
		public boolean isPossible(Point point)	{
			if ((point.x<0)||(point.x>=WIDTH)||(point.y<0)||(point.y>=HEIGHT)) return false;
			else return !walls.contains(point);
		}
		public static Board getLeftBoard105()	{
			Point initialPoint=new Point(5,2);
			Point goal=new Point(0,3);
			Set<Point> holes=new HashSet<>();
			holes.add(new Point(0,4));
			holes.add(new Point(1,1));
			holes.add(new Point(1,5));
			holes.add(new Point(2,2));
			holes.add(new Point(3,0));
			holes.add(new Point(4,2));
			holes.add(new Point(5,6));
			Set<Point> walls=new HashSet<>();
			walls.add(new Point(1,2));
			walls.add(new Point(3,4));
			return new Board(initialPoint,goal,holes,walls);
		}
		public static Board getRightBoard105()	{
			Point initialPoint=new Point(4,2);
			Point goal=new Point(0,3);
			Set<Point> holes=new HashSet<>();
			holes.add(new Point(0,0));
			holes.add(new Point(0,4));
			holes.add(new Point(0,5));
			holes.add(new Point(2,1));
			holes.add(new Point(2,6));
			holes.add(new Point(3,3));
			holes.add(new Point(3,4));
			holes.add(new Point(4,5));
			holes.add(new Point(6,3));
			Set<Point> walls=new HashSet<>();
			walls.add(new Point(1,2));
			walls.add(new Point(4,1));
			return new Board(initialPoint,goal,holes,walls);
		}
		public static Board getLeftBoardS6()	{
			Point initialPoint=new Point(0,2);
			Point goal=new Point(0,3);
			Set<Point> holes=new HashSet<>();
			for (int i=0;i<HEIGHT;++i)	{
				holes.add(new Point(i,0));
				holes.add(new Point(i,6));
			}
			holes.add(new Point(1,4));
			holes.add(new Point(3,3));
			holes.add(new Point(4,4));
			holes.add(new Point(6,3));
			Set<Point> walls=new HashSet<>();
			walls.add(new Point(1,2));
			walls.add(new Point(2,2));
			walls.add(new Point(4,1));
			return new Board(initialPoint,goal,holes,walls);
		}
		public static Board getRightBoardS6()	{
			Point initialPoint=new Point(0,4);
			Point goal=new Point(0,3);
			Set<Point> holes=new HashSet<>();
			for (int i=0;i<HEIGHT;++i)	{
				holes.add(new Point(i,0));
				holes.add(new Point(i,6));
			}
			holes.add(new Point(1,1));
			holes.add(new Point(1,2));
			holes.add(new Point(1,4));
			holes.add(new Point(1,5));
			holes.add(new Point(3,3));
			holes.add(new Point(6,3));
			Set<Point> walls=new HashSet<>();
			walls.add(new Point(5,2));
			walls.add(new Point(5,4));
			return new Board(initialPoint,goal,holes,walls);
		}
		public static Board getLeftBoard180()	{
			Point initialPoint=new Point(6,0);
			Point goal=new Point(0,3);
			Set<Point> holes=new HashSet<>();
			holes.add(new Point(0,4));
			holes.add(new Point(1,2));
			holes.add(new Point(1,4));
			holes.add(new Point(2,1));
			holes.add(new Point(2,6));
			holes.add(new Point(3,3));
			holes.add(new Point(3,5));
			holes.add(new Point(4,1));
			holes.add(new Point(5,2));
			holes.add(new Point(5,5));
			Set<Point> walls=new HashSet<>();
			walls.add(new Point(0,2));
			walls.add(new Point(2,4));
			walls.add(new Point(4,0));
			return new Board(initialPoint,goal,holes,walls);
		}
		public static Board getRightBoard180()	{
			Point initialPoint=new Point(6,6);
			Point goal=new Point(0,3);
			Set<Point> holes=new HashSet<>();
			holes.add(new Point(0,1));
			holes.add(new Point(1,1));
			holes.add(new Point(1,3));
			holes.add(new Point(2,0));
			holes.add(new Point(2,3));
			holes.add(new Point(2,5));
			holes.add(new Point(2,6));
			holes.add(new Point(3,2));
			holes.add(new Point(3,5));
			holes.add(new Point(4,3));
			holes.add(new Point(5,0));
			holes.add(new Point(5,3));
			holes.add(new Point(6,1));
			Set<Point> walls=new HashSet<>();
			walls.add(new Point(3,3));
			walls.add(new Point(5,5));
			return new Board(initialPoint,goal,holes,walls);
		}
	}
	
	private static class Status105 implements ProblemStatus<Move,Status105>	{
		private final static Board leftBoard=Board.getLeftBoard180();
		private final static Board rightBoard=Board.getRightBoard180();
		
		private final Point leftPos;
		private final Point rightPos;
		private final int hashCode;
		public Status105()	{
			leftPos=leftBoard.initialPoint;
			rightPos=rightBoard.initialPoint;
			hashCode=calculateHashCode();
		}
		private Status105(Point leftPos,Point rightPos)	{
			this.leftPos=leftPos;
			this.rightPos=rightPos;
			hashCode=calculateHashCode();
		}
		@Override
		public boolean isFinal() {
			return leftBoard.isGoal(leftPos)&&rightBoard.isGoal(rightPos);
		}
		@Override
		public List<Move> availableMoves() {
			List<Move> result=new ArrayList<>();
			for (Move move:Move.values()) if (leftBoard.canMove(leftPos,move)&&rightBoard.canMove(rightPos,move)) result.add(move);
			return result;
		}
		@Override
		public Status105 move(Move move) {
			Point newLeft=leftBoard.movePoint(leftPos,move);
			Point newRight=rightBoard.movePoint(rightPos,move);
			return new Status105(newLeft,newRight);
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			result+=leftPos.x*multiplier;
			multiplier*=WIDTH;
			result+=leftPos.y*multiplier;
			multiplier*=HEIGHT;
			result+=rightPos.x*multiplier;
			multiplier*=WIDTH;
			result+=rightPos.y*multiplier;
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status105 s=(Status105)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return toString(leftPos)+";"+toString(rightPos);
		}
		private static String toString(Point point)	{
			return ""+point.x+","+point.y;
		}
	}
	
	public static void main(String[] args)	{
		Status105 initial=new Status105();
		BreadthSearch<Move,Status105> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move,Status105>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move,Status105> path:result) path.print(System.out);
	}
}
