package com.euler;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_13 {
	private final static int SIZE=18;
	
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
	private static BitSet[] createBitSetCache(int bitSize)	{
		int size=1<<bitSize;
		long[] holder=new long[1];
		BitSet[] result=new BitSet[size];
		for (int i=0;i<size;++i)	{
			holder[0]=i;
			result[i]=BitSet.valueOf(holder);
		}
		return result;
	}
	public static class Sculpture	{
		private final static BitSet[] BITSET_CACHE=createBitSetCache(SIZE);
		private final int[] rows;
		private final int hashCode;
		public Sculpture()	{
			rows=new int[] {1};
			hashCode=Arrays.hashCode(rows);
		}
		private Sculpture(Sculpture base,Position newBlock)	{
			if (newBlock.y<0)	{
				rows=new int[base.rows.length+1];
				System.arraycopy(base.rows,0,rows,1,base.rows.length);
				rows[0]=1<<newBlock.x;
			}	else if (newBlock.y>=base.rows.length)	{
				rows=new int[base.rows.length+1];
				System.arraycopy(base.rows,0,rows,0,base.rows.length);
				rows[base.rows.length]=1<<newBlock.x;
			}	else if (newBlock.x<0)	{
				rows=new int[base.rows.length];
				for (int i=0;i<base.rows.length;++i) rows[i]=base.rows[i]<<1;
				rows[newBlock.y]|=1;
			}	else	{
				rows=Arrays.copyOf(base.rows,base.rows.length);
				rows[newBlock.y]|=1<<newBlock.x;
			}
			hashCode=Arrays.hashCode(rows);
		}
		public boolean contains(Position block)	{
			if ((block.x<0)||(block.y<0)||(block.y>=rows.length)) return false;
			else return (rows[block.y]&(1<<block.x))!=0;
		}
		public int size()	{
			int result=0;
			for (int r:rows) result+=BITSET_CACHE[r].cardinality();
			return result;
		}
		public int getMassCenter()	{
			int sum=0;
			int N=size();
			for (int idx:rows)	{
				BitSet r=BITSET_CACHE[idx];
				for (int i=r.nextSetBit(0);i>=0;i=r.nextSetBit(1+i)) sum+=i;
			}
			if ((sum%N)==0) return sum/N;
			return -1;
		}
		public boolean isAcceptable()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			else return (rows[0]&(1<<massCenter))!=0;
		}
		public boolean isSymmetric()	{
			int massCenter=getMassCenter();
			if (massCenter==-1) return false;
			for (int idx:rows)	{
				BitSet r=BITSET_CACHE[idx];
				for (int i=r.nextSetBit(0);i>=0;i=r.nextSetBit(1+i))	{
					int symPos=2*massCenter-i;
					if ((symPos<0)||!r.get(symPos)) return false;
				}
			}
			return true;
		}
		public void getChildren(Set<Sculpture> result)	{
			Set<Position> neighbours=new HashSet<>();
			for (int y=0;y<rows.length;++y)	{
				BitSet r=BITSET_CACHE[rows[y]];
				for (int x=r.nextSetBit(0);x>=0;x=r.nextSetBit(1+x))	{
					Position.of(x,y).getNeighbours(neighbours,this);
				}
			}
			for (Position p:neighbours) result.add(new Sculpture(this,p));
		}
		public void getValidChildren(Set<Sculpture> result)	{
			Set<Position> neighbours=new HashSet<>();
			for (int y=0;y<rows.length;++y)	{
				BitSet r=BITSET_CACHE[rows[y]];
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
			return Arrays.equals(rows,sOther.rows);
		}
		@Override
		public int hashCode()	{
			return hashCode;
		}
		@Override
		public String toString()	{
			return Arrays.toString(rows);
		}
	}
	
	private static Set<Sculpture> getNextGeneration(Collection<Sculpture> prev)	{
		Set<Sculpture> result=new HashSet<>(4*prev.size());
		int nextToShow=1000000;
		int current=0;
		Iterator<Sculpture> iterator=prev.iterator();
		while (iterator.hasNext())	{
			++current;
			if (current==nextToShow)	{
				nextToShow+=1000000;
				System.out.println("\t"+current+"...");
			}
			iterator.next().getChildren(result);
			iterator.remove();
		}
		return result;
	}
	private static Set<Sculpture> getNextGenerationRestricted(Collection<Sculpture> prev)	{
		Set<Sculpture> result=new HashSet<>(4*prev.size());
		int nextToShow=1000000;
		int current=0;
		Iterator<Sculpture> iterator=prev.iterator();
		while (iterator.hasNext())	{
			++current;
			if (current==nextToShow)	{
				nextToShow+=1000000;
				System.out.println("\t"+current+"...");
			}
			iterator.next().getValidChildren(result);
			iterator.remove();
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Collection<Sculpture> prevGen=Sets.newHashSet(new Sculpture());
		for (int i=2;i<SIZE;++i)	{
			Set<Sculpture> newGen=getNextGeneration(prevGen);
			System.out.println("Tamaño "+i+": "+newGen.size()+" elementos.");
			prevGen=newGen;
		}
		Set<Sculpture> lastGen=getNextGenerationRestricted(prevGen);
		System.out.println("Tamaño "+SIZE+" (restringido): "+lastGen.size()+" elementos.");
		int sum=0;
		for (Sculpture s:lastGen) sum+=(s.isSymmetric())?2:1;
		long tac=System.nanoTime();
		System.out.println(sum/2);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
