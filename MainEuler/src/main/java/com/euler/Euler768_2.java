package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler768_2 {
	private final static int CANDLES=20;
	private final static int HOLES=360;
	
	private static class Partition	{
		private final IntIntMap counters;
		private Partition(IntIntMap counters)	{
			this.counters=counters;
		}
		public static Partition initialPartition()	{
			return new Partition(HashIntIntMaps.newMutableMap());
		}
		public Partition addValue(int x)	{
			IntIntMap newCounters=HashIntIntMaps.newMutableMap(counters);
			newCounters.addValue(x,1);
			return new Partition(newCounters);
		}
		public IntIntMap getCounters()	{
			return counters;
		}
		@Override
		public int hashCode()	{
			return counters.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			Partition pOther=(Partition)other;
			return counters.equals(pOther.counters);
		}
		@Override
		public String toString()	{
			return counters.toString();
		}
	}
	
	private static class PartitionPiece	{
		public final int key;
		public final int counter;
		public PartitionPiece(int key,int counter)	{
			this.key=key;
			this.counter=counter;
		}
		private final static Comparator<PartitionPiece> DEFAULT_COMPARATOR=Comparator.comparingInt((PartitionPiece p)->p.counter).thenComparingInt((PartitionPiece p)->-p.key);
		public static List<PartitionPiece> sortPartitions(Partition p)	{
			IntIntMap pieces=p.getCounters();
			List<PartitionPiece> result=new ArrayList<>(pieces.size());
			for (IntIntCursor c=pieces.cursor();c.moveNext();) result.add(new PartitionPiece(c.key(),c.value()));
			result.sort(DEFAULT_COMPARATOR);
			return result;
		}
		@Override
		public String toString()	{
			return key+"->"+counter;
		}
	}
	
	private static class RandomAccessCombinatorialNumberCache	{
		private static BigInteger calculate(long n,long k)	{
			BigInteger num=BigInteger.ONE;
			BigInteger den=BigInteger.ONE;
			while (k>=1)	{
				num=num.multiply(BigInteger.valueOf(n));
				den=den.multiply(BigInteger.valueOf(k));
				--n;
				--k;
			}
			return num.divide(den);
		}
		private final IntObjMap<IntObjMap<BigInteger>> cache;
		public RandomAccessCombinatorialNumberCache()	{
			cache=HashIntObjMaps.newMutableMap();
		}
		public BigInteger nchoosek(int n,int k)	{
			IntObjMap<BigInteger> subMap=cache.computeIfAbsent(n,(int unused)->HashIntObjMaps.newMutableMap());
			return subMap.computeIfAbsent(k,(int unused)->calculate(n,k));
		}
	}
	
	private static class CompatibilitySummary	{
		private static class SinglePairCompatibilityList	{
			private final BitSet[] compatibleValues;
			private final BitSet[] incompatibleValues;
			public SinglePairCompatibilityList(BitSet[] compatibleValues,BitSet[] incompatibleValues)	{
				this.compatibleValues=compatibleValues;
				this.incompatibleValues=incompatibleValues;
			}
		}
		private static class SingleValueCompatibilityList	{
			private final IntObjMap<SinglePairCompatibilityList> compatibilities;
			public SingleValueCompatibilityList()	{
				compatibilities=HashIntObjMaps.newMutableMap();
			}
			public void addInformation(int value,SinglePairCompatibilityList info)	{
				compatibilities.put(value,info);
			}
		}
		// Needed because arrays of generic classes gehen nicht. This is the kind of shit that makes me hate Java sometimes.
		private static class PartitionSet	{
			public final Set<Partition> partitions;
			public PartitionSet(Set<Partition> partitions)	{
				this.partitions=partitions;
			}
		}
		private final int totalSize;
		private final IntObjMap<SingleValueCompatibilityList> compatibility;
		private final Set<Partition> validPartitions;
		private final int[] möbius;
		private final RandomAccessCombinatorialNumberCache combinatorialCache;
		public CompatibilitySummary(int totalSize,int elements)	{
			this.totalSize=totalSize;
			möbius=EulerUtils.möbiusSieve(elements);
			IntStream.Builder validElementsBuilder=IntStream.builder();
			for (int i=2;i<=elements;++i) if ((möbius[i]!=0)&&((totalSize%i)==0)) validElementsBuilder.accept(i);
			int[] validElements=validElementsBuilder.build().toArray();
			IntSet validElementsSet=HashIntSets.newImmutableSet(validElements);
			PartitionSet[] partitions=new PartitionSet[1+totalSize];
			partitions[0]=new PartitionSet(Set.of(Partition.initialPartition()));
			partitions[1]=new PartitionSet(Set.of());
			for (int i=2;i<=elements;++i)	{
				Set<Partition> currentSet=new HashSet<>();
				for (int j=2;j<=i;++j) if (validElementsSet.contains(j)) for (Partition p:partitions[i-j].partitions) currentSet.add(p.addValue(j));
				partitions[i]=new PartitionSet(currentSet);
			}
			validPartitions=partitions[elements].partitions;
			compatibility=HashIntObjMaps.newMutableMap();
			for (int i=0;i<validElements.length;++i) compatibility.put(validElements[i],new SingleValueCompatibilityList());
			for (int i=0;i<validElements.length;++i) for (int j=1+i;j<validElements.length;++j)	{
				int elI=validElements[i];
				int elJ=validElements[j];
				Pair<SinglePairCompatibilityList,SinglePairCompatibilityList> currentCompatibility=calculateCompatibility(totalSize,elI,elJ);
				compatibility.get(elI).addInformation(elJ,currentCompatibility.first);
				compatibility.get(elJ).addInformation(elI,currentCompatibility.second);
			}
			combinatorialCache=new RandomAccessCombinatorialNumberCache();
		}
		private static Pair<SinglePairCompatibilityList,SinglePairCompatibilityList> calculateCompatibility(int totalSize,int sizeA,int sizeB)	{
			int entriesA=totalSize/sizeA;
			int entriesB=totalSize/sizeB;
			BitSet[] compatibleValuesAToB=new BitSet[entriesA];
			BitSet[] incompatibleValuesAToB=new BitSet[entriesA];
			for (int i=0;i<entriesA;++i)	{
				compatibleValuesAToB[i]=new BitSet(entriesB);
				incompatibleValuesAToB[i]=new BitSet(entriesB);
			}
			BitSet[] compatibleValuesBToA=new BitSet[entriesB];
			BitSet[] incompatibleValuesBToA=new BitSet[entriesB];
			for (int i=0;i<entriesB;++i)	{
				compatibleValuesBToA[i]=new BitSet(entriesA);
				incompatibleValuesBToA[i]=new BitSet(entriesA);
			}
			BitSet[] pointsA=new BitSet[entriesA];
			for (int i=0;i<entriesA;++i)	{
				BitSet points=new BitSet(totalSize);
				for (int j=i;j<totalSize;j+=entriesA) points.set(j);
				pointsA[i]=points;
			}
			BitSet[] pointsB=new BitSet[entriesB];
			for (int i=0;i<entriesB;++i)	{
				BitSet points=new BitSet(totalSize);
				for (int j=i;j<totalSize;j+=entriesB) points.set(j);
				pointsB[i]=points;
			}
			for (int i=0;i<entriesA;++i) for (int j=0;j<entriesB;++j)	{
				boolean areIncompatible=pointsA[i].intersects(pointsB[j]);
				compatibleValuesAToB[i].set(j,!areIncompatible);
				incompatibleValuesAToB[i].set(j,areIncompatible);
				compatibleValuesBToA[j].set(i,!areIncompatible);
				incompatibleValuesBToA[j].set(i,areIncompatible);
			}
			SinglePairCompatibilityList resultA=new SinglePairCompatibilityList(compatibleValuesAToB,incompatibleValuesAToB);
			SinglePairCompatibilityList resultB=new SinglePairCompatibilityList(compatibleValuesBToA,incompatibleValuesBToA);
			return new Pair<>(resultA,resultB);
		}
		public Set<Partition> getAllPartitions()	{
			return validPartitions;
		}
		public boolean isNegative(Partition p)	{
			boolean result=false;
			for (IntIntCursor cursor=p.getCounters().cursor();cursor.moveNext();) if ((möbius[cursor.key()]==1)&&((cursor.value()%2)==1)) result=!result;
			return result;
		}
		public BigInteger getCombinations(Partition p)	{
			List<PartitionPiece> partitions=PartitionPiece.sortPartitions(p);
			/*-
			long casesToExplore=1;
			for (int i=0;i<partitions.size()-1;++i)	{
				long num=1l;
				long den=1l;
				long base=totalSize/partitions.get(i).key;
				for (int j=0;j<partitions.get(i).counter;++j)	{
					num*=base-j;
					den*=j+1;
				}
				casesToExplore*=num/den;
			}
			System.out.println(partitions+": "+casesToExplore+" cases.");
			*/
			if (partitions.size()==1)	{
				PartitionPiece piece=partitions.get(0);
				return combinatorialCache.nchoosek(totalSize/piece.key,piece.counter);
			}
			PartitionPiece initialPiece=partitions.get(0);
			IntObjMap<BitSet> unavailability=HashIntObjMaps.newMutableMap();
			for (PartitionPiece piece:partitions) unavailability.put(piece.key,new BitSet(totalSize/piece.key));
			return getCombinationsRecursive(initialPiece.key,initialPiece.counter,0,partitions.subList(1,partitions.size()),unavailability);
		}
		private BigInteger getCombinationsRecursive(int currentKey,int currentCounter,int currentIndex,List<PartitionPiece> remainders,IntObjMap<BitSet> unavailableValues)	{
			int lastIndex=(totalSize/currentKey)+1-currentCounter;
			BigInteger result=BigInteger.ZERO;
			BitSet currentlyUnavailable=unavailableValues.get(currentKey);
			for (int i=currentIndex;i<lastIndex;++i)	{
				if (currentlyUnavailable.get(i)) continue;
				IntObjMap<BitSet> newAvailableValues=HashIntObjMaps.newMutableMap();
				newAvailableValues.put(currentKey,currentlyUnavailable);
				for (PartitionPiece p:remainders)	{
					int otherKey=p.key;
					BitSet oldSet=unavailableValues.get(otherKey);
					BitSet newRestrictions=compatibility.get(currentKey).compatibilities.get(otherKey).incompatibleValues[i];
					newAvailableValues.put(otherKey,or(oldSet,newRestrictions));
				}
				if (currentCounter>1) result=result.add(getCombinationsRecursive(currentKey,currentCounter-1,i+1,remainders,newAvailableValues));
				else if (remainders.size()>1)	{
					PartitionPiece nextPiece=remainders.get(0);
					result=result.add(getCombinationsRecursive(nextPiece.key,nextPiece.counter,0,remainders.subList(1,remainders.size()),newAvailableValues));
				}	else	{
					PartitionPiece lastPiece=remainders.get(0);
					int lastKey=lastPiece.key;
					int lastCounter=lastPiece.counter;
					int lastSize=totalSize/lastKey;
					int available=lastSize-newAvailableValues.get(lastKey).cardinality();
					result=result.add(combinatorialCache.nchoosek(available,lastCounter));
				}
			}
			return result;
		}
		private static BitSet or(BitSet a,BitSet b)	{
			BitSet result=(BitSet)a.clone();
			result.or(b);
			return result;
		}
	}
	
	/*
	 * Ooooh, doesn't work :(. Is there a mistake, or my logic is wrong?
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		CompatibilitySummary problemSummary=new CompatibilitySummary(HOLES,CANDLES);
		BigInteger result=BigInteger.ZERO;
		for (Partition p:problemSummary.getAllPartitions())	{
			BigInteger combinations=problemSummary.getCombinations(p);
			System.out.println(p+": "+combinations+"!!!!!");
			if (problemSummary.isNegative(p)) result=result.subtract(combinations);
			else result=result.add(combinations);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
