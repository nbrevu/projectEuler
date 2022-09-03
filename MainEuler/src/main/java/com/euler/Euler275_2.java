package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_2 {
	private final static int SIZE=12;
	
	private static class Position	{
		public final int x;
		public final int y;
		private final int hashCode;
		//private static Table<Integer,Integer,Position> cache=HashBasedTable.create();
		private static IntObjMap<IntObjMap<Position>> cache=HashIntObjMaps.newMutableMap();
		public static Position of(int x,int y)	{
			/*-
			Position res=cache.get(x,y);
			if (res==null)	{
				res=new Position(x,y);
				cache.put(x,y,res);
			}
			return res;
			*/
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
		public List<Position> getNeighbours()	{
			return Arrays.asList(moveRight(),moveUp(),moveLeft(),moveDown());
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
		public List<Sculpture> getChildren()	{
			Set<Position> neighbours=new HashSet<>();
			for (Position p:blocks) neighbours.addAll(p.getNeighbours());
			neighbours.removeAll(blocks);
			List<Sculpture> result=new ArrayList<>();
			for (Position p:neighbours) result.add(new Sculpture(this,p));
			return result;
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
		for (Sculpture sc:prev) result.addAll(sc.getChildren());
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Collection<Sculpture> prevGen=Collections.singleton(new Sculpture());
		for (int i=2;i<=SIZE;++i)	{
			Set<Sculpture> newGen=getNextGeneration(prevGen);
			System.out.println("Tamaño "+i+": "+newGen.size()+" elementos.");
			prevGen=newGen;
		}
		int sum=0;
		for (Sculpture s:prevGen) if (s.isAcceptable())	{
			//System.out.println(s);
			sum+=(s.isSymmetric())?2:1;
		}
		long tac=System.nanoTime();
		System.out.println(sum/2);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
