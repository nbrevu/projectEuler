package com.euler;

import java.util.Arrays;
import java.util.List;

public class Euler287_2 {
	private final static long N=24;
	
	private final static long SIZE=1l<<N;
	private final static long HALF_SIZE=1l<<(N-1);
	private final static long SQUARE_RADIUS=1l<<(2*N-2);
	
	private static boolean getPixelColour(long x,long y)	{
		long xd=x-HALF_SIZE;
		long yd=y-HALF_SIZE;
		return (xd*xd)+(yd*yd)<=SQUARE_RADIUS;
	}
	
	private static class Position	{
		private final long x,y;
		public Position(long x,long y)	{
			this.x=x;
			this.y=y;
		}
		public Position getDisplaced(long dx,long dy)	{
			return new Position(x+dx,y+dy);
		}
	}
	
	private static class Square	{
		private final Position p0;
		private final long size;
		public Square(Position p0,long size)	{
			this.p0=p0;
			this.size=size;
		}
		public boolean isSinglePixel()	{
			return size==1;
		}
		public List<Position> getCornerPixels()	{
			Position p1=p0.getDisplaced(0,size-1);
			Position p2=p0.getDisplaced(size-1,0);
			Position p3=p0.getDisplaced(size-1,size-1);
			return Arrays.asList(p0,p1,p2,p3);
		}
		public List<Square> getChildren()	{
			long halfSize=size/2;
			Position ix=p0.getDisplaced(halfSize,0);
			Position iy=p0.getDisplaced(0,halfSize);
			Position ixy=p0.getDisplaced(halfSize,halfSize);
			Square s0=new Square(p0,halfSize);
			Square s1=new Square(ix,halfSize);
			Square s2=new Square(iy,halfSize);
			Square s3=new Square(ixy,halfSize);
			return Arrays.asList(s0,s1,s2,s3);
		}
	}
	
	private static boolean isHomogeneous(Square sq)	{
		if (sq.size==SIZE) return false;
		int hmTrue=0;
		int hmFalse=0;
		for (Position p:sq.getCornerPixels())	{
			if (getPixelColour(p.x,p.y)) ++hmTrue;
			else ++hmFalse;
			if (hmTrue>0&&hmFalse>0) return false;
		}
		return true;
	}
	
	private static int getBestSequence(Square sq)	{
		if (sq.isSinglePixel()) return 2;
		else if (isHomogeneous(sq)) return 2;
		else	{
			int result=1;
			for (Square child:sq.getChildren()) result+=getBestSequence(child);
			return result;
		}
	}
	
	// TODO: algo no funciona...
	public static void main(String[] args)	{
		Square fullSquare=new Square(new Position(0l,0l),SIZE);
		System.out.println(getBestSequence(fullSquare));
	}
}
