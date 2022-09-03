package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

// IT'S TIME TO CONFRONT THIS.
public class Euler275_3 {
	private final static int SIZE=6;
	
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
		public IntRange(int start,int end)	{
			this.start=start;
			this.end=end;
		}
		public static List<IntRange> separate(BitSet blocks)	{
			List<IntRange> result=new ArrayList<>();
			int position=-1;
			for (;;)	{
				int start=blocks.nextSetBit(1+position);
				if (start<0) return result;
				int end=1+start;
				while (blocks.get(end)) ++end;
				result.add(new IntRange(start,end));
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
	}
	
	private static class ConnectionList	{
		private final IntRange[] forwardConnections;
		private final IntRange[] backwardsConnections;
		private ConnectionList(IntRange[] forwardConnections,IntRange[] backwardsConnections)	{
			this.forwardConnections=forwardConnections;
			this.backwardsConnections=backwardsConnections;
		}
		public ConnectionList reverse()	{
			return new ConnectionList(backwardsConnections,forwardConnections);
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
					forwardConnections[i]=new IntRange(start,end);
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
					backwardsConnections[i]=new IntRange(start,end);
				}
			}
			return new ConnectionList(forwardConnections,backwardsConnections);
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
	
	private static class ColumnCache	{
		private final IntObjMap<List<Column>> columnsBySize;
		private final IntObjMap<List<Column>> validColumnsBySize;
		private final Map<Column,IntObjMap<List<Column>>> compatibleColumns;
		private final Table<Column,Column,ConnectionList> connectionTable;
		public ColumnCache(int maxSize)	{
			columnsBySize=HashIntObjMaps.newMutableMap();
			validColumnsBySize=HashIntObjMaps.newMutableMap();
			long[] bitHolder=new long[1];
			long maxBit=(1l)<<maxSize;
			for (bitHolder[0]=0;bitHolder[0]<maxBit;++bitHolder[0])	{
				Column c=new Column(BitSet.valueOf(bitHolder));
				List<Column> subList1=columnsBySize.computeIfAbsent(c.cardinality,(int unused)->new ArrayList<>());
				subList1.add(c);
				if (c.isValidAsCentre())	{
					List<Column> subList2=validColumnsBySize.computeIfAbsent(c.cardinality,(int unused)->new ArrayList<>());
					subList2.add(c);
				}
			}
			compatibleColumns=new HashMap<>();
			connectionTable=HashBasedTable.create();
		}
		public List<Column> getCompatibleColumns(Column origin,int size)	{
			IntObjMap<List<Column>> subMap=compatibleColumns.computeIfAbsent(origin,(Column unused)->HashIntObjMaps.newMutableMap());
			return subMap.computeIfAbsent(size,(int s)->doGetCompatibleColumns(origin,columnsBySize.get(s)));
		}
		private List<Column> doGetCompatibleColumns(Column origin,List<Column> others)	{
			List<Column> result=new ArrayList<>();
			for (Column c:others)	{
				ConnectionList reverseConnections=connectionTable.get(c,origin);
				if (reverseConnections!=null)	{
					connectionTable.put(origin,c,reverseConnections.reverse());
					result.add(c);
					continue;
				}
				ConnectionList connections=ConnectionList.getFor(origin,c);
				if (connections!=null)	{
					connectionTable.put(origin,c,connections);
					result.add(c);
				}
			}
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
		private void visitRecursively(VisitedBlocks connectedSet,int columnId,int blockId,List<Column> allColumns)	{
			if (connectedSet.isVisited(columnId,blockId)) return;
			connectedSet.markVisited(columnId,blockId);
			if (columnId>0)	{
				// Travel in reverse.
				ConnectionList connections=connectionTable.get(allColumns.get(columnId-1),allColumns.get(columnId));
				IntRange canMoveTo=connections.backwardsConnections[blockId];
				if (canMoveTo!=null) for (int i=canMoveTo.start;i<canMoveTo.end;++i) visitRecursively(connectedSet,columnId-1,i,allColumns);
			}
			if (columnId<allColumns.size()-1)	{
				// Travel forward.
				ConnectionList connections=connectionTable.get(allColumns.get(columnId),allColumns.get(columnId+1));
				IntRange canMoveTo=connections.forwardConnections[blockId];
				if (canMoveTo!=null) for (int i=canMoveTo.start;i<canMoveTo.end;++i) visitRecursively(connectedSet,columnId+1,i,allColumns);
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
				}	else	{
					calculateRecursively(mainColumn,distr,c,allAddedColumns,maxHeight,result);
				}
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
		int maxHeight=mainColumnSize+Math.max(getMaxAddedHeight(distr1),getMaxAddedHeight(distr2));
		List<Column> mainColumns=columnsCache.validColumnsBySize.get(mainColumnSize);
		int result=0;
		for (Column c:mainColumns) if (c.height<=maxHeight)	{
			ObjIntMap<JunctionSummary> junctions1=junctionsCache.getFor(c,distr1,maxHeight);
			ObjIntMap<JunctionSummary> junctions2=junctionsCache.getFor(c,distr2,maxHeight);
			for (ObjIntCursor<JunctionSummary> cursor1=junctions1.cursor();cursor1.moveNext();) for (ObjIntCursor<JunctionSummary> cursor2=junctions2.cursor();cursor2.moveNext();) if (JunctionSummary.formCompleteSculpture(cursor1.key(),cursor2.key())) result+=cursor1.value()*cursor2.value();
		}
		return result;
	}

	private static int countAll(int mainColumnSize,List<SideDistribution> distrs,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache) {
		int result=0;
		for (int i=0;i<distrs.size();++i)	{
			SideDistribution oneDistr=distrs.get(i);
			result+=count(mainColumnSize,oneDistr,oneDistr,columnsCache,junctionsCache);
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
	 * At the very least, one mistake I'm committing is that I'm counting some pairings twice, but I believe that there could be more.
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
					result+=2*countAll(remaining,myDistrs,entry2.getValue(),columnsCache,junctionsCache);
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
