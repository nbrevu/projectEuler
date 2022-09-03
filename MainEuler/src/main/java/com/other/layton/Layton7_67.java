package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_67 {
	private final static int SIZE=6;
	private enum Movement	{
		RIGHT,UP,LEFT,DOWN;
	}
	private final static Map<Point,Point> WARPS=createWarps();
	
	private static Map<Point,Point> createWarps()	{
		Map<Point,Point> result=new HashMap<>();
		// These are for problem 67.
		/*
		Point blue1=new Point(2,0);
		Point blue2=new Point(3,4);
		Point green1=new Point(5,0);
		Point green2=new Point(0,4);
		Point violet1=new Point(4,1);
		Point violet2=new Point(1,3);
		result.put(blue1,blue2);
		result.put(blue2,blue1);
		result.put(green1,green2);
		result.put(green2,green1);
		result.put(violet1,violet2);
		result.put(violet2,violet1);
		*/
		// These are for problem S01.
		/*
		Point blue1=new Point(1,0);
		Point blue2=new Point(4,4);
		Point sqViolet1=new Point(4,0);
		Point sqViolet2=new Point(1,4);
		Point green1=new Point(0,1);
		Point green2=new Point(5,4);
		Point yellow1=new Point(5,1);
		Point yellow2=new Point(1,5);
		Point violet1=new Point(0,4);
		Point violet2=new Point(4,5);
		result.put(blue1,blue2);
		result.put(blue2,blue1);
		result.put(sqViolet1,sqViolet2);
		result.put(sqViolet2,sqViolet1);
		result.put(green1,green2);
		result.put(green2,green1);
		result.put(yellow1,yellow2);
		result.put(yellow2,yellow1);
		result.put(violet1,violet2);
		result.put(violet2,violet1);
		*/
		// And these are for problem 183.
		Point blue1=new Point(4,0);
		Point blue2=new Point(2,5);
		Point green1=new Point(2,0);
		Point green2=new Point(5,3);
		Point violet1=new Point(0,1);
		Point violet2=new Point(4,5);
		result.put(blue1,blue2);
		result.put(blue2,blue1);
		result.put(green1,green2);
		result.put(green2,green1);
		result.put(violet1,violet2);
		result.put(violet2,violet1);
		return result;
	}
	private static class Status7_67 implements ProblemStatus<Movement,Status7_67>	{
		private final Point currentPoint;
		private final boolean[][] visited;
		public Status7_67()	{
			// In problems 67 and S01, the starting point is (1,1). In 183, it's (0,4).
			currentPoint=new Point(0,4);
			visited=new boolean[SIZE][SIZE];
			visited[4][0]=true;
		}
		private Status7_67(Point currentPoint,boolean[][] visited)	{
			this.currentPoint=currentPoint;
			this.visited=visited;
		}
		@Override
		public boolean isFinal() {
			for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) if (!visited[i][j]) return false;
			return true;
		}
		@Override
		public List<Movement> availableMoves() {
			List<Movement> result=new ArrayList<>();
			if (currentPoint.x<SIZE-1)	{
				if (!visited[currentPoint.y][currentPoint.x+1]) result.add(Movement.RIGHT);
			}
			if (currentPoint.y>0)	{
				if (!visited[currentPoint.y-1][currentPoint.x]) result.add(Movement.UP);
			}
			if (currentPoint.x>0)	{
				if (!visited[currentPoint.y][currentPoint.x-1]) result.add(Movement.LEFT);
			}
			if (currentPoint.y<SIZE-1)	{
				if (!visited[currentPoint.y+1][currentPoint.x]) result.add(Movement.DOWN);
			}
			return result;
		}
		@Override
		public Status7_67 move(Movement move) {
			Point newPoint;
			switch (move)	{
				case RIGHT:newPoint=new Point(currentPoint.x+1,currentPoint.y);break;
				case UP:newPoint=new Point(currentPoint.x,currentPoint.y-1);break;
				case LEFT:newPoint=new Point(currentPoint.x-1,currentPoint.y);break;
				case DOWN:newPoint=new Point(currentPoint.x,currentPoint.y+1);break;
				default:throw new IllegalArgumentException();
			}
			boolean[][] newVisited=new boolean[SIZE][SIZE];
			for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) newVisited[i][j]=visited[i][j];
			newVisited[newPoint.y][newPoint.x]=true;
			if (WARPS.containsKey(newPoint))	{
				newPoint=WARPS.get(newPoint);
				newVisited[newPoint.y][newPoint.x]=true;
			}
			return new Status7_67(newPoint,newVisited);
		}
		@Override
		public int hashCode()	{
			return currentPoint.hashCode()+Arrays.deepHashCode(visited);
		}
		@Override
		public boolean equals(Object other)	{
			Status7_67 s=(Status7_67)other;
			return Objects.equals(currentPoint,s.currentPoint)&&Objects.deepEquals(visited,s.visited);
		}
		@Override
		public String toString()	{
			return "["+currentPoint.x+","+currentPoint.y+"]";
		}
	}

	public static void main(String[] args)	{
		Status7_67 initial=new Status7_67();
		BreadthSearch<Movement,Status7_67> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Movement,Status7_67>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine L�sung gefunden!!!!!");
		else for (SearchPath<Movement,Status7_67> path:result) path.print(System.out);
	}
}
