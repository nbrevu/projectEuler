package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_49 {
	private final static int SIZE=7;
	
	private static class Move7_49	{
		private final int movingBunny;
		private final Point destination;
		public Move7_49(int movingBunny,Point destination)	{
			this.movingBunny=movingBunny;
			this.destination=destination;
		}
		@Override
		public String toString()	{
			return "["+movingBunny+"] => ("+destination.x+","+destination.y+")";
		}
	}
	
	private static class PointSegment	{
		private final Point p1;
		private final Point p2;
		public PointSegment(Point p1,Point p2)	{
			this.p1=p1;
			this.p2=p2;
		}
		public boolean crosses(PointSegment other)	{
			// ACHTUNG! Geometry and algebra ahead.
			// We solve:
			// a11·t + a12·u = b1;
			// a21·t + a22·u = b2.
			// We use Cramer because it's just easier.
			int a11=p2.x-p1.x;
			int a12=other.p1.x-other.p2.x;
			int a21=p2.y-p1.y;
			int a22=other.p1.y-other.p2.y;
			int b1=other.p1.x-p1.x;
			int b2=other.p1.y-p1.y;
			int detM=a11*a22-a12*a21;
			if (detM==0) return false;
			int detT=b1*a22-b2*a12;
			int detU=a11*b2-a21*b1;
			return isQuotientBetween0And1Strict(detT,detM)&&isQuotientBetween0And1(detU,detM);
		}
		private boolean isQuotientBetween0And1(int a,int b)	{
			if (a==0) return true;
			else if (a*b<0) return false;	// Different sign, so a/b is negative.
			else return Math.abs(a)<=Math.abs(b);
		}
		private boolean isQuotientBetween0And1Strict(int a,int b)	{
			if (a==0) return false;
			else if (a*b<0) return false;	// Different sign, so a/b is negative.
			else return Math.abs(a)<Math.abs(b);
		}
	}
	
	public static class Status7_49 implements ProblemStatus<Move7_49,Status7_49>	{
		private final static List<Point> FLOWERS=Arrays.asList(new Point(2,3),new Point(3,2),new Point(3,4));
		private final int lastMovingBunny;
		private final Point[] positions;
		private final int hashCode;
		public Status7_49()	{
			lastMovingBunny=3;
			positions=new Point[3];
			positions[0]=new Point(0,1);
			positions[1]=new Point(0,3);
			positions[2]=new Point(3,2);
			hashCode=calculateHashCode();
		}
		private Status7_49(int lastMovingBunny,Point[] positions)	{
			this.lastMovingBunny=lastMovingBunny;
			this.positions=positions;
			hashCode=calculateHashCode();
		}
		@Override
		public boolean isFinal() {
			for (Point p:positions) if (!FLOWERS.contains(p)) return false;
			return true;
		}
		@Override
		public List<Move7_49> availableMoves() {
			List<Move7_49> result=new ArrayList<>();
			for (int i=0;i<positions.length;++i)	{
				if (i==lastMovingBunny) continue;
				Point other1=positions[(i+1)%3];
				Point other2=positions[(i+2)%3];
				PointSegment seg=new PointSegment(other1,other2);
				for (Point p:getMoves(positions[i]))	{
					if (p.equals(other1)||p.equals(other2)) continue;
					PointSegment direction=new PointSegment(positions[i],p);
					if (seg.crosses(direction)) result.add(new Move7_49(i,p));
				}
			}
			return result;
		}
		@Override
		public Status7_49 move(Move7_49 move) {
			Point[] newPositions=Arrays.copyOf(positions,positions.length);
			newPositions[move.movingBunny]=move.destination;
			return new Status7_49(move.movingBunny,newPositions);
		}
		private static List<Point> getMoves(Point current)	{
			List<Point> result=new ArrayList<>();
			getAllMovesWhileValid(result,current,1,0);
			getAllMovesWhileValid(result,current,1,-1);
			getAllMovesWhileValid(result,current,0,-1);
			getAllMovesWhileValid(result,current,-1,-1);
			getAllMovesWhileValid(result,current,-1,0);
			getAllMovesWhileValid(result,current,-1,1);
			getAllMovesWhileValid(result,current,0,1);
			getAllMovesWhileValid(result,current,1,1);
			return result;
		}
		private static void getAllMovesWhileValid(List<Point> result,Point base,int dx,int dy)	{
			Point next=new Point(base.x+dx,base.y+dy);
			while (isValid(next))	{
				result.add(next);
				next=new Point(next.x+dx,next.y+dy);
			}
		}
		private static boolean isValid(Point p)	{
			return (p.x>=0)&&(p.x<SIZE)&&(p.y>=0)&&(p.y<SIZE);
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<positions.length;++i)	{
				result+=positions[i].x*multiplier;
				multiplier*=SIZE;
			}
			result+=lastMovingBunny*multiplier;
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status7_49 s=(Status7_49)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			// This is kind of ugly because Point.toString() is retarded. But it's good enough.
			return Arrays.toString(positions);
		}
	}
	
	public static void main(String[] args)	{
		Status7_49 initial=new Status7_49();
		BreadthSearch<Move7_49,Status7_49> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move7_49,Status7_49>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move7_49,Status7_49> path:result) path.print(System.out);
	}
}
