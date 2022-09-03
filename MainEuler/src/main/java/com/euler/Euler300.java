package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler300 {
	// Aaaand another one at the first try :). This was ultra-slow, though.
	private final static int LENGTH=15;
	
	private static class Position	{
		public final int x;
		public final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		public boolean isAdjacent(Position other)	{
			int manhattan=Math.abs(x-other.x)+Math.abs(y-other.y);
			return (manhattan==1);
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
		@Override
		public boolean equals(Object other)	{
			Position pOther=(Position)other;
			return (x==pOther.x)&&(y==pOther.y);
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
	
	private static class FoldingSchema	{
		private FoldingSchema schemaUpToThis;
		private Position lastSquare;
		public FoldingSchema(Position initial)	{
			schemaUpToThis=null;
			lastSquare=initial;
		}
		private FoldingSchema(FoldingSchema previous,Position current)	{
			schemaUpToThis=previous;
			lastSquare=current;
		}
		public List<Position> getOrderedPositions()	{
			List<Position> upToThis=((schemaUpToThis==null)?new ArrayList<>():schemaUpToThis.getOrderedPositions());
			upToThis.add(lastSquare);
			return upToThis;
		}
		private boolean containsPosition(Position square)	{
			if (lastSquare.equals(square)) return true;
			if (schemaUpToThis==null) return false;
			return schemaUpToThis.containsPosition(square);
		}
		public boolean canMoveRight()	{
			return !containsPosition(lastSquare.moveRight());
		}
		public FoldingSchema moveRight()	{
			return new FoldingSchema(this,lastSquare.moveRight());
		}
		public boolean canMoveUp()	{
			return !containsPosition(lastSquare.moveUp());
		}
		public FoldingSchema moveUp()	{
			return new FoldingSchema(this,lastSquare.moveUp());
		}
		public boolean canMoveLeft()	{
			return !containsPosition(lastSquare.moveLeft());
		}
		public FoldingSchema moveLeft()	{
			return new FoldingSchema(this,lastSquare.moveLeft());
		}
		public boolean canMoveDown()	{
			return !containsPosition(lastSquare.moveDown());
		}
		public FoldingSchema moveDown()	{
			return new FoldingSchema(this,lastSquare.moveDown());
		}
		public void addChildren(Collection<FoldingSchema> schemas)	{
			if (canMoveRight()) schemas.add(moveRight());
			if (canMoveUp()) schemas.add(moveUp());
			if (canMoveLeft()) schemas.add(moveLeft());
			if (canMoveDown()) schemas.add(moveDown());
		}
		public Table<Integer,Integer,Boolean> getAdjacencyMatrix()	{
			List<Position> poss=getOrderedPositions();
			int N=poss.size();
			Table<Integer,Integer,Boolean> result=HashBasedTable.create(N-1,N-1);
			for (int i=0;i<N-1;++i) for (int j=i+1;j<N;++j) result.put(i,j,poss.get(i).isAdjacent(poss.get(j)));
			return result;
		}
		public int size()	{
			return 1+((schemaUpToThis==null)?0:(schemaUpToThis.size()));
		}
		@Override
		public String toString()	{
			return ((schemaUpToThis==null)?"":((schemaUpToThis.toString()+"-")))+lastSquare.toString();
		}
	}
	
	private static Map<Integer,List<FoldingSchema>> createAllSchemas(int upTo)	{
		Map<Integer,List<FoldingSchema>> result=new HashMap<>();
		FoldingSchema base=new FoldingSchema(new Position(0,0));
		result.put(1,Arrays.asList(base));
		FoldingSchema base2=base.moveRight();
		result.put(2,Arrays.asList(base2));
		List<FoldingSchema> prev=Arrays.asList(base2.moveRight(),base2.moveUp());
		result.put(3,prev);
		for (int i=4;i<=upTo;++i)	{
			List<FoldingSchema> curr=new ArrayList<>();
			for (FoldingSchema schema:prev) schema.addChildren(curr);
			result.put(i,curr);
			prev=curr;
		}
		return result;
	}
	
	private static class FoldChecker	{
		private Set<Table<Integer,Integer,Boolean>> schemas;
		private int length;
		public FoldChecker(List<FoldingSchema> schemaList)	{
			System.out.println("I have "+schemaList.size()+" schemata...");
			schemas=new HashSet<>();
			boolean first=true;
			length=0;
			int counter=0;
			for (FoldingSchema schema:schemaList)	{
				if (first)	{
					first=false;
					length=schema.size();
				}	else assert (schema.size()==length);
				schemas.add(schema.getAdjacencyMatrix());
				++counter;
				if ((counter%1000)==0) System.out.println("Index "+counter+"...");
			}
			System.out.println("In the end, I have "+schemas.size()+" contact configurations.");
		}
		private boolean[] intToBits(int in)	{
			boolean[] result=new boolean[length];
			for (int i=0;i<length;++i)	{
				result[i]=((in%2)==1)?true:false;
				in/=2;
			}
			return result;
		}
		private static int checkTable(boolean[] bits,Table<Integer,Integer,Boolean> contactPoints)	{
			int counter=0;
			int N=bits.length;
			for (int i=0;i<N-1;++i)	{
				if (!bits[i]) continue;
				for (int j=i+1;j<N;++j) if (bits[j]&&contactPoints.get(i,j)) ++counter;
			}
			return counter;
		}
		public int checkInput(int in)	{
			boolean[] bits=intToBits(in);
			int max=0;
			for (Table<Integer,Integer,Boolean> table:schemas) max=Math.max(max,checkTable(bits,table));
			return max;
		}
	}
	
	public static void main(String[] args)	{
		Map<Integer,List<FoldingSchema>> schemas=createAllSchemas(LENGTH);
		// for (Map.Entry<Integer,List<FoldingSchema>> entry:schemas.entrySet()) System.out.println(""+entry.getKey()+" => "+entry.getValue().size());
		FoldChecker checker=new FoldChecker(schemas.get(LENGTH));
		int maxSize=1<<LENGTH;
		int counter=0;
		for (int i=0;i<maxSize;++i)	{
			counter+=checker.checkInput(i);
			if ((i%1000)==0) System.out.println(i);
		}
		System.out.println(""+counter+"/"+maxSize+"="+(((double)counter)/((double)maxSize)));
	}
}
