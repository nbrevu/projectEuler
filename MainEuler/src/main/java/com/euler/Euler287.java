package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Euler287 {
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
	
	private static class Square implements Iterable<Position>	{
		private final Position p0;
		private final long size;
		private class BorderIterator implements Iterator<Position>	{
			private long i=0;
			private final long s1=size-1;
			private Iterator<Position> current;
			public BorderIterator()	{
				i=0l;
				current=Collections.emptyIterator();
			}
			@Override
			public boolean hasNext() {
				if (current.hasNext()) return true;
				else return i<s1;
			}
			@Override
			public Position next() {
				if (current.hasNext()) return current.next();
				if (i>=s1) throw new NoSuchElementException();
				List<Position> nextFour=new ArrayList<>(4);
				nextFour.add(p0.getDisplaced(0,i));
				nextFour.add(p0.getDisplaced(i,s1));
				nextFour.add(p0.getDisplaced(s1,s1-i));
				nextFour.add(p0.getDisplaced(s1-i,0));
				current=nextFour.iterator();
				++i;
				return current.next();
			}
		}
		public Square(Position p0,long size)	{
			this.p0=p0;
			this.size=size;
		}
		public boolean isSinglePixel()	{
			return size==1;
		}
		public Iterator<Position> iterator()	{
			return new BorderIterator();
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
		boolean someFalse=false;
		boolean someTrue=false;
		for (Position p:sq)	{
			if (getPixelColour(p.x,p.y)) someTrue=true;
			else someFalse=true;
			if (someTrue&&someFalse) return false;
		}
		return true;
	}
	
	private static long getBestSequence(Square sq)	{
		if (sq.isSinglePixel()) return 2;
		else if (isHomogeneous(sq)) return 2;
		else	{
			long result=1;
			for (Square child:sq.getChildren()) result+=getBestSequence(child);
			return result;
		}
	}
	
	// TODO: algo no funciona...
	// Toda esta molestia para un puto error de tipos (1 en vez de 1l, y al desplazar 32 bits ya la hemos jodido).
	public static void main(String[] args)	{
		Square fullSquare=new Square(new Position(0l,0l),SIZE);
		System.out.println(getBestSequence(fullSquare));
	}
}
