package com.other.layton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_70 {
	public final static int WIDTH=4;
	public final static int HEIGHT=2;
	
	private enum HorizontalPencil	{
		LEFT,RIGHT;
	}
	private enum VerticalPencil	{
		UP,DOWN;
	}
	
	private enum Move	{
		RIGHT,UP,LEFT,DOWN;
	}
	
	private static class PencilConfiguration	{
		private final HorizontalPencil[][] horizontalPencils;
		private final VerticalPencil[][] verticalPencils;
		public PencilConfiguration()	{
			horizontalPencils=new HorizontalPencil[][]{
				{HorizontalPencil.LEFT,HorizontalPencil.RIGHT,HorizontalPencil.LEFT,HorizontalPencil.LEFT},
				{HorizontalPencil.RIGHT,HorizontalPencil.RIGHT,HorizontalPencil.LEFT,HorizontalPencil.RIGHT},
				{HorizontalPencil.LEFT,HorizontalPencil.LEFT,HorizontalPencil.RIGHT,HorizontalPencil.RIGHT}
			};
			verticalPencils=new VerticalPencil[][]{
				{VerticalPencil.UP,VerticalPencil.UP,VerticalPencil.DOWN,VerticalPencil.DOWN,VerticalPencil.UP},
				{VerticalPencil.UP,VerticalPencil.UP,VerticalPencil.DOWN,VerticalPencil.DOWN,VerticalPencil.DOWN}
			};
		}
		public List<Move> availableMoves(int x,int y)	{
			List<Move> result=new ArrayList<>();
			if (canMoveRight(x,y)) result.add(Move.RIGHT);
			if (canMoveUp(x,y)) result.add(Move.UP);
			if (canMoveLeft(x,y)) result.add(Move.LEFT);
			if (canMoveDown(x,y)) result.add(Move.DOWN);
			return result;
		}
		private boolean canMoveRight(int x,int y)	{
			if (x>=WIDTH) return false;
			HorizontalPencil pencil=horizontalPencils[y][x];
			return pencil==HorizontalPencil.RIGHT;
		}
		private boolean canMoveUp(int x,int y)	{
			if (y<=0) return false;
			VerticalPencil pencil=verticalPencils[y-1][x];
			return pencil==VerticalPencil.UP;
		}
		private boolean canMoveLeft(int x,int y)	{
			if (x<=0) return false;
			HorizontalPencil pencil=horizontalPencils[y][x-1];
			return pencil==HorizontalPencil.LEFT;
		}
		private boolean canMoveDown(int x,int y)	{
			if (y>=HEIGHT) return false;
			VerticalPencil pencil=verticalPencils[y][x];
			return pencil==VerticalPencil.DOWN;
		}
	}
	
	private static class Status7_70 implements ProblemStatus<Move,Status7_70>	{
		private final static PencilConfiguration CONFIGURATION=new PencilConfiguration();
		private final int x;
		private final int y;
		private final boolean[][] horizontalPencils;
		private final boolean[][] verticalPencils;
		private final int hashCode;
		public Status7_70(int x,int y)	{
			this.x=x;
			this.y=y;
			horizontalPencils=new boolean[1+HEIGHT][WIDTH];
			verticalPencils=new boolean[HEIGHT][1+WIDTH];
			hashCode=calculateHashCode();
		}
		private Status7_70(int x,int y,boolean[][] horizontalPencils,boolean[][] verticalPencils)	{
			this.x=x;
			this.y=y;
			this.horizontalPencils=horizontalPencils;
			this.verticalPencils=verticalPencils;
			hashCode=calculateHashCode();
		}
		
		@Override
		public boolean isFinal() {
			int squares=0;
			for (int i=0;i<HEIGHT;++i) for (int j=0;j<WIDTH;++j) if (isSquareDrawn(i,j)) ++squares;
			return squares>=4;
		}

		@Override
		public List<Move> availableMoves() {
			return CONFIGURATION.availableMoves(x,y);
		}

		@Override
		public Status7_70 move(Move move) {
			switch (move)	{
				case RIGHT:	{
					boolean[][] newH=duplicate(horizontalPencils);
					newH[y][x]=!newH[y][x];
					return new Status7_70(x+1,y,newH,verticalPencils);
				}	case UP:	{
					boolean[][] newV=duplicate(verticalPencils);
					newV[y-1][x]=!newV[y-1][x];
					return new Status7_70(x,y-1,horizontalPencils,newV);
				}	case LEFT:	{
					boolean[][] newH=duplicate(horizontalPencils);
					newH[y][x-1]=!newH[y][x-1];
					return new Status7_70(x-1,y,newH,verticalPencils);
				}	case DOWN:	{
					boolean[][] newV=duplicate(verticalPencils);
					newV[y][x]=!newV[y][x];
					return new Status7_70(x,y+1,horizontalPencils,newV);
				}	default: throw new IllegalArgumentException();
			}
		}
		private int calculateHashCode()	{
			int result=0;
			int multiplier=1;
			for (int i=0;i<=HEIGHT;++i) for (int j=0;j<WIDTH;++j)	{
				if (horizontalPencils[i][j]) result+=multiplier;
				multiplier*=2;
			}
			for (int i=0;i<HEIGHT;++i) for (int j=0;j<=WIDTH;++j)	{
				if (verticalPencils[i][j]) result+=multiplier;
				multiplier*=2;
			}
			result+=x*multiplier;
			multiplier*=1+WIDTH;
			result+=y*multiplier;
			return result;
		}
		private boolean isSquareDrawn(int row,int col)	{
			return horizontalPencils[row][col]&&horizontalPencils[1+row][col]&&verticalPencils[row][col]&&verticalPencils[row][1+col];
		}
		private static boolean[][] duplicate(boolean[][] array)	{
			int d1=array.length;
			int d2=array[0].length;
			boolean[][] result=new boolean[d1][d2];
			for (int i=0;i<d1;++i) for (int j=0;j<d2;++j) result[i][j]=array[i][j];
			return result;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			Status7_70 s=(Status7_70)other;
			return s.hashCode==hashCode;
		}
		@Override
		public String toString()	{
			return "("+x+","+y+")";
		}
	}
	
	public static void main(String[] args)	{
		List<Status7_70> startingPoints=new ArrayList<>();
		for (int i=0;i<=WIDTH;++i) for (int j=0;j<=HEIGHT;++j) startingPoints.add(new Status7_70(i,j));
		BreadthSearch<Move,Status7_70> search=new BreadthSearch<>(startingPoints);
		Collection<SearchPath<Move,Status7_70>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move,Status7_70> path:result) path.print(System.out);
	}
}
