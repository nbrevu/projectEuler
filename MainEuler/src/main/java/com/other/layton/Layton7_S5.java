package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_S5 {
	private final static int SIZE=6;
	
	private enum KnightMove	{
		A(-2,-1),B(-2,1),C(-1,-2),D(-1,2),E(1,-2),F(1,2),G(2,-1),H(2,1);
		
		public final int dx,dy;
		KnightMove(int dx,int dy)	{
			this.dx=dx;
			this.dy=dy;
		}
		public Point move(Point p)	{
			return new Point(p.x+dx,p.y+dy);
		}
	}
	
	private static class ProblemS5	{
		private final List<Point> impassable;
		private final List<Point> carrots;
		private final Point goal;
		private final Point startingPoint;
		public ProblemS5()	{
			// These are for S5
			/*
			impassable=new ArrayList<>();
			impassable.add(new Point(0,1));
			impassable.add(new Point(0,4));
			impassable.add(new Point(1,0));
			impassable.add(new Point(1,5));
			impassable.add(new Point(4,0));
			impassable.add(new Point(4,5));
			impassable.add(new Point(5,1));
			impassable.add(new Point(5,4));
			impassable.add(new Point(0,3));
			impassable.add(new Point(1,3));
			impassable.add(new Point(2,3));
			impassable.add(new Point(3,2));
			impassable.add(new Point(4,2));
			impassable.add(new Point(5,2));
			carrots=new ArrayList<>();
			carrots.add(new Point(0,0));
			carrots.add(new Point(0,5));
			carrots.add(new Point(5,0));
			carrots.add(new Point(5,5));
			goal=new Point(2,2);
			startingPoint=new Point(3,3);
			*/
			// These are for 178. There is something wrong and this doesn't work...
			impassable=new ArrayList<>();
			impassable.add(new Point(0,3));
			impassable.add(new Point(1,0));
			impassable.add(new Point(1,3));
			impassable.add(new Point(1,5));
			impassable.add(new Point(2,0));
			impassable.add(new Point(2,2));
			impassable.add(new Point(2,3));
			impassable.add(new Point(2,5));
			impassable.add(new Point(3,1));
			impassable.add(new Point(3,4));
			impassable.add(new Point(4,2));
			impassable.add(new Point(4,4));
			impassable.add(new Point(5,0));
			impassable.add(new Point(5,4));
			carrots=new ArrayList<>();
			carrots.add(new Point(0,0));
			carrots.add(new Point(1,2));
			carrots.add(new Point(2,5));
			carrots.add(new Point(3,2));
			goal=new Point(1,1);
			startingPoint=new Point(5,2);
		}
		public boolean canPass(Point p)	{
			return !impassable.contains(p);
		}
		public int howManyCarrots()	{
			return carrots.size();
		}
		public int isCarrot(Point p)	{
			return carrots.indexOf(p);
		}
		public boolean isGoal(Point p)	{
			return goal.equals(p);
		}
	}
	
	private static class StatusS5 implements ProblemStatus<KnightMove,StatusS5>	{
		private final static ProblemS5 PROBLEM=new ProblemS5();
		private final Point currentPoint;
		private final boolean[] visitedCarrots;
		private final int hashCode;
		public StatusS5()	{
			currentPoint=PROBLEM.startingPoint;
			visitedCarrots=new boolean[PROBLEM.howManyCarrots()];
			hashCode=calculateHashCode();
		}
		private StatusS5(Point currentPoint,boolean[] visitedCarrots)	{
			this.currentPoint=currentPoint;
			this.visitedCarrots=visitedCarrots;
			hashCode=calculateHashCode();
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<visitedCarrots.length;++i)	{
				if (visitedCarrots[i]) result+=multiplier;
				multiplier*=2;
			}
			result+=currentPoint.x*multiplier;
			multiplier*=SIZE;
			result+=currentPoint.y*multiplier;
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			StatusS5 s=(StatusS5)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return "["+currentPoint.x+","+currentPoint.y+"]";
		}
		@Override
		public boolean isFinal() {
			if (!PROBLEM.isGoal(currentPoint)) return false;
			for (int i=0;i<visitedCarrots.length;++i) if (!visitedCarrots[i]) return false;
			return true;
		}
		@Override
		public List<KnightMove> availableMoves() {
			List<KnightMove> result=new ArrayList<>();
			for (KnightMove move:KnightMove.values())	{
				Point target=move.move(currentPoint);
				if (PROBLEM.canPass(target)&&inBounds(target)) result.add(move);
			}
			return result;
		}
		@Override
		public StatusS5 move(KnightMove move) {
			Point newPos=move.move(currentPoint);
			boolean[] newVisited=visitedCarrots;
			int carrotIndex=PROBLEM.isCarrot(newPos);
			if (carrotIndex!=-1)	{
				newVisited=Arrays.copyOf(visitedCarrots,visitedCarrots.length);
				newVisited[carrotIndex]=true;
			}
			return new StatusS5(newPos,newVisited);
		}
		private boolean inBounds(Point p)	{
			return (p.x>=0)&&(p.x<SIZE)&&(p.y>=0)&&(p.y<SIZE);
		}
	}

	public static void main(String[] args)	{
		StatusS5 initial=new StatusS5();
		BreadthSearch<KnightMove,StatusS5> search=new BreadthSearch<>(initial);
		Collection<SearchPath<KnightMove,StatusS5>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<KnightMove,StatusS5> path:result) path.print(System.out);
	}
}
