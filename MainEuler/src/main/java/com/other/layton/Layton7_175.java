package com.other.layton;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.other.layton.search.BreadthSearch;
import com.other.layton.search.ProblemStatus;
import com.other.layton.search.SearchPath;

public class Layton7_175 {
	private final static int WIDTH=6;
	private final static int HEIGHT=5;
	
	private static class Move	{
		private final int row;
		private final int column;
		private final int id;
		public Move(int row,int column,int id)	{
			this.row=row;
			this.column=column;
			this.id=id;
		}
		@Override
		public String toString()	{
			return "["+row+","+column+"]:"+id;
		}
	}
	
	private static class Status175 implements ProblemStatus<Move,Status175>	{
		private final int[][] contents;
		public Status175()	{
			contents=new int[][]{{0,0,0,0,5,0},{5,5,3,3,4,5},{0,4,0,4,5,0},{4,4,0,3,3,0},{2,5,2,0,5,5}};
		}
		private Status175(int[][] contents)	{
			this.contents=contents;
		}
		@Override
		public int hashCode()	{
			return Arrays.deepHashCode(contents);
		}
		@Override
		public boolean equals(Object other)	{
			Status175 s=(Status175)other;
			for (int i=0;i<HEIGHT;++i) for (int j=0;j<WIDTH;++j) if (s.contents[i][j]!=contents[i][j]) return false;
			return true;
		}
		@Override
		public boolean isFinal() {
			for (int i=0;i<HEIGHT;++i) for (int j=0;j<WIDTH;++j) if (contents[i][j]!=0) return false;
			return true;
		}
		@Override
		public List<Move> availableMoves() {
			List<Move> result=new ArrayList<>();
			for (int i=0;i<HEIGHT;++i) for (int j=0;j<WIDTH;++j) if (contents[i][j]==0)	{
				Set<Integer> candidates=getAdjacents(i,j);
				for (int n:candidates) result.add(new Move(i,j,n));
			}
			return result;
		}
		@Override
		public Status175 move(Move move) {
			int[][] newContent=duplicate(contents);
			newContent[move.row][move.column]=move.id;
			Set<Point> chain=getChain(move.row,move.column,move.id);
			if (chain.size()>=move.id) for (Point p:chain) newContent[p.y][p.x]=0;
			return new Status175(newContent);
		}
		private Set<Integer> getAdjacents(int row,int column)	{
			List<Point> adjPos=getAdjacentPositions(row,column);
			Set<Integer> result=new HashSet<>();
			for (Point p:adjPos)	{
				int id=contents[p.y][p.x];
				if (id!=0) result.add(id);
			}
			return result;
		}
		private List<Point> getAdjacentPositions(int row,int column)	{
			List<Point> result=new ArrayList<>();
			if (column<WIDTH-1) result.add(new Point(column+1,row));
			if (row>0) result.add(new Point(column,row-1));
			if (column>0) result.add(new Point(column-1,row));
			if (row<HEIGHT-1) result.add(new Point(column,row+1));
			return result;
		}
		private static int[][] duplicate(int[][] array)	{
			int[][] result=new int[HEIGHT][WIDTH];
			for (int i=0;i<HEIGHT;++i) for (int j=0;j<WIDTH;++j) result[i][j]=array[i][j];
			return result;
		}
		private Set<Point> getChain(int row,int column,int id)	{
			Set<Point> result=new HashSet<>();
			result.add(new Point(column,row));
			for (Point adjacent:getAdjacentPositions(row,column)) addChain(result,adjacent,id);
			return result;
		}
		private void addChain(Set<Point> result,Point candidate,int id)	{
			if (contents[candidate.y][candidate.x]!=id) return;
			result.add(candidate);
			for (Point adjacent:getAdjacentPositions(candidate.y,candidate.x)) if (!result.contains(adjacent)) addChain(result,adjacent,id);
		}
	}
	
	public static void main(String[] args)	{
		Status175 initial=new Status175();
		BreadthSearch<Move,Status175> search=new BreadthSearch<>(initial);
		Collection<SearchPath<Move,Status175>> result=search.solve();
		if (result.isEmpty()) System.out.println("Leider habe ich keine Lösung gefunden!!!!!");
		else for (SearchPath<Move,Status175> path:result) path.print(System.out);
	}
}
