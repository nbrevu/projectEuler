package com.euler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.euler.common.EulerUtils.Pair;
import com.google.common.collect.Sets;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_10 {
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
		public Set<Position> getNeighbours()	{
			return Sets.newHashSet(moveRight(),moveUp(),moveLeft(),moveDown());
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
	
	private static class NextGenInfo	{
		private final Set<Position> alreadyVisited;
		private final Set<Position> nextToVisit;
		private NextGenInfo(Set<Position> alreadyVisited,Set<Position> nextToVisit)	{
			this.alreadyVisited=alreadyVisited;
			this.nextToVisit=nextToVisit;
		}
		public static NextGenInfo initForSingleBlock(Position block)	{
			return new NextGenInfo(Set.of(),block.getNeighbours());
		}
		public NextGenInfo mixWith(NextGenInfo other)	{
			Set<Position> newAlreadyVisited=new HashSet<>(alreadyVisited);
			newAlreadyVisited.addAll(other.alreadyVisited);
			Set<Position> newNextToVisit=new HashSet<>(nextToVisit);
			newNextToVisit.retainAll(other.nextToVisit);
			return new NextGenInfo(newAlreadyVisited,newNextToVisit);
		}
		public static Set<Position> shift(Set<Position> baseSet,Function<Position,Position> updateFun)	{
			return baseSet.stream().map(updateFun).collect(Collectors.toUnmodifiableSet());
		}
		public Pair<Sculpture,NextGenInfo> evolve(Sculpture sculpture,Position newBlock)	{
			Set<Position> newAlreadyVisited=new HashSet<>(alreadyVisited);
			Set<Position> newNextToVisit=new HashSet<>(nextToVisit);
			newAlreadyVisited.add(newBlock);
			newNextToVisit.remove(newBlock);
			Set<Position> neighbours=newBlock.getNeighbours();
			neighbours.removeAll(newAlreadyVisited);
			newNextToVisit.addAll(neighbours);
			if (newBlock.x<=0)	{
				newAlreadyVisited=shift(newAlreadyVisited,Position::moveRight);
				newNextToVisit=shift(newNextToVisit,Position::moveRight);
			}	else if (newBlock.y<=0)	{
				newAlreadyVisited=shift(newAlreadyVisited,Position::moveUp);
				newNextToVisit=shift(newNextToVisit,Position::moveUp);
			}
			Sculpture result1=new Sculpture(sculpture,newBlock);
			NextGenInfo result2=new NextGenInfo(newAlreadyVisited,newNextToVisit);
			return new Pair<>(result1,result2);
		}
		public static void evolve(Sculpture sculpture,NextGenInfo infoToEvolve,Map<Sculpture,NextGenInfo> result)	{
			for (Position block:infoToEvolve.nextToVisit)	{
				Pair<Sculpture,NextGenInfo> evolved=infoToEvolve.evolve(sculpture,block);
				result.merge(evolved.first,evolved.second,NextGenInfo::mixWith);
			}
		}
		public static Map<Sculpture,NextGenInfo> evolve(Map<Sculpture,NextGenInfo> previousGen)	{
			Map<Sculpture,NextGenInfo> result=new HashMap<>();
			for (Map.Entry<Sculpture,NextGenInfo> entry:previousGen.entrySet()) evolve(entry.getKey(),entry.getValue(),result);
			return result;
		}
		public static Set<Sculpture> evolveSimple(Map<Sculpture,NextGenInfo> previousGen)	{
			Set<Sculpture> result=new HashSet<>();
			for (Map.Entry<Sculpture,NextGenInfo> entry:previousGen.entrySet())	{
				Sculpture s=entry.getKey();
				for (Position p:entry.getValue().nextToVisit) result.add(new Sculpture(s,p));
			}
			return result;
		}
	}
	
	public static class Sculpture	{
		private final Set<Position> blocks;
		private final int hashCode;
		public Sculpture()	{
			blocks=Collections.singleton(Position.of(1,1));
			hashCode=blocks.hashCode();
		}
		public Sculpture(Sculpture base,Position newBlock)	{
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
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Map<Sculpture,NextGenInfo> currentGen=Map.of(new Sculpture(),NextGenInfo.initForSingleBlock(Position.of(1,1)));
		for (int i=2;i<SIZE;++i)	{
			System.out.println(i+"...");
			currentGen=NextGenInfo.evolve(currentGen);
		}
		System.out.println(SIZE+"...");
		Set<Sculpture> finalSet=NextGenInfo.evolveSimple(currentGen);
		int sum=0;
		for (Sculpture s:finalSet) if (s.isAcceptable()) sum+=(s.isSymmetric())?2:1;
		int result=sum/2;
		long tac=System.nanoTime();
		System.out.println(result);
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
