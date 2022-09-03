package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_12 {
	// Well, this is MUCH faster (still slow though), but it's also wrong. Maybe I DO need to use "shifts". Also, bitsets should be cached.
	private final static int SIZE=15;
	
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
		private final List<BitSet> rows;
		private final int hashCode;
		public Sculpture()	{
			BitSet justTheCenter=new BitSet(2*SIZE);
			justTheCenter.set(SIZE);
			rows=List.of(justTheCenter);
			hashCode=rows.hashCode();
		}
		private Sculpture(Sculpture base,Position newBlock)	{
			if (newBlock.y<0)	{
				rows=new ArrayList<>(base.rows.size()+1);
				BitSet newRow=new BitSet(2*SIZE);
				newRow.set(newBlock.x);
				rows.add(newRow);
				rows.addAll(base.rows);
			}	else if (newBlock.y>=base.rows.size())	{
				rows=new ArrayList<>(base.rows.size()+1);
				rows.addAll(base.rows);
				BitSet newRow=new BitSet(2*SIZE);
				newRow.set(newBlock.x);
				rows.add(newRow);
			}	else	{
				rows=new ArrayList<>(base.rows);
				BitSet modified=(BitSet)(rows.get(newBlock.y).clone());
				modified.set(newBlock.x);
				rows.set(newBlock.y,modified);
			}
			hashCode=rows.hashCode();
		}
		public boolean contains(Position block)	{
			if ((block.y<0)||(block.y>=rows.size())) return false;
			else return rows.get(block.y).get(block.x);
		}
		public int size()	{
			int result=0;
			for (BitSet r:rows) result+=r.cardinality();
			return result;
		}
		public int getMassCenter()	{
			int sum=0;
			int N=size();
			for (BitSet r:rows)	for (int i=r.nextSetBit(0);i>=0;i=r.nextSetBit(1+i)) sum+=i;
			if ((sum%N)==0) return sum/N;
			return -1;
		}
		public boolean isAcceptable()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			return rows.get(0).get(massCenter);
		}
		public boolean isSymmetric()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			for (BitSet r:rows)	for (int i=r.nextSetBit(0);i>=0;i=r.nextSetBit(1+i)) if (!r.get(2*massCenter-i)) return false;
			return true;
		}
		public void getChildren(Set<Sculpture> result)	{
			Set<Position> neighbours=new HashSet<>();
			for (int y=0;y<rows.size();++y)	{
				BitSet r=rows.get(y);
				for (int x=r.nextSetBit(0);x>=0;x=r.nextSetBit(1+x))	{
					Position.of(x,y).getNeighbours(neighbours,this);
				}
			}
			for (Position p:neighbours) result.add(new Sculpture(this,p));
		}
		public void getValidChildren(Set<Sculpture> result)	{
			Set<Position> neighbours=new HashSet<>();
			for (int y=0;y<rows.size();++y)	{
				BitSet r=rows.get(y);
				for (int x=r.nextSetBit(0);x>=0;x=r.nextSetBit(1+x))	{
					Position.of(x,y).getNeighbours(neighbours,this);
				}
			}
			for (Position p:neighbours)	{
				Sculpture s=new Sculpture(this,p);
				if (s.isAcceptable()) result.add(s);
			}
		}
		@Override
		public boolean equals(Object other)	{
			Sculpture sOther=(Sculpture)other;
			return rows.equals(sOther.rows);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public String toString()	{
			return rows.toString();
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
