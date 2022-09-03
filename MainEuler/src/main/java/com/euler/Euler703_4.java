package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler703_4 {
	private final static int N=20;
	private final static long MOD=1_001_001_011l;
	
	private final static int ELEMENTS=1<<N;
	
	private static List<BitSet> getOpenCycles(int size)	{
		if (size==1)	{
			BitSet zero=new BitSet(1);
			BitSet one=new BitSet(1);
			one.set(0);
			return List.of(zero,one);
		}
		List<BitSet> result=new ArrayList<>();
		for (BitSet prev:getOpenCycles(size-1))	{
			result.add(prev);
			if (!prev.get(size-2))	{
				BitSet copy=new BitSet(size);
				copy.or(prev);
				copy.set(size-1);
				result.add(copy);
			}
		}
		return result;
	}
	
	private static List<BitSet> getClosedCycles(int size)	{
		return getOpenCycles(size).stream().filter((BitSet b)->!(b.get(0)&&b.get(size-1))).collect(Collectors.toUnmodifiableList());		
	}
	
	private static class Tree	{
		private long value0;
		private long value1;
		private final List<Tree> children;
		public Tree()	{
			value0=-1l;
			value1=-1l;
			children=new ArrayList<>();
		}
		public void addChild(Tree child)	{
			children.add(child);
		}
		public void evaluate()	{
			value0=1l;
			value1=1l;
			for (Tree child:children)	{
				child.evaluate();
				value0*=child.value0+child.value1;
				value0%=MOD;
				value1*=child.value0;
				value1%=MOD;
			}
		}
		public long getValue(boolean bitValue)	{
			return bitValue?value1:value0;
		}
	}
	
	private static class CycleData	{
		public final IntSet allElements;
		public final int[] sortedCycle;
		public final IntSet entryPoints;
		private final IntSet cycleSet;
		public CycleData(IntSet allElements,int[] sortedCycle)	{
			this.allElements=allElements;
			this.sortedCycle=sortedCycle;
			entryPoints=HashIntSets.newMutableSet();
			cycleSet=HashIntSets.newImmutableSet(sortedCycle);
		}
		public boolean isInCycle(int element)	{
			return cycleSet.contains(element);
		}
	}
	
	private static class CycleTrees	{
		private final List<Tree> entryPointList;
		private CycleTrees(List<Tree> entryPointList)	{
			this.entryPointList=entryPointList;
		}
		public static CycleTrees buildFromCycleData(CycleData data,Tree[] trees)	{
			List<Tree> entryPointList=new ArrayList<>(data.allElements.size());
			for (int i=0;i<data.sortedCycle.length;++i) entryPointList.add(trees[data.sortedCycle[i]]);
			for (IntCursor cursor=data.allElements.cursor();cursor.moveNext();)	{
				int x=cursor.elem();
				if (data.isInCycle(x)) continue;
				int y=nextValue(x);
				trees[y].addChild(trees[x]);
			}
			return new CycleTrees(entryPointList);
		}
		public void evaluate()	{
			entryPointList.forEach(Tree::evaluate);
		}
		public long getValue(BitSet pattern)	{
			long value=1l;
			for (int i=0;i<entryPointList.size();++i)	{
				Tree entryPoint=entryPointList.get(i);
				boolean bitValue=pattern.get(i);
				value*=entryPoint.getValue(bitValue);
				value%=MOD;
			}
			return value;
		}
		public long getValue(List<BitSet> patterns)	{
			long result=0l;
			for (BitSet pattern:patterns) result+=getValue(pattern);
			return result%MOD;
		}
	}
	
	private static int nextValue(int x)	{
		int b1=x&1;
		int b2=(x>>1)&1;
		int b3=(x>>2)&1;
		int y=x>>1;
		int lastBit=b1&(b2^b3);
		y+=(lastBit<<(N-1));
		return y;
	}
	
	private static List<CycleData> getCycles()	{
		CycleData[] assignedSets=new CycleData[ELEMENTS];
		List<CycleData> result=new ArrayList<>();
		for (int i=0;i<ELEMENTS;++i)	{
			if (assignedSets[i]!=null) continue;
			IntSet currentSet=HashIntSets.newMutableSet();
			int x=i;
			for (;;)	{
				currentSet.add(x);
				x=nextValue(x);
				if (currentSet.contains(x))	{
					// Since "x" is a cycle, we first build the actual cycle.
					IntStream.Builder builder=IntStream.builder();
					int y=x;
					do	{
						builder.add(y);
						y=nextValue(y);
					}	while (y!=x);
					int[] pureCycle=builder.build().toArray();
					CycleData data=new CycleData(currentSet,pureCycle);
					for (IntCursor cursor=currentSet.cursor();cursor.moveNext();)	{
						int elem=cursor.elem();
						if (!data.isInCycle(elem))	{
							y=nextValue(elem);
							if (data.isInCycle(y)) data.entryPoints.add(y);
						}
					}
					result.add(data);
					for (IntCursor cursor=currentSet.cursor();cursor.moveNext();) assignedSets[cursor.elem()]=data;
					break;
				}
				CycleData existingSet=assignedSets[x];
				if (existingSet!=null)	{
					for (IntCursor cursor=currentSet.cursor();cursor.moveNext();) assignedSets[cursor.elem()]=existingSet;
					currentSet.forEach((IntConsumer)existingSet.allElements::add);
					if (existingSet.isInCycle(x)) existingSet.entryPoints.add(x);
					break;
				}
			}
		}
		return result;
	}
	
	public static void main(String[] args)	{
		// Right at the first try, awesome.
		long tic=System.nanoTime();
		Tree[] trees=new Tree[ELEMENTS];
		for (int i=0;i<trees.length;++i) trees[i]=new Tree();
		long result=1l;
		IntObjMap<List<BitSet>> patternsBySize=HashIntObjMaps.newMutableMap();
		for (CycleData cycle:getCycles())	{
			CycleTrees forest=CycleTrees.buildFromCycleData(cycle,trees);
			forest.evaluate();
			List<BitSet> patternList=patternsBySize.computeIfAbsent(cycle.sortedCycle.length,Euler703_4::getClosedCycles);
			result*=forest.getValue(patternList);
			result%=MOD;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
