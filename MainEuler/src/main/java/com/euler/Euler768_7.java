package com.euler;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler768_7 {
	private static class PartitionPiece	{
		public final int key;
		public final int counter;
		public PartitionPiece(int key,int counter)	{
			this.key=key;
			this.counter=counter;
		}
		private final static Comparator<PartitionPiece> DEFAULT_COMPARATOR=Comparator.comparingInt((PartitionPiece p)->p.counter).thenComparingInt((PartitionPiece p)->-p.key);
		@Override
		public int hashCode()	{
			return key+counter;
		}
		@Override
		public boolean equals(Object other)	{
			PartitionPiece ppOther=(PartitionPiece)other;
			return (key==ppOther.key)&&(counter==ppOther.counter);
		}
		@Override
		public String toString()	{
			return key+"->"+counter;
		}
	}
	
	private static class SortedPartition	{
		public final List<PartitionPiece> pieces;
		public SortedPartition(List<PartitionPiece> pieces)	{
			this.pieces=pieces;
			pieces.sort(PartitionPiece.DEFAULT_COMPARATOR);
		}
		public static SortedPartition prepare(IntIntMap pieces)	{
			List<PartitionPiece> result=new ArrayList<>(pieces.size());
			for (IntIntCursor c=pieces.cursor();c.moveNext();) result.add(new PartitionPiece(c.key(),c.value()));
			return new SortedPartition(result);
		}
		@Override
		public int hashCode()	{
			return pieces.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			SortedPartition spOther=(SortedPartition)other;
			return pieces.equals(spOther.pieces);
		}
		@Override
		public String toString()	{
			return pieces.toString();
		}
	}
	
	private static int tryPartition(int sum,SortedPartition partition)	{
		BitSet contents=new BitSet(sum);
		return tryPartitionRecursive(contents,sum,partition.pieces,0,0,0);
	}
	
	private static int tryPartitionRecursive(BitSet contents,int sum,List<PartitionPiece> pieces,int currentListIndex,int currentPieceIndex,int currentPositionIndex)	{
		int result=0;
		PartitionPiece currentPiece=pieces.get(currentListIndex);
		int quotient=sum/currentPiece.key;
		for (int i=currentPositionIndex;i<quotient;++i)	{
			BitSet newContents=(BitSet)contents.clone();
			boolean isValid=true;
			for (int j=0;j<currentPiece.key;++j)	{
				int position=i+j*quotient;
				if (newContents.get(position))	{
					isValid=false;
					break;
				}	else newContents.set(position);
			}
			if (isValid)	{
				int nextListIndex,nextPieceIndex,nextPositionIndex;
				nextPieceIndex=1+currentPieceIndex;
				if (nextPieceIndex<currentPiece.counter)	{
					nextListIndex=currentListIndex;
					nextPositionIndex=i+1;
				}	else	{
					nextListIndex=1+currentListIndex;
					nextPieceIndex=0;
					nextPositionIndex=0;
				}
				if (nextListIndex>=pieces.size())	{
					if (newContents.cardinality()==sum) ++result;
					else throw new RuntimeException("JAJA SI.");
				}	else result+=tryPartitionRecursive(newContents,sum,pieces,nextListIndex,nextPieceIndex,nextPositionIndex);
			}
		}
		return result;
	}
	
	private static Set<SortedPartition> filter(int i,Set<IntIntMap> partitions) {
		return partitions.stream().filter((IntIntMap partition)->	{
			for (IntCursor cursor=partition.keySet().cursor();cursor.moveNext();) if ((i%cursor.elem())!=0) return false;
			return true;
		}).map(SortedPartition::prepare).collect(Collectors.toSet());
	}
	
	public static void main(String[] args)	{
		int sum=20;
		int total=360;
		IntStream.Builder builder=IntStream.builder();
		for (int i=2;i<=sum;++i) if ((total%i)==0) builder.accept(i);
		int[] validValues=builder.build().toArray();
		List<Set<IntIntMap>> partitions=new ArrayList<>();
		partitions.add(Set.of(HashIntIntMaps.newMutableMap()));
		partitions.add(Set.of());
		for (int i=2;i<=sum;++i)	{
			Set<IntIntMap> result=new HashSet<>();
			for (int j:validValues)	{
				if (j>i) break;
				Set<IntIntMap> oldResult=partitions.get(i-j);
				for (IntIntMap map:oldResult)	{
					IntIntMap newMap=HashIntIntMaps.newMutableMap(map);
					newMap.addValue(j,1);
					result.add(newMap);
				}
			}
			partitions.add(result);
		}
		List<Set<SortedPartition>> filteredPartitions=new ArrayList<>();
		for (int i=0;i<partitions.size();++i) filteredPartitions.add(filter(i,partitions.get(i)));
		System.out.println(filteredPartitions);
		System.out.println();
		IntObjMap<List<Pair<SortedPartition,Integer>>> summary=HashIntObjMaps.newMutableMap();
		for (int i=2;i<filteredPartitions.size();++i)	{
			List<Pair<SortedPartition,Integer>> list=new ArrayList<>();
			System.out.println(i+":");
			for (SortedPartition p:filteredPartitions.get(i))	{
				int cases=tryPartition(i,p);
				System.out.println("\t"+p+": "+cases+" cases.");
				if (cases!=0) list.add(new Pair<>(p,cases));
			}
			summary.put(i,list);
		}
		System.out.println(summary);
	}
}
