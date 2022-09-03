package com.euler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_11 {
	private final static int SIZE=14;
	
	private static class Position	{
		public final int x;
		public final int y;
		private final int hashCode;
		private static IntObjMap<IntObjMap<Position>> cache=HashIntObjMaps.newMutableMap();
		public static Position of(int x,int y)	{
			IntObjMap<Position> subCache=cache.computeIfAbsent(x,(int unused)->HashIntObjMaps.newMutableMap());
			return subCache.computeIfAbsent(y,(int unused)->new Position(x,y));
		}
		private Position(int x,int y)	{
			this.x=x;
			this.y=y;
			hashCode=SIZE*x+y;
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
		public void getNeighbours(Set<Position> result,Sculpture s)	{
			Position r=moveRight();
			if (!s.contains(r)) result.add(r);
			Position u=moveUp();
			if (!s.contains(u)) result.add(u);
			Position l=moveLeft();
			if (!s.contains(l)) result.add(l);
			Position d=moveDown();
			if (!s.contains(d)) result.add(d);
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
	public static class Sculpture	{
		private final Set<Position> blocks;
		private final int hashCode;
		public Sculpture()	{
			blocks=Collections.singleton(Position.of(1,1));
			hashCode=blocks.hashCode();
		}
		private Sculpture(Sculpture base,Position newBlock)	{
			int incX=((newBlock.x==0)?1:0);
			int incY=((newBlock.y==0)?1:0);
			blocks=new HashSet<>();
			for (Position p:base.blocks) blocks.add(Position.of(p.x+incX,p.y+incY));
			blocks.add(Position.of(newBlock.x+incX,newBlock.y+incY));
			hashCode=blocks.hashCode();
		}
		public boolean contains(Position block)	{
			return blocks.contains(block);
		}
		public int size()	{
			return blocks.size();
		}
		public int getMassCenter()	{
			int sum=0;
			int N=size();
			for (Position p:blocks) sum+=p.x;
			if ((sum%N)==0) return sum/N;
			return -1;
		}
		public boolean isAcceptable()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			return blocks.contains(Position.of(massCenter,1));
		}
		public boolean isSymmetric()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			for (Position p:blocks) if (!blocks.contains(Position.of(2*massCenter-p.x,p.y))) return false;
			return true;
		}
		public void getChildren(Set<Sculpture> result)	{
			Set<Position> neighbours=new HashSet<>();
			for (Position p:blocks) p.getNeighbours(neighbours,this);
			for (Position p:neighbours) result.add(new Sculpture(this,p));
		}
		public void getValidChildren(Set<Sculpture> result)	{
			Set<Position> neighbours=new HashSet<>();
			for (Position p:blocks) p.getNeighbours(neighbours,this);
			for (Position p:neighbours)	{
				Sculpture s=new Sculpture(this,p);
				if (s.isAcceptable()) result.add(s);
			}
		}
		@Override
		public boolean equals(Object other)	{
			Sculpture sOther=(Sculpture)other;
			return blocks.equals(sOther.blocks);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public String toString()	{
			return blocks.toString();
		}
	}
	
	private static Set<Sculpture> getNextGeneration(Collection<Sculpture> prev)	{
		Set<Sculpture> result=new HashSet<>(4*prev.size());
		for (Sculpture sc:prev) sc.getChildren(result);
		return result;
	}
	private static Set<Sculpture> getNextGenerationRestricted(Collection<Sculpture> prev)	{
		Set<Sculpture> result=new HashSet<>(4*prev.size());
		for (Sculpture sc:prev) sc.getValidChildren(result);
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Collection<Sculpture> prevGen=Collections.singleton(new Sculpture());
		for (int i=2;i<SIZE;++i)	{
			Set<Sculpture> newGen=getNextGeneration(prevGen);
			System.out.println("Tamaño "+i+": "+newGen.size()+" elementos.");
			prevGen=newGen;
		}
		Set<Sculpture> lastGen=getNextGenerationRestricted(prevGen);
		System.out.println("Tamaño "+SIZE+"(restringido): "+lastGen.size()+" elementos.");
		int sum=0;
		for (Sculpture s:lastGen) if (s.isAcceptable()) sum+=(s.isSymmetric())?2:1;
		long tac=System.nanoTime();
		System.out.println(sum/2);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
