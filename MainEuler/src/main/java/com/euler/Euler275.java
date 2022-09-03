package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler275 {
	private final static int BLOCKS=6;
	private final static int[] TRIANGULAR=getTriangular(BLOCKS);
	
	private static int[] getTriangular(int max)	{
		int[] result=new int[max];
		for (int i=1;i<max;++i) result[i]=result[i-1]+i;
		return result;
	}
	
	private static class Position	{
		public final int x;
		public final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		public Position moveRight()	{
			return new Position(x+1,y);
		}
		public Position moveUp()	{
			return new Position(x,y+1);
		}
		public Position moveLeft()	{
			return new Position(x-1,y);
		}
		public Position moveDown()	{
			return new Position(x,y-1);
		}
		public List<Position> neighbours()	{
			return Arrays.asList(moveRight(),moveUp(),moveLeft(),moveDown());
		}
		@Override
		public boolean equals(Object other)	{
			Position pOther=(Position)other;
			return ((x==pOther.x)&&y==pOther.y);
		}
		@Override
		public int hashCode()	{
			return Integer.valueOf(x).hashCode()+Integer.valueOf(y).hashCode();
		}
		@Override
		public String toString()	{
			return "("+x+","+y+")";
		}
	}
	
	public static class Polyomino	{
		private Set<Position> positions;
		private NavigableSet<Integer> contactPointsCache=null;
		public Polyomino(Position pos)	{
			positions=Collections.singleton(pos);
		}
		public Polyomino(Set<Position> positions)	{
			this.positions=positions;
		}
		public Polyomino(Set<Position> previous,Position newBlock)	{
			this.positions=new HashSet<>(previous);
			this.positions.add(newBlock);
		}
		public int size()	{
			return positions.size();
		}
		public int weight()	{
			int sum=0;
			for (Position p:positions) sum+=p.x;
			return sum;
		}
		public NavigableSet<Integer> wallContactPoints()	{
			if (contactPointsCache!=null) return contactPointsCache;
			contactPointsCache=new TreeSet<>();
			for (Position p:positions) if (p.x==1) contactPointsCache.add(p.y);
			return contactPointsCache;
		}
		public int getWallContactPointsAsIntKey()	{
			NavigableSet<Integer> contactPoints=wallContactPoints();
			int res=0;
			for (int c:contactPoints) res+=1<<(c-1);
			return res;
		}
		public Set<Polyomino> getChildren(int maxBlocks)	{
			Set<Position> neighbours=new HashSet<>();
			for (Position p:positions) neighbours.addAll(p.neighbours());
			Set<Polyomino> result=new HashSet<>();
			for (Position p:neighbours) if (!positions.contains(p)&&(p.x>0))	{
				Polyomino newPoly=new Polyomino(positions,p);
				int remaining=maxBlocks-1-newPoly.size();
				if (remaining<1) continue;
				if (newPoly.weight()<=TRIANGULAR[remaining]) result.add(newPoly.rebase());
			}
			return result;
		}
		private Polyomino rebase()	{
			int minHeight=Integer.MAX_VALUE;
			for (Position p:positions) minHeight=Math.min(minHeight,p.y);
			if (minHeight==0) return this;
			Set<Position> newPoss=new HashSet<>();
			for (Position p:positions) newPoss.add(new Position(p.x,p.y-minHeight));
			return new Polyomino(newPoss);
		}
		@Override
		public boolean equals(Object other)	{
			Polyomino pOther=(Polyomino)other;
			return positions.equals(pOther.positions);
		}
		@Override
		public int hashCode()	{
			return positions.hashCode();
		}
		@Override
		public String toString()	{
			return positions.toString();
		}
	}
	
	private static NavigableMap<Integer,Set<Polyomino>> createPolyominosUpTo(int maxBlocks)	{
		NavigableMap<Integer,Set<Polyomino>> sortedByBlocks=new TreeMap<>();
		sortedByBlocks.put(1,Collections.singleton(new Polyomino(new Position(1,0))));
		for (int i=1;;++i)	{
			Set<Polyomino> oldSet=sortedByBlocks.get(i);
			Set<Polyomino> newSet=new HashSet<>();
			System.out.println(""+i+" bloques: "+oldSet.size()+" polyominos.");
			for (Polyomino p:oldSet) newSet.addAll(p.getChildren(maxBlocks));
			if (newSet.isEmpty()) break;
			sortedByBlocks.put(i+1,newSet);
		}
		return sortedByBlocks;
	}
	
	private static NavigableMap<Integer,List<Polyomino>> sortByWeight(NavigableMap<Integer,? extends Set<Polyomino>> sortedByBlocks)	{
		NavigableMap<Integer,List<Polyomino>> sortedByWeight=new TreeMap<>();
		for (Set<Polyomino> set:sortedByBlocks.values()) for (Polyomino p:set)	{
			int weight=p.weight();
			List<Polyomino> thisList=sortedByWeight.get(weight);
			if (thisList==null)	{
				thisList=new ArrayList<>();
				sortedByWeight.put(weight,thisList);
			}
			thisList.add(p);
		}
		return sortedByWeight;
	}
	
	private static Map<Integer,Map<Integer,Long>> symmetricCombinations=new HashMap<>();
	private static Table<Integer,Integer,Map<Integer,Long>> nonSymmetricCombinations=HashBasedTable.create();
	
	private static class Combination	{
		private static class Position	{
			private final int relativeToPolyomino;
			private final int relativeToAxis;
			public Position()	{
				relativeToPolyomino=-1;
				relativeToAxis=-1;
			}
			public Position(int relativeToPolyomino,int relativeToAxis)	{
				this.relativeToPolyomino=relativeToPolyomino;
				this.relativeToAxis=relativeToAxis;
			}
			public boolean isSet()	{
				return relativeToPolyomino>=0;
			}
			@Override
			public boolean equals(Object other)	{
				Position pOther=(Position)other;
				return (relativeToAxis-relativeToPolyomino)==(pOther.relativeToAxis-pOther.relativeToPolyomino);
			}
			@Override
			public int hashCode()	{
				return relativeToAxis-relativeToPolyomino;
			}
		}
		private final int verticalPositions;	// Codified as bits.
		private final int remaining;
		private final Position leftPosition;
		private final Position rightPosition;
		public Combination(int amount)	{
			verticalPositions=1;
			remaining=amount-1;
			leftPosition=new Position();
			rightPosition=new Position();
		}
		private Combination(Combination parent,Position pos,boolean useRight)	{
			verticalPositions=parent.verticalPositions;
			remaining=parent.remaining;
			if (useRight)	{
				leftPosition=parent.leftPosition;
				rightPosition=pos;
			}	else	{
				leftPosition=pos;
				rightPosition=parent.rightPosition;
			}
		}
		private Combination(Combination parent,Position left,Position right)	{
			verticalPositions=parent.verticalPositions;
			remaining=parent.remaining;
			leftPosition=left;
			rightPosition=right;
		}
		private Combination(Combination parent,int newVertical)	{
			verticalPositions=parent.verticalPositions|(1<<newVertical);
			remaining=parent.remaining-1;
			leftPosition=parent.leftPosition;
			rightPosition=parent.rightPosition;
		}
		private static NavigableSet<Integer> getAvailablePoss(Position pos,Set<Integer> contacts)	{
			if (!pos.isSet()) return new TreeSet<>();
			NavigableSet<Integer> result=new TreeSet<>();
			int diff=pos.relativeToAxis-pos.relativeToPolyomino;
			for (int contact:contacts) result.add(contact+diff);
			return result;
		}
		private int getLatestPosition()	{
			int result=0;
			int n=verticalPositions;
			while (n>1)	{
				++result;
				n>>=1;
			}
			return result;
		}
		public List<Combination> getChildren(Set<Integer> poly1,Set<Integer> poly2,boolean allowRightBeforeLeft)	{
			int latest=getLatestPosition();
			List<Combination> result=new ArrayList<>();
			if (!leftPosition.isSet()) for (int pos:poly1)	{
				if (pos<=latest) result.add(new Combination(this,new Position(latest,pos),false));
				else break;
			}
			if ((!rightPosition.isSet())&&(leftPosition.isSet()||allowRightBeforeLeft)) for (int pos:poly2)	{
				if (pos<=latest) result.add(new Combination(this,new Position(latest,pos),true));
				else break;
			}
			if ((!leftPosition.isSet())&&(!rightPosition.isSet()))	{
				for (int pos1:poly1) if (pos1<=latest)	{
					for (int pos2:poly2) if (pos2<=latest) result.add(new Combination(this,new Position(latest,pos1),new Position(latest,pos2)));
					else break;
				}	else break;
			}
			if (remaining>0)	{
				NavigableSet<Integer> available=getAvailablePoss(leftPosition,poly1);
				available.addAll(getAvailablePoss(rightPosition,poly2));
				available.add(latest+1);
				available=available.tailSet(latest,false);
				for (int pos:available) result.add(new Combination(this,pos));
			}
			return result;
		}
		public boolean isFinished()	{
			return (remaining==0)&&leftPosition.isSet()&&rightPosition.isSet();
		}
		@Override
		public boolean equals(Object other)	{
			Combination cOther=(Combination)other;
			return ((verticalPositions==cOther.verticalPositions)&&(remaining==cOther.remaining)&&(leftPosition.equals(cOther.leftPosition))&&(rightPosition.equals(cOther.rightPosition)));
		}
		@Override
		public int hashCode() {
			int prime=31;
			int result=prime+Combination.class.hashCode();
			result=prime*result+remaining;
			result=prime*result+verticalPositions;
			result=prime*result+leftPosition.hashCode();
			return prime*result+rightPosition.hashCode();
		}
	}
	
	private static long calculateCombinations(Polyomino symPoly,int vBlocks) {
		Set<Combination> analyzed=new HashSet<>();
		Set<Combination> pending=new HashSet<>();
		pending.add(new Combination(vBlocks));
		Set<Integer> contactPoints=symPoly.wallContactPoints();
		long result=0;
		while (!pending.isEmpty())	{
			Combination comb=pending.iterator().next();
			pending.remove(comb);
			if (analyzed.contains(comb)) continue;
			if (comb.isFinished()) ++result;
			else pending.addAll(comb.getChildren(contactPoints,contactPoints,false));
			analyzed.add(comb);
		}
		return result;
	}

	private static long getCombinations(Polyomino symPoly,int vBlocks) {
		int key=symPoly.getWallContactPointsAsIntKey();
		Map<Integer,Long> internalMap=symmetricCombinations.get(key);
		if (internalMap==null)	{
			internalMap=new HashMap<>();
			symmetricCombinations.put(key,internalMap);
		}
		Long value=internalMap.get(vBlocks);
		if (value==null)	{
			value=calculateCombinations(symPoly,vBlocks);
			internalMap.put(vBlocks,value);
		}
		return value;
	}

	private static long calculateCombinations(Polyomino polyA,Polyomino polyB,int vBlocks) {
		Set<Combination> analyzed=new HashSet<>();
		Set<Combination> pending=new HashSet<>();
		pending.add(new Combination(vBlocks));
		Set<Integer> contactPoints1=polyA.wallContactPoints();
		Set<Integer> contactPoints2=polyB.wallContactPoints();
		long result=0;
		while (!pending.isEmpty())	{
			Combination comb=pending.iterator().next();
			pending.remove(comb);
			if (analyzed.contains(comb)) continue;
			if (comb.isFinished()) ++result;
			else pending.addAll(comb.getChildren(contactPoints1,contactPoints2,true));
			analyzed.add(comb);
		}
		return result;
	}

	private static long getCombinations(Polyomino polyA,Polyomino polyB,int vBlocks) {
		int key1=polyA.getWallContactPointsAsIntKey();
		int key2=polyB.getWallContactPointsAsIntKey();
		Map<Integer,Long> internalMap=nonSymmetricCombinations.get(key1,key2);
		if (internalMap==null)	{
			internalMap=new HashMap<>();
			nonSymmetricCombinations.put(key1,key2,internalMap);
			nonSymmetricCombinations.put(key2,key1,internalMap);
		}
		Long value=internalMap.get(vBlocks);
		if (value==null)	{
			value=calculateCombinations(polyA,polyB,vBlocks);
			internalMap.put(vBlocks,value);
		}
		return value;
	}

	private static long getCombinations(List<Polyomino> fixedWeight,int blocks)	{
		int N=fixedWeight.size();
		long sum=0l;
		for (int i=0;i<N;++i)	{
			Polyomino polyA=fixedWeight.get(i);
			int sizeA=polyA.size();
			{
				int vertical=blocks-2*sizeA;
				if (vertical>=1) sum+=getCombinations(polyA,vertical);
			}
			for (int j=i+1;j<N;++j)	{
				Polyomino polyB=fixedWeight.get(j);
				int sizeB=polyB.size();
				int vertical=blocks-(sizeA+sizeB);
				if (vertical>=1) sum+=getCombinations(polyA,polyB,vertical);
			}
		}
		return sum;
	}
	
	public static void main(String[] args)	{
		NavigableMap<Integer,Set<Polyomino>> sortedByBlocks=createPolyominosUpTo(BLOCKS);
		NavigableMap<Integer,List<Polyomino>> sortedByWeight=sortByWeight(sortedByBlocks);
		long count=1l;	// This is the "completely vertical".
		for (List<Polyomino> fixedWeight:sortedByWeight.values()) count+=getCombinations(fixedWeight,BLOCKS);
		System.out.println(count);
	}
}
