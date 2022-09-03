package com.euler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler213 {
	private final static int SIZE=30;
	private final static int ITERATIONS=50;
	
	private static class Position	{
		public final int x;
		public final int y;
		private final int hashCode;
		private static Table<Integer,Integer,Position> cache=HashBasedTable.create();
		public static Position of(int x,int y)	{
			Position res=cache.get(x,y);
			if (res==null)	{
				res=new Position(x,y);
				cache.put(x,y,res);
			}
			return res;
		}
		private Position(int x,int y)	{
			this.x=x;
			this.y=y;
			hashCode=(x*x)+(y*y*y);
		}
		public Position moveRight()	{
			return of(x+1,y);
		}
		public Position moveUp()	{
			return of(x,y+1);
		}
		public Position moveLeft()	{
			return of(x-1,y);
		}
		public Position moveDown()	{
			return of(x,y-1);
		}
		public List<Position> getNeighbours()	{
			List<Position> res=new ArrayList<>();
			if (x<SIZE-1) res.add(moveRight());
			if (y<SIZE-1) res.add(moveUp());
			if (x>0) res.add(moveLeft());
			if (y>0) res.add(moveDown());
			return res;
		}
		@Override
		public boolean equals(Object other)	{
			return ((Object)this)==other;
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public String toString()	{
			return "("+x+","+y+")";
		}
	}
	
	private static class PositionList	{
		public final List<Position> poss;
		public PositionList(int x,int y)	{
			poss=Collections.unmodifiableList(Position.of(x,y).getNeighbours());
		}
		@Override
		public String toString()	{
			return poss.toString();
		}
	}
	
	public static PositionList[][] getNeighbourhoods()	{
		PositionList[][] result=new PositionList[SIZE][SIZE];
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) result[i][j]=new PositionList(i,j);
		return result;
	}
	
	public static double[][] initialConfiguration()	{
		double[][] result=new double[SIZE][SIZE];
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) result[i][j]=1.0;
		return result;
	}

	public static void main(String[] args)	{
		PositionList[][] neighbourhoods=getNeighbourhoods();
		double[][] base=initialConfiguration();
		double[][] singleFrogPath=new double[SIZE][SIZE];
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j)	{
			for (int k=0;k<SIZE;++k) for (int l=0;l<SIZE;++l) singleFrogPath[k][l]=0.0;
			singleFrogPath[i][j]=1.0;
			for (int z=0;z<ITERATIONS;++z)	{
				double[][] nextIt=new double[SIZE][SIZE];
				for (int k=0;k<SIZE;++k) for (int l=0;l<SIZE;++l)	{
					if (singleFrogPath[k][l]==0.0) continue;
					double prob=singleFrogPath[k][l]/(double)neighbourhoods[k][l].poss.size();
					for (Position p:neighbourhoods[k][l].poss) nextIt[p.x][p.y]+=prob;
				}
				singleFrogPath=nextIt;
			}
			for (int k=0;k<SIZE;++k) for (int l=0;l<SIZE;++l) base[k][l]*=1-singleFrogPath[k][l];
		}
		double result=0.0;
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) result+=base[i][j];
		System.out.println(String.format(Locale.UK, "%.6f",result));
	}
}
