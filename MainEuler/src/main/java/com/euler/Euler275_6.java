package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Euler275_6 {
	private final static int SIZE=18;
	
	private static class SideDistribution	{
		private final int[] blocks;
		public final int size;
		public final int weight;
		public SideDistribution(int[] blocks)	{
			this.blocks=blocks;
			int s=0;
			int w=0;
			for (int i=0;i<blocks.length;++i)	{
				s+=blocks[i];
				w+=(i+1)*blocks[i];
			}
			size=s;
			weight=w;
		}
		@Override
		public String toString()	{
			return Arrays.toString(blocks);
		}
	}
	
	private static NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> getAllDistributions(int size,int maxWeight)	{
		List<Integer> holder=new ArrayList<>();
		NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> result=new TreeMap<>();
		getAllDistributionsRecursive(size,maxWeight,holder,result);
		return result;
	}
	
	private static void getAllDistributionsRecursive(int remaining,int maxWeight,List<Integer> holder,NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> result)	{
		int n=holder.size();
		holder.add(0);
		for (int i=1;i<=remaining;++i)	{
			holder.set(n,i);
			if (i==remaining)	{
				int[] array=holder.stream().mapToInt(Integer::intValue).toArray();
				SideDistribution distribution=new SideDistribution(array);
				if (distribution.weight<=maxWeight)	{
					NavigableMap<Integer,List<SideDistribution>> subMap=result.computeIfAbsent(distribution.weight,(Integer unused)->new TreeMap<>());
					List<SideDistribution> subList=subMap.computeIfAbsent(distribution.size,(Integer unused)->new ArrayList<>());
					subList.add(distribution);
				}
			}	else getAllDistributionsRecursive(remaining-i,maxWeight,holder,result);
		}
		holder.remove(n);
	}
	
	private static void addAll(NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> mainData,NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> toAdd)	{
		for (Map.Entry<Integer,NavigableMap<Integer,List<SideDistribution>>> entry:toAdd.entrySet())	{
			int weight=entry.getKey();
			NavigableMap<Integer,List<SideDistribution>> toAddSubmap=entry.getValue();
			NavigableMap<Integer,List<SideDistribution>> mainDataSubmap=mainData.computeIfAbsent(weight,(Integer unused)->new TreeMap<>());
			addAll2(mainDataSubmap,toAddSubmap);
		}
	}
	
	private static void addAll2(NavigableMap<Integer,List<SideDistribution>> mainData,NavigableMap<Integer,List<SideDistribution>> toAdd)	{
		for (Map.Entry<Integer,List<SideDistribution>> entry:toAdd.entrySet())	{
			int size=entry.getKey();
			List<SideDistribution> toAddList=entry.getValue();
			List<SideDistribution> mainDataList=mainData.computeIfAbsent(size,(Integer unused)->new ArrayList<>());
			mainDataList.addAll(toAddList);
		}
	}
	
	/*
	 * Returns a map where the key is the WEIGHT, which must be the same for both sides, and the value is itself another map.
	 * This submap has the SIZE (i.e. number of blocks) as a key, and the values is a list with all the block distributions with the given weight
	 * and size.
	 */
	private static NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> getAllSideDistributions(int maxSize)	{
		NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> result=new TreeMap<>();
		for (int i=1;i<=maxSize;++i)	{
			int maxOtherSide=maxSize-i-1;
			int maxWeight=(maxOtherSide*(maxOtherSide+1))/2;
			NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> tmpResult=getAllDistributions(i,maxWeight);
			addAll(result,tmpResult);
		}
		return result;
	}
	
	private static class IntRange	{
		// Typical "[)" range: "end" does NOT belong. 
		public final int start;
		public final int end;
		private final static IntObjMap<IntObjMap<IntRange>> CACHE=HashIntObjMaps.newMutableMap();
		public IntRange(int start,int end)	{
			this.start=start;
			this.end=end;
		}
		public static IntRange of(int start,int end)	{
			return CACHE.computeIfAbsent(start,(int unused)->HashIntObjMaps.newMutableMap()).computeIfAbsent(end,(int unused)->new IntRange(start,end));
		}
		public static List<IntRange> separate(BitSet blocks)	{
			List<IntRange> result=new ArrayList<>();
			int position=-1;
			for (;;)	{
				int start=blocks.nextSetBit(1+position);
				if (start<0) return result;
				int end=1+start;
				while (blocks.get(end)) ++end;
				result.add(IntRange.of(start,end));
				position=end;
			}
		}
		public boolean contains(int value)	{
			return (start<=value)&&(value<end);
		}
		public boolean intersects(IntRange other)	{
			return (other.start<end)&&(start<other.end);
		}
		@Override
		public int hashCode()	{
			return SIZE*start+end;
		}
		@Override
		public boolean equals(Object other)	{
			if (other==null) return false;
			IntRange irOther=(IntRange)other;
			return (start==irOther.start)&&(end==irOther.end);
		}
		@Override
		public String toString()	{
			return "["+start+","+end+")";
		}
	}
	
	private static class Column	{
		public final BitSet blocks;
		public final int cardinality;
		public final int height;
		public final int id;
		private final List<IntRange> blockRanges;
		public Column(BitSet blocks)	{
			this.blocks=blocks;
			cardinality=blocks.cardinality();
			blockRanges=IntRange.separate(blocks);
			int h=-1;
			for (;;)	{
				int next=blocks.nextSetBit(1+h);
				if (next==-1) break;
				else h=next;
			}
			height=h;
			id=(blocks.cardinality()==0)?0:(int)(blocks.toLongArray()[0]);
		}
		@Override
		public int hashCode()	{
			return blocks.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			Column cOther=(Column)other;
			return blocks.equals(cOther.blocks);
		}
		@Override
		public String toString()	{
			return blocks.toString();
		}
		public boolean isValidAsCentre()	{
			return blocks.get(0);
		}
	}
	
	private static class JunctionSummary	{
		private final boolean[] junctions;
		public JunctionSummary(boolean[] junctions)	{
			this.junctions=junctions;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(junctions);
		}
		@Override
		public boolean equals(Object other)	{
			JunctionSummary jsOther=(JunctionSummary)other;
			return Arrays.equals(junctions,jsOther.junctions);
		}
		public static boolean formCompleteSculpture(JunctionSummary left,JunctionSummary right)	{
			for (int i=0;i<left.junctions.length;++i) if (!left.junctions[i]&&!right.junctions[i]) return false;
			return true;
		}
		public boolean isComplete()	{
			for (boolean b:junctions) if (!b) return false;
			return true;
		}
	}
	
	private static class ConnectionList	{
		private static class Array	{
			private final IntRange[] data;
			public Array(IntRange[] data)	{
				this.data=data;
			}
			@Override
			public int hashCode()	{
				return Arrays.hashCode(data);
			}
			@Override
			public boolean equals(Object other)	{
				Array aOther=(Array)other;
				return Arrays.equals(data,aOther.data);
			}
		}
		private final static Table<Array,Array,ConnectionList> CACHE=HashBasedTable.create();
		private final IntRange[] forwardConnections;
		private final IntRange[] backwardsConnections;
		private ConnectionList(IntRange[] forwardConnections,IntRange[] backwardsConnections)	{
			this.forwardConnections=forwardConnections;
			this.backwardsConnections=backwardsConnections;
		}
		public static ConnectionList of(IntRange[] forwardConnections,IntRange[] backwardsConnections)	{
			return CACHE.row(new Array(forwardConnections)).computeIfAbsent(new Array(backwardsConnections),(Array unused)->new ConnectionList(forwardConnections,backwardsConnections));
		}
		/*
		 * Returns null only if the connection is not possible.
		 */
		public static ConnectionList getFor(Column source,Column target)	{
			if (!source.blocks.intersects(target.blocks)) return null;
			IntRange[] forwardConnections=new IntRange[source.blockRanges.size()];
			for (int i=0;i<source.blockRanges.size();++i)	{
				IntRange sourceRange=source.blockRanges.get(i);
				for (int j=0;j<target.blockRanges.size();++j) if (sourceRange.intersects(target.blockRanges.get(j)))	{
					int start=j;
					int end=j;
					while ((end<target.blockRanges.size())&&sourceRange.intersects(target.blockRanges.get(end))) ++end;
					forwardConnections[i]=IntRange.of(start,end);
					break;
				}
			}
			IntRange[] backwardsConnections=new IntRange[target.blockRanges.size()];
			for (int i=0;i<target.blockRanges.size();++i) for (int j=0;j<forwardConnections.length;++j)	{
				IntRange direct=forwardConnections[j];
				if ((direct!=null)&&(direct.contains(i)))	{
					int start=j;
					int end=j;
					while (end<forwardConnections.length)	{
						direct=forwardConnections[end];
						if ((direct==null)||!direct.contains(i)) break;
						else ++end;
					}
					backwardsConnections[i]=IntRange.of(start,end);
					break;
				}
			}
			return ConnectionList.of(forwardConnections,backwardsConnections);
		}
	}
	
	private static class VisitedBlocks	{
		private final boolean[][] data;
		private VisitedBlocks(boolean[][] data)	{
			this.data=data;
		}
		public static VisitedBlocks init(List<Column> columns)	{
			boolean[][] data=new boolean[columns.size()][];
			for (int i=0;i<columns.size();++i) data[i]=new boolean[columns.get(i).blockRanges.size()];
			return new VisitedBlocks(data);
		}
		public static VisitedBlocks init(VisitedBlocks base)	{
			boolean[][] data=new boolean[base.data.length][];
			for (int i=0;i<data.length;++i) data[i]=new boolean[base.data[i].length];
			return new VisitedBlocks(data);
		}
		public boolean isVisited(int i,int j)	{
			return data[i][j];
		}
		public void markVisited(int i,int j)	{
			data[i][j]=true;
		}
		public static boolean checkFullAccess(List<VisitedBlocks> disjointSets)	{
			VisitedBlocks tmp=init(disjointSets.get(0));
			for (VisitedBlocks visited:disjointSets) for (int i=0;i<tmp.data.length;++i) for (int j=0;j<tmp.data[i].length;++j) tmp.data[i][j]|=visited.data[i][j];
			for (int i=0;i<tmp.data.length;++i) for (int j=0;j<tmp.data[i].length;++j) if (!tmp.data[i][j]) return false;
			return true;
		}
	}
	
	private static class ConnectionTable	{
		private final ConnectionList[][] data;
		public ConnectionTable(Column[] columns)	{
			int n=columns.length;
			int nextToShow=1000;
			int incr=1000;
			data=new ConnectionList[n][];
			for (int i=0;i<n;++i)	{
				if (i>=nextToShow)	{
					System.out.println(i+"...");
					nextToShow+=incr;
				}
				data[i]=new ConnectionList[i+1];
				for (int j=0;j<=i;++j) data[i][j]=ConnectionList.getFor(columns[i],columns[j]);
			}
		}
		public boolean areCompatible(Column a,Column b)	{
			int aId=a.id;
			int bId=b.id;
			return ((aId<=bId)?data[bId][aId]:data[aId][bId])!=null;
		}
		public IntRange[] getForwardConnections(Column a,Column b)	{
			int aId=a.id;
			int bId=b.id;
			if (aId<=bId) return data[bId][aId].backwardsConnections;
			else return data[aId][bId].forwardConnections;
		}
		public IntRange[] getBackwardsConnections(Column a,Column b)	{
			int aId=a.id;
			int bId=b.id;
			if (aId<=bId) return data[bId][aId].forwardConnections;
			else return data[aId][bId].backwardsConnections;
		}
	}
	
	private static class ColumnCache	{
		private final IntObjMap<List<Column>> columnsBySize;
		private final IntObjMap<List<Column>> validColumnsBySize;
		private final Map<Column,IntObjMap<List<Column>>> compatibleColumns;
		private final ConnectionTable connectionTable;
		public ColumnCache(int maxSize)	{
			columnsBySize=HashIntObjMaps.newMutableMap();
			validColumnsBySize=HashIntObjMaps.newMutableMap();
			long[] bitHolder=new long[1];
			long maxBit=(1l)<<maxSize;
			Column[] array=new Column[(int)maxBit];
			for (bitHolder[0]=0;bitHolder[0]<maxBit;++bitHolder[0])	{
				Column c=new Column(BitSet.valueOf(bitHolder));
				array[c.id]=c;
				List<Column> subList1=columnsBySize.computeIfAbsent(c.cardinality,(int unused)->new ArrayList<>());
				subList1.add(c);
				if (c.isValidAsCentre())	{
					List<Column> subList2=validColumnsBySize.computeIfAbsent(c.cardinality,(int unused)->new ArrayList<>());
					subList2.add(c);
				}
			}
			compatibleColumns=new HashMap<>();
			connectionTable=new ConnectionTable(array);
		}
		public List<Column> getCompatibleColumns(Column origin,int size)	{
			IntObjMap<List<Column>> subMap=compatibleColumns.computeIfAbsent(origin,(Column unused)->HashIntObjMaps.newMutableMap());
			return subMap.computeIfAbsent(size,(int s)->doGetCompatibleColumns(origin,columnsBySize.get(s)));
		}
		private List<Column> doGetCompatibleColumns(Column origin,List<Column> others)	{
			List<Column> result=new ArrayList<>();
			for (Column c:others) if (connectionTable.areCompatible(origin,c)) result.add(c);
			return result;
		}
		public JunctionSummary getJunctions(Column centralColumn,List<Column> sideColumns)	{
			boolean[] junctions=new boolean[centralColumn.blockRanges.size()-1];
			List<Column> allColumns=new ArrayList<>(1+sideColumns.size());
			allColumns.add(centralColumn);
			allColumns.addAll(sideColumns);
			List<VisitedBlocks> disjointSets=new ArrayList<>();
			int nextBlock=0;
			while (nextBlock<centralColumn.blockRanges.size())	{
				VisitedBlocks connectedSet=VisitedBlocks.init(allColumns);
				visitRecursively(connectedSet,0,nextBlock,allColumns);
				disjointSets.add(connectedSet);
				++nextBlock;
				while ((nextBlock<centralColumn.blockRanges.size())&&connectedSet.isVisited(0,nextBlock))	{
					junctions[nextBlock-1]=true;
					++nextBlock;
				}
			}
			if (!VisitedBlocks.checkFullAccess(disjointSets)) return null;
			return new JunctionSummary(junctions);
		}
		private void visitRecursively(VisitedBlocks connectedSet,int columnIdx,int blockIdx,List<Column> allColumns)	{
			if (connectedSet.isVisited(columnIdx,blockIdx)) return;
			connectedSet.markVisited(columnIdx,blockIdx);
			if (columnIdx>0)	{
				// Travel in reverse.
				IntRange canMoveTo=connectionTable.getBackwardsConnections(allColumns.get(columnIdx-1),allColumns.get(columnIdx))[blockIdx];
				if (canMoveTo!=null) for (int i=canMoveTo.start;i<canMoveTo.end;++i) visitRecursively(connectedSet,columnIdx-1,i,allColumns);
			}
			if (columnIdx<allColumns.size()-1)	{
				// Travel forward.
				IntRange canMoveTo=connectionTable.getForwardConnections(allColumns.get(columnIdx),allColumns.get(columnIdx+1))[blockIdx];
				if (canMoveTo!=null) for (int i=canMoveTo.start;i<canMoveTo.end;++i) visitRecursively(connectedSet,columnIdx+1,i,allColumns);
			}
		}
	}
	
	private static class JunctionSummaryCalculator	{
		private final ColumnCache columnsCache;
		private final Table<Column,SideDistribution,ObjIntMap<JunctionSummary>> data;
		public JunctionSummaryCalculator(ColumnCache columnsCache)	{
			this.columnsCache=columnsCache;
			data=HashBasedTable.create();
		}
		public ObjIntMap<JunctionSummary> getFor(Column mainColumn,SideDistribution distr,int maxHeight)	{
			return data.row(mainColumn).computeIfAbsent(distr,(SideDistribution d)->doCalculateFor(mainColumn,d,maxHeight));
		}
		private ObjIntMap<JunctionSummary> doCalculateFor(Column mainColumn,SideDistribution distr,int maxHeight)	{
			ObjIntMap<JunctionSummary> result=HashObjIntMaps.newMutableMap();
			List<Column> addedColumns=new ArrayList<>();
			calculateRecursively(mainColumn,distr,mainColumn,addedColumns,maxHeight,result);
			return result;
		}
		private void calculateRecursively(Column mainColumn,SideDistribution distr,Column lastColumn,List<Column> allAddedColumns,int maxHeight,ObjIntMap<JunctionSummary> result)	{
			int n=allAddedColumns.size();
			int currentSize=distr.blocks[n];
			allAddedColumns.add(null);
			List<Column> availableColumns=columnsCache.getCompatibleColumns(lastColumn,currentSize);
			for (Column c:availableColumns)	{
				if (c.height>maxHeight) continue;
				allAddedColumns.set(n,c);
				if (n==distr.blocks.length-1)	{
					JunctionSummary junctions=columnsCache.getJunctions(mainColumn,allAddedColumns);
					if (junctions!=null) result.addValue(junctions,1);
				}	else calculateRecursively(mainColumn,distr,c,allAddedColumns,maxHeight,result);
			}
			allAddedColumns.remove(n);
		}
	}
	
	private static int getMaxAddedHeight(SideDistribution s)	{
		int result=0;
		for (int h:s.blocks) result+=h-1;
		return result;
	}
	
	private static int count(int mainColumnSize,SideDistribution distr1,SideDistribution distr2,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache) {
		int maxAdded1=getMaxAddedHeight(distr1);
		int maxAdded2=getMaxAddedHeight(distr2);
		int maxHeight=mainColumnSize+maxAdded1+maxAdded2;
		List<Column> mainColumns=columnsCache.validColumnsBySize.get(mainColumnSize);
		int result=0;
		for (Column c:mainColumns) if (c.height<=maxHeight)	{
			ObjIntMap<JunctionSummary> junctions1=junctionsCache.getFor(c,distr1,maxHeight);
			ObjIntMap<JunctionSummary> junctions2=junctionsCache.getFor(c,distr2,maxHeight);
			for (ObjIntCursor<JunctionSummary> cursor1=junctions1.cursor();cursor1.moveNext();) for (ObjIntCursor<JunctionSummary> cursor2=junctions2.cursor();cursor2.moveNext();) if (JunctionSummary.formCompleteSculpture(cursor1.key(),cursor2.key())) result+=cursor1.value()*cursor2.value();
		}
		return result;
	}

	private static int count(int mainColumnSize,SideDistribution distr1,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache) {
		int maxAdded=getMaxAddedHeight(distr1);
		int maxHeight=mainColumnSize+2*maxAdded;
		List<Column> mainColumns=columnsCache.validColumnsBySize.get(mainColumnSize);
		int result=0;
		for (Column c:mainColumns) if (c.height<=maxHeight)	{
			Set<JunctionSummary> alreadyCounted=new HashSet<>();
			ObjIntMap<JunctionSummary> junctions1=junctionsCache.getFor(c,distr1,maxHeight);
			for (ObjIntCursor<JunctionSummary> cursor1=junctions1.cursor();cursor1.moveNext();)	{
				for (ObjIntCursor<JunctionSummary> cursor2=junctions1.cursor();cursor2.moveNext();)	{
					if (cursor1.key().equals(cursor2.key()))	{
						if (cursor1.key().isComplete())	{
							int value=cursor1.value();
							result+=((value+1)*value)/2;
						}
					}	else if (alreadyCounted.contains(cursor2.key())) continue;
					else if (JunctionSummary.formCompleteSculpture(cursor1.key(),cursor2.key())) result+=cursor1.value()*cursor2.value();
				}
				alreadyCounted.add(cursor1.key());
			}
		}
		return result;
	}

	private static int countAll(int mainColumnSize,List<SideDistribution> distrs,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache) {
		int result=0;
		for (int i=0;i<distrs.size();++i)	{
			SideDistribution oneDistr=distrs.get(i);
			result+=count(mainColumnSize,oneDistr,columnsCache,junctionsCache);
			for (int j=i+1;j<distrs.size();++j) result+=count(mainColumnSize,oneDistr,distrs.get(j),columnsCache,junctionsCache);
		}
		return result;
	}

	private static int countAll(int mainColumnSize,List<SideDistribution> distrs1,List<SideDistribution> distrs2,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache) {
		int result=0;
		for (int i=0;i<distrs1.size();++i) for (int j=0;j<distrs2.size();++j) result+=count(mainColumnSize,distrs1.get(i),distrs2.get(j),columnsCache,junctionsCache);
		return result;
	}

	/*
	 * Returns the correct solution for all the cases, but comes short for the real problem :(.
	 * I finally managed to concot something that finishes in "time" (~4200 seconds), but the answer is wrong (about 15026000. The real answer
	 * is about 15030000). 
	 * 
	 * So. I removed the "max height" limits and I got the same wrong results, after nearly 11 hours.
	 * At least, now I know that THAT logic was right. 
	 * 15026629
	 * Elapsed 39174.231310200004 seconds.
	 * JAJA SI.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> sideDistributions=getAllSideDistributions(SIZE);
		ColumnCache columnsCache=new ColumnCache(SIZE-2);
		int result=1;	// This is the completely vertical sculpture, weight=0 at both sides.
		for (Map.Entry<Integer,NavigableMap<Integer,List<SideDistribution>>> higherEntry:sideDistributions.entrySet())	{
			System.out.println(higherEntry.getKey()+"...");
			NavigableMap<Integer,List<SideDistribution>> subMap=higherEntry.getValue();
			JunctionSummaryCalculator junctionsCache=new JunctionSummaryCalculator(columnsCache);
			for (Map.Entry<Integer,List<SideDistribution>> entry:subMap.entrySet())	{
				int mySize=entry.getKey();
				int symRemaining=SIZE-2*mySize;
				if (symRemaining<=0) break;
				List<SideDistribution> myDistrs=entry.getValue();
				result+=countAll(symRemaining,myDistrs,columnsCache,junctionsCache);
				for (Map.Entry<Integer,List<SideDistribution>> entry2:subMap.tailMap(entry.getKey(),false).entrySet())	{
					int otherSize=entry2.getKey();
					int remaining=SIZE-mySize-otherSize;
					if (remaining<=0) break;
					result+=countAll(remaining,myDistrs,entry2.getValue(),columnsCache,junctionsCache);
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
