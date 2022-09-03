package com.euler;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler275_7 {
	private final static int SIZE=18;
	
	private final static boolean IS_EVEN=((SIZE%2)==0);
	
	private final static int ROW_SIZE=SIZE-2;
	private final static int CENTRAL_POSITION=IntMath.divide(ROW_SIZE,2,RoundingMode.DOWN);	// For 15 we want 7. For 18->16 we want 8.
	private final static int SPECIAL_CASES=IS_EVEN?3:1;
	
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
	
	/*
	 * Symmetry value: 0 if the block is symmetric. Otherwise, a symmetric element might exist (it won't if the leftmost bit=1).
	 * If the symmetric element exists and its id is smaller than the current one, this blocks's symmetry value is 1. Otherwise, it's -1.
	 */
	private static class Row	{
		public final BitSet blocks;
		public final int cardinality;
		public final int weight;
		public final int id;
		public final int centralBlock;	// Might be -1 if not covering the "0" position.
		public final int symmetryValue;
		public final int firstBlock;
		public final int lastBlock;
		private final List<IntRange> blockRanges;
		public Row(BitSet blocks)	{
			this.blocks=blocks;
			cardinality=blocks.cardinality();
			blockRanges=IntRange.separate(blocks);
			int cb=-1;
			for (int i=0;i<blockRanges.size();++i) if (blockRanges.get(i).contains(CENTRAL_POSITION))	{
				cb=i;
				break;
			}
			centralBlock=cb;
			int w=0;
			for (int i=0;i<ROW_SIZE;++i) if (blocks.get(i)) w+=i-CENTRAL_POSITION;
			weight=w;
			int sym=0;
			if (IS_EVEN&&blocks.get(0)) sym=-1;
			else for (int i=IS_EVEN?1:0;i<ROW_SIZE;++i)	{
				boolean cur=blocks.get(i);
				boolean oth=blocks.get(ROW_SIZE-i-(IS_EVEN?0:1));
				if (cur==oth) continue;
				sym=cur?-1:1;
				break;
			}
			symmetryValue=sym;
			if (blocks.cardinality()==0)	{
				id=0;
				firstBlock=-1;
				lastBlock=-1;
			}	else	{
				id=(int)(blocks.toLongArray()[0]);
				firstBlock=blocks.nextSetBit(0);
				int l=firstBlock;
				int n=blocks.nextSetBit(1+l);
				while (n>0)	{
					l=n;
					n=blocks.nextSetBit(1+n);
				}
				lastBlock=l;
			}
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
		public static ConnectionList getFor(Row source,Row target)	{
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
	
	private static class ConnectionTable	{
		private final ConnectionList[][] data;
		public ConnectionTable(Row[] columns)	{
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
		public boolean areCompatible(Row a,Row b)	{
			int aId=a.id;
			int bId=b.id;
			return ((aId<=bId)?data[bId][aId]:data[aId][bId])!=null;
		}
		public IntRange[] getForwardConnections(Row a,Row b)	{
			int aId=a.id;
			int bId=b.id;
			if (aId<=bId) return data[bId][aId].backwardsConnections;
			else return data[aId][bId].forwardConnections;
		}
		public IntRange[] getBackwardsConnections(Row a,Row b)	{
			int aId=a.id;
			int bId=b.id;
			if (aId<=bId) return data[bId][aId].forwardConnections;
			else return data[aId][bId].backwardsConnections;
		}
	}
	
	// Silly class used to facilitate arrays over IntObjMaps, which are O(1) and not O(log n). I hate Java sometimes.
	private static class MapWrapper	{
		public final IntObjMap<List<Row>> data;
		public MapWrapper()	{
			data=HashIntObjMaps.newMutableMap();
		}
	}
	
	private static class RowCache	{
		private final MapWrapper[] rowsBySizeAndWeight;
		private final List<Row> validInitialRows;
		private final ConnectionTable connectionTable;
		private final BoundsCache boundsCache;
		public RowCache()	{
			rowsBySizeAndWeight=new MapWrapper[1+SIZE];
			for (int i=0;i<=SIZE;++i) rowsBySizeAndWeight[i]=new MapWrapper();
			validInitialRows=new ArrayList<>();
			long[] bitHolder=new long[1];
			long maxBit=(1l)<<ROW_SIZE;
			Row[] array=new Row[(int)maxBit];
			for (bitHolder[0]=0;bitHolder[0]<maxBit;++bitHolder[0])	{
				Row r=new Row(BitSet.valueOf(bitHolder));
				array[r.id]=r;
				IntObjMap<List<Row>> mapByWeight=rowsBySizeAndWeight[r.cardinality].data;
				List<Row> subList=mapByWeight.computeIfAbsent(r.weight,(int unused)->new ArrayList<>());
				subList.add(r);
				if ((r.centralBlock>=0)&&(r.symmetryValue<=0)) validInitialRows.add(r);
			}
			connectionTable=new ConnectionTable(array);
			boundsCache=new BoundsCache();
		}
		public int countAll()	{
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern("HH:mm:ss");
			int result=SPECIAL_CASES;
			System.out.println("Total rows to start from: "+validInitialRows.size()+".");
			for (int i=0;i<validInitialRows.size();++i)	{
				System.out.println("Starting row "+i+" at "+formatter.format(LocalDateTime.now())+"...");
				Row r=validInitialRows.get(i);
				boolean[] fulfilledSets=new boolean[r.blockRanges.size()];
				fulfilledSets[r.centralBlock]=true;
				int remaining=SIZE-r.cardinality;
				int counterweight=-r.weight;
				boolean isSymmetric=(r.symmetryValue==0);
				result+=countRecursively(r,fulfilledSets,remaining,counterweight,isSymmetric);
			}
			return result;
		}
		private boolean updateInOneDirection(boolean[] source,boolean[] target,IntRange[] relations)	{
			boolean anyUpdate=false;
			for (int i=0;i<source.length;++i) if (source[i])	{
				IntRange r=relations[i];
				if (r==null) continue;
				for (int j=r.start;j<r.end;++j)	if (!target[j])	{
					anyUpdate=true;
					target[j]=true;
				}
			}
			return anyUpdate;
		}
		private boolean updateConnections(boolean[] fulfilledPre,boolean[] fulfilledPost,IntRange[] forwardRelations,IntRange[] backwardsRelations)	{
			updateInOneDirection(fulfilledPre,fulfilledPost,forwardRelations);
			for (;;)	{
				if (!updateInOneDirection(fulfilledPost,fulfilledPre,backwardsRelations)) break;
				if (!updateInOneDirection(fulfilledPre,fulfilledPost,forwardRelations)) break;
			}
			for (boolean b:fulfilledPre) if (!b) return false;
			return true;
		}
		private int countRecursively(Row currentRow,boolean[] fulfilledSets,int remaining,int counterweight,boolean isSymmetric)	{
			int result=0;
			for (int i=1;i<remaining;++i)	{
				int updatedRemaining=remaining-i;
				for (IntObjCursor<List<Row>> cursor=rowsBySizeAndWeight[i].data.cursor();cursor.moveNext();)	{
					int updatedCounterweight=counterweight-cursor.key();
					for (Row nextRow:cursor.value()) if ((!isSymmetric||(nextRow.symmetryValue<=0))&&connectionTable.areCompatible(currentRow,nextRow))	{
						 IntRange range=boundsCache.getValidWeights(nextRow,updatedRemaining);
						 if ((range==null)||(!range.contains(updatedCounterweight))) continue;
						 IntRange[] forwardRelations=connectionTable.getForwardConnections(currentRow,nextRow);
						 IntRange[] backwardsRelations=connectionTable.getBackwardsConnections(currentRow,nextRow);
						 boolean unfeasible=false;
						 for (int j=0;j<fulfilledSets.length;++j) if (!fulfilledSets[j]&&(forwardRelations[j]==null))	{
							 unfeasible=true;
							 break;
						 }
						 if (unfeasible) continue;
						 boolean[] newFulfilledSets=new boolean[nextRow.blockRanges.size()];
						 if (!updateConnections(Arrays.copyOf(fulfilledSets,fulfilledSets.length),newFulfilledSets,forwardRelations,backwardsRelations)) continue;
						 boolean newIsSymmetric=isSymmetric&&(nextRow.symmetryValue==0);
						 result+=countRecursively(nextRow,newFulfilledSets,updatedRemaining,updatedCounterweight,newIsSymmetric);
					}
				}
			}
			// Now, let's go directly to the desired size and weight.
			List<Row> directSolutions=rowsBySizeAndWeight[remaining].data.get(counterweight);
			if (directSolutions!=null) for (Row lastRow:directSolutions) if (connectionTable.areCompatible(currentRow,lastRow))	{
				 IntRange[] forwardRelations=connectionTable.getForwardConnections(currentRow,lastRow);
				 IntRange[] backwardsRelations=connectionTable.getBackwardsConnections(currentRow,lastRow);
				 boolean unfeasible=false;
				 for (int j=0;j<fulfilledSets.length;++j) if (!fulfilledSets[j]&&(forwardRelations[j]==null))	{
					 unfeasible=true;
					 break;
				 }
				 if (unfeasible) continue;
				 boolean[] newFulfilledSets=new boolean[lastRow.blockRanges.size()];
				 updateConnections(Arrays.copyOf(fulfilledSets,fulfilledSets.length),newFulfilledSets,forwardRelations,backwardsRelations);
				 for (boolean b:newFulfilledSets) if (!b)	{
					 unfeasible=true;
					 break;
				 }
				 if (!unfeasible) ++result;
			}
			return result;
		}
	}
	
	private static int getMinAddedWeight(int currentMinimum,int remaining)	{
		int position=currentMinimum;
		int result=0;
		for (int i=0;i<remaining;++i)	{
			result+=position-CENTRAL_POSITION;
			if (position>0) --position;
		}
		return result;
	}
	
	private static int getMaxAddedWeight(int currentMaximum,int remaining)	{
		int position=currentMaximum;
		int result=0;
		for (int i=0;i<remaining;++i)	{
			result+=position-CENTRAL_POSITION;
			if (position<ROW_SIZE-1) ++position;
		}
		return result;
	}
	
	private static class BoundsCache	{
		private final IntRange[][][] data;
		public BoundsCache()	{
			data=new IntRange[ROW_SIZE][ROW_SIZE][SIZE];
			for (int i=0;i<ROW_SIZE;++i) for (int j=0;j<ROW_SIZE;++j) if (i<=j) for (int k=0;k<SIZE;++k)	{
				int minimum=getMinAddedWeight(i,k);
				int maximum=getMaxAddedWeight(j,k);
				if (minimum<=maximum) data[i][j][k]=IntRange.of(minimum,maximum+1);
				else data[i][j][k]=IntRange.of(0,0);
			}	else for (int k=0;k<SIZE;++k) data[i][j][k]=IntRange.of(0,0);
		}
		public IntRange getValidWeights(Row r,int remainingBlocks)	{
			return data[r.firstBlock][r.lastBlock][remainingBlocks];
		}
	}
	
	// OK, it doesn't work, but most of the base ideas are already laid there. Tomorrow: debug! Also manage symmetry differently (lower/higher/symmetric).
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		RowCache rowsCache=new RowCache();
		int result=rowsCache.countAll();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
