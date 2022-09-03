package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;

import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler768_5 {
	private final static int CANDLES=20;
	private final static int HOLES=360;
	
	private static class RawPartition	{
		private final IntIntMap counters;
		private RawPartition(IntIntMap counters)	{
			this.counters=counters;
		}
		public static RawPartition initialPartition()	{
			return new RawPartition(HashIntIntMaps.newMutableMap());
		}
		public RawPartition addValue(int x)	{
			IntIntMap newCounters=HashIntIntMaps.newMutableMap(counters);
			newCounters.addValue(x,1);
			return new RawPartition(newCounters);
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
			RawPartition pOther=(RawPartition)other;
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
		public static SortedPartition prepare(RawPartition p)	{
			IntIntMap pieces=p.getCounters();
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
		public int isChildOf(SortedPartition other)	{
			IntIntMap origin=HashIntIntMaps.newMutableMap();
			IntIntMap target=HashIntIntMaps.newMutableMap();
			for (PartitionPiece p:pieces) origin.addValue(p.key,p.counter);
			for (PartitionPiece p:other.pieces)	{
				int key=p.key;
				int value=p.counter;
				if (origin.containsKey(key))	{
					int oldValue=origin.get(key);
					if (oldValue<=value)	{
						origin.remove(key);
						if (oldValue<value) target.addValue(key,value-oldValue);
					}	else origin.addValue(key,-value);
				}	else target.addValue(p.key,p.counter);
			}
			return findPathsRecursive(origin,target);
		}
		private static int findPathsRecursive(IntIntMap origin,IntIntMap target)	{
			int result=0;
			if (origin.isEmpty()&&target.isEmpty()) return 1;
			for (IntIntCursor origCursor=origin.cursor();origCursor.moveNext();)	{
				int origKey=origCursor.key();
				int origVal=origCursor.value();
				for (IntIntCursor targCursor=target.cursor();targCursor.moveNext();)	{
					int targKey=targCursor.key();
					if ((targKey%origKey)==0)	{
						int q=targKey/origKey;
						if (origVal>=q) result+=findPathsRecursive(remove(origin,origKey,q),remove(target,targKey,1));
					}
				}
			}
			return result;
		}
		private static IntIntMap remove(IntIntMap map,int key,int value)	{
			IntIntMap result=HashIntIntMaps.newMutableMap(map);
			if (result.get(key)==value) result.remove(key);
			else result.addValue(key,-value);
			return result;
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
	
	private static class Decomposition	{
		private final SortedMap<Integer,Integer> entries;
		public Decomposition(int value,int gap,int times)	{
			entries=new TreeMap<>();
			for (int i=0;i<times;++i) entries.put(gap*i,value);
		}
		private Decomposition(SortedMap<Integer,Integer> entries)	{
			this.entries=entries;
		}
		public Decomposition replace(Integer position,Decomposition smallerDecomp)	{
			SortedMap<Integer,Integer> newEntries=new TreeMap<>(entries);
			newEntries.remove(position);
			for (Map.Entry<Integer,Integer> smallerEntry:smallerDecomp.entries.entrySet()) newEntries.put(smallerEntry.getKey()+position,smallerEntry.getValue()); 
			return new Decomposition(newEntries);
		}
		public SortedPartition getAsPartition()	{
			IntIntMap result=HashIntIntMaps.newMutableMap();
			for (int v:entries.values()) result.addValue(v,1);
			return SortedPartition.prepare(new RawPartition(result));
		}
		@Override
		public int hashCode()	{
			return entries.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			Decomposition dOther=(Decomposition)other;
			return entries.equals(dOther.entries);
		}
		@Override
		public String toString()	{
			return entries.toString();
		}
	}
	
	private static class Decompositions	{
		private final Set<Decomposition> decompositions;
		private final int value;
		private final int gap;
		public Decompositions(int value,int totalSize)	{
			this.value=value;
			decompositions=new LinkedHashSet<>();
			gap=totalSize/value;
		}
		private void init(IntObjMap<Decompositions> preexisting)	{
			for (int i=2;i<=value/2;++i) if ((value%i)==0)	{
				int q=value/i;
				Decomposition baseDecomp=new Decomposition(i,gap,q);
				decompositions.add(baseDecomp);
				Decompositions previousData=preexisting.get(i);
				if (!previousData.decompositions.isEmpty()) addDecompositionsRecursive(baseDecomp,previousData.decompositions,i,0);
			}
		}
		private void addDecompositionsRecursive(Decomposition currentDecomp,Set<Decomposition> toDecompose,Integer toReplace,Integer position)	{
			if (!toReplace.equals(currentDecomp.entries.get(position))) return;
			Integer nextPosition=position+gap;
			addDecompositionsRecursive(currentDecomp,toDecompose,toReplace,nextPosition);
			for (Decomposition smallerDecomp:toDecompose)	{
				Decomposition newDecomp=currentDecomp.replace(position,smallerDecomp);
				decompositions.add(newDecomp);
				addDecompositionsRecursive(newDecomp,toDecompose,toReplace,nextPosition);
			}
		}
		@Override
		public String toString()	{
			return decompositions.toString();
		}
		public ObjIntMap<SortedPartition> getPartitionsSummary()	{
			ObjIntMap<SortedPartition> result=HashObjIntMaps.newMutableMap();
			for (Decomposition d:decompositions) result.addValue(d.getAsPartition(),1);
			return result;
		}
	}
	
	private static IntObjMap<Decompositions> getAllDecompositions(int totalSize,int maxCounter)	{
		IntObjMap<Decompositions> result=HashIntObjMaps.newMutableMap();
		for (int i=2;i<=maxCounter;++i) if ((totalSize%i)==0)	{
			Decompositions decomps=new Decompositions(i,totalSize);
			decomps.init(result);
			result.put(i,decomps);
		}
		return result;
	}
	
	private static class CompatibilitySummary	{
		private static class SingleValueIncompatibilityList	{
			private final IntObjMap<BitSet[]> compatibilities;
			public SingleValueIncompatibilityList()	{
				compatibilities=HashIntObjMaps.newMutableMap();
			}
			public void addInformation(int value,BitSet[] incompatibilities)	{
				compatibilities.put(value,incompatibilities);
			}
		}
		// Needed because arrays of generic classes gehen nicht. This is the kind of shit that makes me hate Java sometimes.
		private static class PartitionSet	{
			public final Set<RawPartition> partitions;
			public PartitionSet(Set<RawPartition> partitions)	{
				this.partitions=partitions;
			}
		}
		private final int totalSize;
		private final IntObjMap<SingleValueIncompatibilityList> compatibility;
		private final ObjIntMap<SortedPartition> partitionsWithCoeffs;
		private final RandomAccessCombinatorialNumberCache combinatorialCache;
		public CompatibilitySummary(int totalSize,int elements)	{
			this.totalSize=totalSize;
			IntStream.Builder validElementsBuilder=IntStream.builder();
			for (int i=2;i<=elements;++i) if ((totalSize%i)==0) validElementsBuilder.accept(i);
			int[] validElements=validElementsBuilder.build().toArray();
			IntSet validElementsSet=HashIntSets.newImmutableSet(validElements);
			SortedPartition[] validPartitions=getPartitions(elements,validElementsSet);
			partitionsWithCoeffs=assignCoefficients(validPartitions);
			compatibility=HashIntObjMaps.newMutableMap();
			for (int i=0;i<validElements.length;++i) compatibility.put(validElements[i],new SingleValueIncompatibilityList());
			for (int i=0;i<validElements.length;++i) for (int j=1+i;j<validElements.length;++j)	{
				int elI=validElements[i];
				int elJ=validElements[j];
				Pair<BitSet[],BitSet[]> currentCompatibility=calculateCompatibility(totalSize,elI,elJ);
				compatibility.get(elI).addInformation(elJ,currentCompatibility.first);
				compatibility.get(elJ).addInformation(elI,currentCompatibility.second);
			}
			combinatorialCache=new RandomAccessCombinatorialNumberCache();
		}
		private static SortedPartition[] getPartitions(int sum,IntSet validElements)	{
			PartitionSet[] partitions=new PartitionSet[1+sum];
			partitions[0]=new PartitionSet(Set.of(RawPartition.initialPartition()));
			partitions[1]=new PartitionSet(Set.of());
			for (int i=2;i<=sum;++i)	{
				Set<RawPartition> currentSet=new HashSet<>();
				for (int j=2;j<=i;++j) if (validElements.contains(j)) for (RawPartition p:partitions[i-j].partitions) currentSet.add(p.addValue(j));
				partitions[i]=new PartitionSet(currentSet);
			}
			Set<RawPartition> baseResult=partitions[sum].partitions;
			return baseResult.stream().map(SortedPartition::prepare).toArray(SortedPartition[]::new);
		}
		private static ObjIntMap<SortedPartition> assignCoefficients(SortedPartition[] allPartitions)	{
			int size=allPartitions.length;
			int[][] relationships=new int[size][size];
			for (int i=0;i<size;++i) for (int j=0;j<size;++j) if (i!=j) relationships[i][j]=allPartitions[j].isChildOf(allPartitions[i]); 
			IntIntMap indexResult=HashIntIntMaps.newMutableMap();
			for (int i=0;i<size;++i) assignCoefficientsRecursive(i,relationships,indexResult);
			ObjIntMap<SortedPartition> result=HashObjIntMaps.newMutableMap();
			for (int i=0;i<size;++i) result.put(allPartitions[i],indexResult.get(i));
			return result;
		}
		private static void assignCoefficientsRecursive(int index,int[][] relationships,IntIntMap result)	{
			if (result.containsKey(index)) return;
			int value=1;
			for (int i=0;i<relationships[index].length;++i)	{
				int paths=relationships[index][i];
				if (paths!=0)	{
					assignCoefficientsRecursive(i,relationships,result);
					value-=paths*result.get(i);
				}
			}
			result.put(index,value);
		}
		private static Pair<BitSet[],BitSet[]> calculateCompatibility(int totalSize,int sizeA,int sizeB)	{
			int entriesA=totalSize/sizeA;
			int entriesB=totalSize/sizeB;
			BitSet[] incompatibleValuesAToB=new BitSet[entriesA];
			for (int i=0;i<entriesA;++i) incompatibleValuesAToB[i]=new BitSet(entriesB);
			BitSet[] incompatibleValuesBToA=new BitSet[entriesB];
			for (int i=0;i<entriesB;++i) incompatibleValuesBToA[i]=new BitSet(entriesA);
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
				incompatibleValuesAToB[i].set(j,areIncompatible);
				incompatibleValuesBToA[j].set(i,areIncompatible);
			}
			return new Pair<>(incompatibleValuesAToB,incompatibleValuesBToA);
		}
		public Collection<SortedPartition> getAllPartitions()	{
			return partitionsWithCoeffs.keySet();
		}
		public BigInteger getCombinations(SortedPartition partition)	{
			List<PartitionPiece> pieces=partition.pieces;
			if (pieces.size()==1)	{
				PartitionPiece piece=pieces.get(0);
				return combinatorialCache.nchoosek(totalSize/piece.key,piece.counter);
			}
			PartitionPiece initialPiece=pieces.get(0);
			IntObjMap<BitSet> unavailability=HashIntObjMaps.newMutableMap();
			for (PartitionPiece piece:pieces) unavailability.put(piece.key,new BitSet(totalSize/piece.key));
			return getCombinationsRecursive(initialPiece.key,initialPiece.counter,0,pieces.subList(1,pieces.size()),unavailability);
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
					BitSet newRestrictions=compatibility.get(currentKey).compatibilities.get(otherKey)[i];
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
		public int getInclusionExclussionCoefficient(SortedPartition part)	{
			return partitionsWithCoeffs.getInt(part);
		}
		private static BitSet or(BitSet a,BitSet b)	{
			BitSet result=(BitSet)a.clone();
			result.or(b);
			return result;
		}
	}
	
	/*
	 * I feel like I am VERY FUCKING CLOSE, but the result is still wrong...
	 * 
	 * I'm now like 95% sure that the trick is somewhere in the calculation of the inclusion-exclusion coefficients.
	 * I need to think exactly about what happens when a solution can be built "twice" with a single set (like transforming [2->3,3->2] into
	 * 	[6->2] in two different ways).
	 */
	public static void main(String[] args)	{
		{
			IntObjMap<Decompositions> initial=getAllDecompositions(HOLES,CANDLES);
			System.out.println(initial);
			System.out.println();
			IntObjMap<ObjIntMap<SortedPartition>> partitions=HashIntObjMaps.newMutableMap();
			for (IntObjCursor<Decompositions> cursor=initial.cursor();cursor.moveNext();) partitions.put(cursor.key(),cursor.value().getPartitionsSummary());
			System.out.println(partitions);
			System.out.println();
		}
		long tic=System.nanoTime();
		CompatibilitySummary problemSummary=new CompatibilitySummary(HOLES,CANDLES);
		BigInteger result=BigInteger.ZERO;
		for (SortedPartition p:problemSummary.getAllPartitions())	{
			int coeff=problemSummary.getInclusionExclussionCoefficient(p);
			if (coeff==0) System.out.println("\t"+p+": coeff=0!!!!!");
			else	{
				BigInteger combinations=problemSummary.getCombinations(p);
				System.out.println(p+" (coeff="+coeff+"): "+combinations+"!!!!!");
				result=result.add(combinations.multiply(BigInteger.valueOf(coeff)));
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
