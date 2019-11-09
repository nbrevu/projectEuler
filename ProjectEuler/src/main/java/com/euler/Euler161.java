package com.euler;

import java.util.Arrays;

import com.euler.common.Timing;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Euler161 {
	private final static int WIDTH=9;
	private final static int HEIGHT=12;
	
	private final static int SIZE=WIDTH*HEIGHT;
	
	private static enum Triomino	{
		ANGLED1(new int[] {0,1,WIDTH},0,1),
		ANGLED2(new int[] {0,1,WIDTH+1},0,1),
		ANGLED3(new int[] {0,WIDTH,WIDTH+1},0,1),
		ANGLED4(new int[] {0,WIDTH-1,WIDTH},1,0),
		HORIZONTAL(new int[] {0,1,2},0,2),
		VERTICAL(new int[] {0,WIDTH,2*WIDTH},0,0);
		
		public final int[] offsets;
		public final int minSpaceLeft;
		public final int minSpaceRight;
		private Triomino(int[] offsets,int minSpaceLeft,int minSpaceRight)	{
			this.offsets=offsets;
			this.minSpaceLeft=minSpaceLeft;
			this.minSpaceRight=minSpaceRight;
		}
	}
	
	private static class Board	{
		private boolean[] data;
		private final int hashCode;
		public Board()	{
			this(new boolean[SIZE]);
		}
		private Board(boolean[] data)	{
			this.data=data;
			hashCode=Arrays.hashCode(data);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public boolean equals(Object other)	{
			return Arrays.equals(data,((Board)other).data);
		}
		public Board tryAndPut(int baseSquare,Triomino triomino)	{
			int remLeft=baseSquare%WIDTH;
			int remRight=WIDTH-1-remLeft;
			if ((remLeft<triomino.minSpaceLeft)||(remRight<triomino.minSpaceRight)||((baseSquare+triomino.offsets[2])>=SIZE)) return null;
			boolean[] newData=Arrays.copyOf(data,data.length);
			for (int p:triomino.offsets) if (newData[baseSquare+p]) return null;
			else newData[baseSquare+p]=true;
			return new Board(newData);
		}
		public boolean isSet(int square)	{
			return data[square];
		}
	}
	
	private static class BoardRecursiveCalculator	{
		private final static Triomino[] PIECES=Triomino.values();
		private final ObjLongMap<Board> cache;
		public BoardRecursiveCalculator()	{
			cache=HashObjLongMaps.newMutableMap();
		}
		public long solve()	{
			return solveRecursive(new Board(),0);
		}
		private long solveRecursive(Board board,int square)	{
			long value=cache.getOrDefault(board,-1);
			if (value!=-1) return value;
			for (;;) if (square>=SIZE)	{
				cache.put(board,1l);
				return 1l;
			}	else if (board.isSet(square)) ++square;
			else break;
			value=0l;
			for (Triomino triomino:PIECES)	{
				Board newBoard=board.tryAndPut(square,triomino);
				if (newBoard!=null) value+=solveRecursive(newBoard,1+square);
			}
			cache.put(board,value);
			return value;
		}
	}
	
	private static long solve()	{
		return new BoardRecursiveCalculator().solve();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler161::solve);
	}
}
