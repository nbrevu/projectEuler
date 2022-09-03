package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_5 {
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
		private final int maxX;
		private final int maxY;
		public Sculpture()	{
			blocks=Collections.singleton(Position.of(1,1));
			hashCode=blocks.hashCode();
			maxX=1;
			maxY=1;
		}
		private Sculpture(Sculpture base,Position newBlock)	{
			int incX=((newBlock.x==0)?1:0);
			int incY=((newBlock.y==0)?1:0);
			blocks=new HashSet<>();
			for (Position p:base.blocks) blocks.add(Position.of(p.x+incX,p.y+incY));
			blocks.add(Position.of(newBlock.x+incX,newBlock.y+incY));
			hashCode=blocks.hashCode();
			maxX=blocks.stream().mapToInt((Position p)->p.x).max().getAsInt();
			maxY=blocks.stream().mapToInt((Position p)->p.y).max().getAsInt();
		}
		private Sculpture(Set<Position> blocks)	{
			this.blocks=blocks;
			hashCode=blocks.hashCode();
			maxX=blocks.stream().mapToInt((Position p)->p.x).max().getAsInt();
			maxY=blocks.stream().mapToInt((Position p)->p.y).max().getAsInt();
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
		private Sculpture getMirrorImage()	{
			Set<Position> newBlocks=new HashSet<>();
			for (Position p:blocks) newBlocks.add(Position.of(maxX+1-p.x,p.y));
			return new Sculpture(newBlocks);
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
	
	private static class SideDistribution implements Comparable<SideDistribution>	{
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
		@Override
		public int compareTo(SideDistribution other)	{
			int diff=blocks.length-other.blocks.length;
			if (diff!=0) return diff;
			for (int i=0;i<blocks.length;++i)	{
				diff=blocks[i]-other.blocks[i];
				if (diff!=0) return diff;
			}
			return 0;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(blocks);
		}
		@Override
		public boolean equals(Object other)	{
			SideDistribution sdOther=(SideDistribution)other;
			return Arrays.equals(blocks,sdOther.blocks);
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
		public boolean isComplete()	{
			for (boolean b:junctions) if (!b) return false;
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
					break;
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
		private final Table<Column,SideDistribution,Map<JunctionSummary,List<List<Column>>>> data;
		public JunctionSummaryCalculator(ColumnCache columnsCache)	{
			this.columnsCache=columnsCache;
			data=HashBasedTable.create();
		}
		public Map<JunctionSummary,List<List<Column>>> getFor(Column mainColumn,SideDistribution distr,int maxHeight)	{
			return data.row(mainColumn).computeIfAbsent(distr,(SideDistribution d)->doCalculateFor(mainColumn,d,maxHeight));
		}
		private Map<JunctionSummary,List<List<Column>>> doCalculateFor(Column mainColumn,SideDistribution distr,int maxHeight)	{
			Map<JunctionSummary,List<List<Column>>> result=new HashMap<>();
			List<Column> addedColumns=new ArrayList<>();
			calculateRecursively(mainColumn,distr,mainColumn,addedColumns,maxHeight,result);
			return result;
		}
		private void calculateRecursively(Column mainColumn,SideDistribution distr,Column lastColumn,List<Column> allAddedColumns,int maxHeight,Map<JunctionSummary,List<List<Column>>> result)	{
			int n=allAddedColumns.size();
			int currentSize=distr.blocks[n];
			allAddedColumns.add(null);
			List<Column> availableColumns=columnsCache.getCompatibleColumns(lastColumn,currentSize);
			for (Column c:availableColumns)	{
				if (c.height>maxHeight) continue;
				allAddedColumns.set(n,c);
				if (n==distr.blocks.length-1)	{
					JunctionSummary junctions=columnsCache.getJunctions(mainColumn,allAddedColumns);
					if (junctions!=null)	{
						List<List<Column>> subList=result.computeIfAbsent(junctions,(JunctionSummary unused)->new ArrayList<>());
						subList.add(new ArrayList<>(allAddedColumns));
					}
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
	
	private static void count(int mainColumnSize,SideDistribution distr1,SideDistribution distr2,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache,FullSculptureSet result) {
		int maxHeight=mainColumnSize+getMaxAddedHeight(distr1)+getMaxAddedHeight(distr2);
		List<Column> mainColumns=columnsCache.validColumnsBySize.get(mainColumnSize);
		for (Column c:mainColumns) if (c.height<=maxHeight)	{
			Map<JunctionSummary,List<List<Column>>> junctionMap1=junctionsCache.getFor(c,distr1,maxHeight);
			Map<JunctionSummary,List<List<Column>>> junctionMap2=junctionsCache.getFor(c,distr2,maxHeight);
			for (Map.Entry<JunctionSummary,List<List<Column>>> entry1:junctionMap1.entrySet()) for (Map.Entry<JunctionSummary,List<List<Column>>> entry2:junctionMap2.entrySet())	{
				JunctionSummary junctions1=entry1.getKey();
				JunctionSummary junctions2=entry2.getKey();
				if (!JunctionSummary.formCompleteSculpture(junctions1,junctions2)) continue;
				for (List<Column> list1:entry1.getValue()) for (List<Column> list2:entry2.getValue()) result.addData(c,list1,list2);
			}
		}
	}

	private static void count(int mainColumnSize,SideDistribution distr1,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache,FullSculptureSet result) {
		int maxHeight=mainColumnSize+2*getMaxAddedHeight(distr1);
		List<Column> mainColumns=columnsCache.validColumnsBySize.get(mainColumnSize);
		for (Column c:mainColumns) if (c.height<=maxHeight)	{
			Set<JunctionSummary> alreadyCounted=new HashSet<>();
			Map<JunctionSummary,List<List<Column>>> junctionMap=junctionsCache.getFor(c,distr1,maxHeight);
			for (Map.Entry<JunctionSummary,List<List<Column>>> entry1:junctionMap.entrySet())	{
				for (Map.Entry<JunctionSummary,List<List<Column>>> entry2:junctionMap.entrySet()) if (entry1.getKey().equals(entry2.getKey()))	{
					if (entry1.getKey().isComplete())	{
						List<List<Column>> list=entry1.getValue();
						for (int i=0;i<list.size();++i) for (int j=i;j<list.size();++j) result.addData(c,list.get(i),list.get(j));
					}
				}	else if (alreadyCounted.contains(entry2.getKey())) continue;
				else if (JunctionSummary.formCompleteSculpture(entry1.getKey(),entry2.getKey())) for (List<Column> list1:entry1.getValue()) for (List<Column> list2:entry2.getValue()) result.addData(c,list1,list2);
				alreadyCounted.add(entry1.getKey());
			}
		}
	}

	private static void countAll(int mainColumnSize,List<SideDistribution> distrs,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache,FullSculptureSet result) {
		for (int i=0;i<distrs.size();++i)	{
			SideDistribution oneDistr=distrs.get(i);
			count(mainColumnSize,oneDistr,columnsCache,junctionsCache,result);
			for (int j=i+1;j<distrs.size();++j) count(mainColumnSize,oneDistr,distrs.get(j),columnsCache,junctionsCache,result);
		}
	}

	private static void countAll(int mainColumnSize,List<SideDistribution> distrs1,List<SideDistribution> distrs2,ColumnCache columnsCache,JunctionSummaryCalculator junctionsCache,FullSculptureSet result) {
		for (int i=0;i<distrs1.size();++i) for (int j=0;j<distrs2.size();++j) count(mainColumnSize,distrs1.get(i),distrs2.get(j),columnsCache,junctionsCache,result);
	}
	
	private static class SideDistributionPair	{
		public final SideDistribution left;
		public final SideDistribution right;
		public SideDistributionPair(SideDistribution a,SideDistribution b)	{
			if (a.compareTo(b)<=0)	{
				left=a;
				right=b;
			}	else	{
				left=b;
				right=a;
			}
			if (left.weight!=right.weight) throw new RuntimeException("Was passiert?????");
		}
		@Override
		public int hashCode()	{
			return left.hashCode()+right.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			SideDistributionPair sdpOther=(SideDistributionPair)other;
			return left.equals(sdpOther.left)&&right.equals(sdpOther.right);
		}
		@Override
		public String toString()	{
			return "Distrs<"+left+","+right+">";
		}
		public int weights()	{
			return left.weight;
		}
	}
	
	private static class ColumnSet	{
		private final List<Column> columns;
		public ColumnSet(List<Column> columns)	{
			this.columns=columns;
		}
		public ColumnSet reverse()	{
			return new ColumnSet(new ArrayList<>(Lists.reverse(columns)));
		}
		public static ColumnSet getFrom(Sculpture s)	{
			List<BitSet> result=new ArrayList<>(s.maxX);
			for (int i=0;i<s.maxX;++i) result.add(new BitSet(s.maxY));
			for (Position p:s.blocks) result.get(p.x-1).set(p.y-1);
			return new ColumnSet(result.stream().map(Column::new).collect(Collectors.toList()));
		}
		public SideDistributionPair getDistributions()	{
			int weight=0;
			int size=0;
			for (int i=0;i<columns.size();++i)	{
				int card=columns.get(i).cardinality;
				weight+=i*card;
				size+=card;
			}
			int centre=IntMath.divide(weight,size,RoundingMode.UNNECESSARY);
			int[] distr1=new int[centre];
			for (int i=0;i<distr1.length;++i) distr1[i]=columns.get(centre-1-i).cardinality;
			int[] distr2=new int[columns.size()-centre-1];
			for (int i=0;i<distr2.length;++i) distr2[i]=columns.get(i+centre+1).cardinality;
			return new SideDistributionPair(new SideDistribution(distr1),new SideDistribution(distr2));
		}
		@Override
		public int hashCode()	{
			return columns.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			ColumnSet csOther=(ColumnSet)other;
			return columns.equals(csOther.columns);
		}
		@Override
		public String toString()	{
			return "Columns"+columns;
		}
	}
	
	private static class FullSculptureSet	{
		/*
		 * First index: lateral weights.
		 * Second index: pair of distributions.
		 */
		private SortedMap<Integer,Map<SideDistributionPair,List<ColumnSet>>> data;
		public FullSculptureSet()	{
			data=new TreeMap<>();
		}
		public void addData(Sculpture s)	{
			ColumnSet columns=ColumnSet.getFrom(s);
			addData(columns);
		}
		public void addData(Column mainColumn,List<Column> distr1,List<Column> distr2)	{
			List<Column> sortedList=new ArrayList<>(distr1);
			Collections.reverse(sortedList);
			sortedList.add(mainColumn);
			sortedList.addAll(distr2);
			ColumnSet columns=new ColumnSet(sortedList);
			addData(columns);
		}
		private void addData(ColumnSet columns)	{
			SideDistributionPair distrs=columns.getDistributions();
			int weight=distrs.weights();
			Map<SideDistributionPair,List<ColumnSet>> subMap=data.computeIfAbsent(weight,(Integer unused)->new HashMap<>());
			List<ColumnSet> subList=subMap.computeIfAbsent(distrs,(SideDistributionPair unused)->new ArrayList<>());
			subList.add(columns);
		}
		public int size()	{
			int result=0;
			for (Map<SideDistributionPair,List<ColumnSet>> map:data.values()) for (List<ColumnSet> list:map.values()) result+=list.size();
			return result;
		}
	}
	
	private static FullSculptureSet generateUsingBruteForce()	{
		Collection<Sculpture> prevGen=Collections.singleton(new Sculpture());
		for (int i=2;i<=SIZE;++i)	{
			Set<Sculpture> newGen=getNextGeneration(prevGen);
			prevGen=newGen;
		}
		Set<Sculpture> newSet=new HashSet<>();
		for (Sculpture s:prevGen) if (s.isAcceptable()&&!newSet.contains(s.getMirrorImage())) newSet.add(s);
		FullSculptureSet result=new FullSculptureSet();
		newSet.forEach(result::addData);
		return result;
	}
	
	private static FullSculptureSet generateColumnwise()	{
		NavigableMap<Integer,NavigableMap<Integer,List<SideDistribution>>> sideDistributions=getAllSideDistributions(SIZE);
		ColumnCache columnsCache=new ColumnCache(SIZE-2);
		FullSculptureSet result=new FullSculptureSet();
		{
			// "Boring" sculpture consisting of a single column!
			BitSet column=new BitSet(SIZE);
			for (int i=0;i<SIZE;++i) column.set(i);
			result.addData(new Column(column),Collections.emptyList(),Collections.emptyList());
		}
		for (Map.Entry<Integer,NavigableMap<Integer,List<SideDistribution>>> higherEntry:sideDistributions.entrySet())	{
			NavigableMap<Integer,List<SideDistribution>> subMap=higherEntry.getValue();
			JunctionSummaryCalculator junctionsCache=new JunctionSummaryCalculator(columnsCache);
			for (Map.Entry<Integer,List<SideDistribution>> entry:subMap.entrySet())	{
				int mySize=entry.getKey();
				int symRemaining=SIZE-2*mySize;
				if (symRemaining<=0) break;
				List<SideDistribution> myDistrs=entry.getValue();
				countAll(symRemaining,myDistrs,columnsCache,junctionsCache,result);
				for (Map.Entry<Integer,List<SideDistribution>> entry2:subMap.tailMap(entry.getKey(),false).entrySet())	{
					int otherSize=entry2.getKey();
					int remaining=SIZE-mySize-otherSize;
					if (remaining<=0) break;
					countAll(remaining,myDistrs,entry2.getValue(),columnsCache,junctionsCache,result);
				}
			}
		}
		return result;
	}
	
	private static class SculptureDifferenceSet	{
		/*
		 * First index: lateral weights.
		 * Second index: pair of distributions.
		 */
		private SortedMap<Integer,Map<SideDistributionPair,List<ColumnSet>>> uncaught;
		private SortedMap<Integer,Map<SideDistributionPair,List<ColumnSet>>> invalid;
		public SculptureDifferenceSet(FullSculptureSet bruteForce,FullSculptureSet columnwise)	{
			uncaught=new TreeMap<>();
			invalid=new TreeMap<>();
			checkDifferences(bruteForce.data,columnwise.data);
		}
		private void checkDifferences(SortedMap<Integer,Map<SideDistributionPair,List<ColumnSet>>> bruteForce,SortedMap<Integer,Map<SideDistributionPair,List<ColumnSet>>> columnwise)	{
			for (Map.Entry<Integer,Map<SideDistributionPair,List<ColumnSet>>> entry:bruteForce.entrySet())	{
				Integer weight=entry.getKey();
				Map<SideDistributionPair,List<ColumnSet>> subBruteForce=entry.getValue();
				Map<SideDistributionPair,List<ColumnSet>> subColumnwise=columnwise.get(weight);
				if (subColumnwise==null) uncaught.put(weight,subBruteForce);
				else checkDifferences(weight,subBruteForce,subColumnwise);
			}
			for (Map.Entry<Integer,Map<SideDistributionPair,List<ColumnSet>>> entry:columnwise.entrySet()) if (!bruteForce.containsKey(entry.getKey())) invalid.put(entry.getKey(),entry.getValue());
		}
		private void checkDifferences(Integer weight,Map<SideDistributionPair,List<ColumnSet>> bruteForce,Map<SideDistributionPair,List<ColumnSet>> columnwise)	{
			for (Map.Entry<SideDistributionPair,List<ColumnSet>> entry:bruteForce.entrySet())	{
				SideDistributionPair distrs=entry.getKey();
				List<ColumnSet> subBruteForce=entry.getValue();
				List<ColumnSet> subColumnwise=columnwise.get(distrs);
				if (subColumnwise==null)	{
					Map<SideDistributionPair,List<ColumnSet>> toAdd=uncaught.computeIfAbsent(weight,(Integer unused)->new HashMap<>());
					toAdd.put(distrs,subBruteForce);
				}	else checkDifferences(weight,distrs,subBruteForce,subColumnwise);
			}
			for (Map.Entry<SideDistributionPair,List<ColumnSet>> entry:columnwise.entrySet()) if (!bruteForce.containsKey(entry.getKey()))	{
				Map<SideDistributionPair,List<ColumnSet>> toAdd=invalid.computeIfAbsent(weight,(Integer unused)->new HashMap<>());
				toAdd.put(entry.getKey(),entry.getValue());
			}
		}
		private void checkDifferences(Integer weight,SideDistributionPair distrs,List<ColumnSet> bruteForce,List<ColumnSet> columnwise)	{
			for (ColumnSet data:bruteForce)	{
				ColumnSet reverse=data.reverse();
				if (!columnwise.contains(data)&&!columnwise.contains(reverse))	{
					Map<SideDistributionPair,List<ColumnSet>> subMap=uncaught.computeIfAbsent(weight,(Integer unused)->new HashMap<>());
					List<ColumnSet> toAdd=subMap.computeIfAbsent(distrs,(SideDistributionPair unused)->new ArrayList<>());
					toAdd.add(data);
				}
			}
			for (ColumnSet data:columnwise)	{
				ColumnSet reverse=data.reverse();
				if (!bruteForce.contains(data)&&!bruteForce.contains(reverse))	{
					Map<SideDistributionPair,List<ColumnSet>> subMap=invalid.computeIfAbsent(weight,(Integer unused)->new HashMap<>());
					List<ColumnSet> toAdd=subMap.computeIfAbsent(distrs,(SideDistributionPair unused)->new ArrayList<>());
					toAdd.add(data);
				}
			}
		}
	}

	public static void main(String[] args)	{
		long tic=System.nanoTime();
		FullSculptureSet bruteForce=generateUsingBruteForce();
		FullSculptureSet columnwise=generateColumnwise();
		int result=bruteForce.size();
		int result2=columnwise.size();
		SculptureDifferenceSet diffs=new SculptureDifferenceSet(bruteForce,columnwise);
		System.out.println("I've misses these ones: "+diffs.uncaught+".");
		System.out.println("These ones probably shouldn't be there: "+diffs.invalid+".");
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println(result2);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
