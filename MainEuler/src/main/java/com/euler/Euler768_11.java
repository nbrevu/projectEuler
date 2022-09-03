package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.euler.common.EulerUtils.Pair;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler768_11 {
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
			return prepare(p.getCounters());
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
		private final IntIntMap factorials;
		public RandomAccessCombinatorialNumberCache()	{
			cache=HashIntObjMaps.newMutableMap();
			factorials=HashIntIntMaps.newMutableMap();
			factorials.put(0,1);
		}
		public BigInteger nchoosek(int n,int k)	{
			IntObjMap<BigInteger> subMap=cache.computeIfAbsent(n,(int unused)->HashIntObjMaps.newMutableMap());
			return subMap.computeIfAbsent(k,(int unused)->calculate(n,k));
		}
		public int combinedNchoosek(int n,int[] ks)	{
			int result=fact(n);
			for (int k:ks) result/=fact(k);
			return result;
		}
		private int fact(int operand)	{
			int result=factorials.get(operand);
			if (result==0)	{
				result=fact(operand-1)*operand;
				factorials.put(operand,result);
			}
			return result;
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
				if (nextListIndex>=pieces.size()) ++result;
				else result+=tryPartitionRecursive(newContents,sum,pieces,nextListIndex,nextPieceIndex,nextPositionIndex);
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
			combinatorialCache=new RandomAccessCombinatorialNumberCache();
			IntStream.Builder validElementsBuilder=IntStream.builder();
			for (int i=2;i<=elements;++i) if ((totalSize%i)==0) validElementsBuilder.accept(i);
			int[] validElements=validElementsBuilder.build().toArray();
			IntSet validElementsSet=HashIntSets.newImmutableSet(validElements);
			IntObjMap<List<Pair<SortedPartition,Integer>>> baseDecompositions=getBaseDecompositions(totalSize,elements);
			SortedPartition[] validPartitions=getPartitions(elements,validElementsSet);
			partitionsWithCoeffs=assignCoefficients(validPartitions,baseDecompositions);
			compatibility=HashIntObjMaps.newMutableMap();
			for (int i=0;i<validElements.length;++i) compatibility.put(validElements[i],new SingleValueIncompatibilityList());
			for (int i=0;i<validElements.length;++i) for (int j=1+i;j<validElements.length;++j)	{
				int elI=validElements[i];
				int elJ=validElements[j];
				Pair<BitSet[],BitSet[]> currentCompatibility=calculateCompatibility(totalSize,elI,elJ);
				compatibility.get(elI).addInformation(elJ,currentCompatibility.first);
				compatibility.get(elJ).addInformation(elI,currentCompatibility.second);
			}
		}
		private static IntObjMap<List<Pair<SortedPartition,Integer>>> getBaseDecompositions(int totalSize,int maxSum)	{
			IntStream.Builder builder=IntStream.builder();
			for (int i=2;i<=maxSum;++i) if ((totalSize%i)==0) builder.accept(i);
			int[] validValues=builder.build().toArray();
			List<Set<IntIntMap>> partitions=new ArrayList<>();
			partitions.add(Set.of(HashIntIntMaps.newMutableMap()));
			partitions.add(Set.of());
			for (int i=2;i<=maxSum;++i)	{
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
			IntObjMap<List<Pair<SortedPartition,Integer>>> result=HashIntObjMaps.newMutableMap();
			for (int i=2;i<filteredPartitions.size();++i)	{
				List<Pair<SortedPartition,Integer>> list=new ArrayList<>();
				for (SortedPartition p:filteredPartitions.get(i))	{
					if (isTrivialPartition(p)) continue;
					int cases=tryPartition(i,p);
					if (cases!=0) list.add(new Pair<>(p,cases));
				}
				result.put(i,list);
			}
			return result;
		}
		private static boolean isTrivialPartition(SortedPartition p)	{
			return (p.pieces.size()==1)&&(p.pieces.get(0).counter==1);
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
		private ObjIntMap<SortedPartition> assignCoefficients(SortedPartition[] allPartitions,IntObjMap<List<Pair<SortedPartition,Integer>>> baseDecompositions)	{
			ObjIntMap<SortedPartition> result=HashObjIntMaps.newMutableMap();
			Map<PartitionPiece,ObjIntMap<SortedPartition>> partialDecompositionsCache=new HashMap<>();
			for (SortedPartition p:allPartitions) assignCoefficientRecursive(p,result,partialDecompositionsCache,baseDecompositions);
			return result;
		}
		private void assignCoefficientRecursive(SortedPartition partition,ObjIntMap<SortedPartition> result,Map<PartitionPiece,ObjIntMap<SortedPartition>> partialDecompositionsCache,IntObjMap<List<Pair<SortedPartition,Integer>>> baseDecompositions)	{
			if (result.containsKey(partition)) return;
			List<ObjIntMap<SortedPartition>> partialDecompositions=partition.pieces.stream().map((PartitionPiece p)->getPartialDecomposition(p,baseDecompositions.get(p.key))).collect(Collectors.toList());
			int value=1;
			for (ObjIntCursor<SortedPartition> cursor=combine(partialDecompositions).cursor();cursor.moveNext();)	{
				SortedPartition subPartition=cursor.key();
				int times=cursor.value();
				if (cursor.key().equals(partition)) continue;
				assignCoefficientRecursive(subPartition,result,partialDecompositionsCache,baseDecompositions);
				value-=times*result.getInt(subPartition);
			}
			result.put(partition,value);
		}
		private ObjIntMap<SortedPartition> getPartialDecomposition(PartitionPiece p,List<Pair<SortedPartition,Integer>> replacements)	{
			ObjIntMap<SortedPartition> result=HashObjIntMaps.newMutableMap();
			if (replacements.isEmpty())	{
				result.put(new SortedPartition(Lists.newArrayList(p)),1);
				return result;
			}
			int[] mapper=new int[replacements.size()+1];
			for (int i=0;i<=p.counter;++i)	{
				mapper[0]=i;
				fillArray(mapper,1,p.counter-i,p,replacements,result);
			}
			return result;
		}
		private void fillArray(int[] mapper,int index,int remaining,PartitionPiece p,List<Pair<SortedPartition,Integer>> replacements,ObjIntMap<SortedPartition> result)	{
			if (index==mapper.length-1)	{
				mapper[index]=remaining;
				int fullValue=combinatorialCache.combinedNchoosek(p.counter,mapper);
				IntIntMap counters=HashIntIntMaps.newMutableMap();
				for (int i=0;i<replacements.size();++i) if (mapper[i]>0)	{
					Pair<SortedPartition,Integer> element=replacements.get(i);
					SortedPartition partition=element.first;
					int value=element.second;
					fullValue*=IntMath.pow(value,mapper[i]);
					for (PartitionPiece piece:partition.pieces) counters.addValue(piece.key,piece.counter*mapper[i]);
				}
				if (mapper[replacements.size()]>0) counters.addValue(p.key,mapper[replacements.size()]);
				result.addValue(SortedPartition.prepare(counters),fullValue);
			}	else for (int i=0;i<=remaining;++i)	{
				mapper[index]=i;
				fillArray(mapper,index+1,remaining-i,p,replacements,result);
			}
		}
		private static ObjIntMap<SortedPartition> combine(List<ObjIntMap<SortedPartition>> partialDecompositions)	{
			IntIntMap counter=HashIntIntMaps.newMutableMap();
			ObjIntMap<SortedPartition> result=HashObjIntMaps.newMutableMap();
			combineRecursive(0,counter,1,partialDecompositions,result);
			return result;
		}
		private static void combineRecursive(int currentIndex,IntIntMap currentCounter,int currentValue,List<ObjIntMap<SortedPartition>> partialDecompositions,ObjIntMap<SortedPartition> result)	{
			if (currentIndex>=partialDecompositions.size())	{
				result.addValue(SortedPartition.prepare(currentCounter),currentValue);
				return;
			}
			ObjIntMap<SortedPartition> toAdd=partialDecompositions.get(currentIndex);
			for (ObjIntCursor<SortedPartition> cursor=toAdd.cursor();cursor.moveNext();)	{
				IntIntMap newCounter=HashIntIntMaps.newMutableMap(currentCounter);
				for (PartitionPiece p:cursor.key().pieces) newCounter.addValue(p.key,p.counter);
				combineRecursive(1+currentIndex,newCounter,cursor.value()*currentValue,partialDecompositions,result);
			}
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
		public List<BitSet> getFullSolutions(SortedPartition p)	{
			List<BitSet> result=new ArrayList<>();
			List<PartitionPiece> pieces=p.pieces;
			BitSet currentSet=new BitSet(totalSize);
			getFullSolutionsRecursive(pieces,currentSet,0,0,0,result);
			return result;
		}
		private void getFullSolutionsRecursive(List<PartitionPiece> pieces,BitSet currentSet,int currentListIndex,int currentPieceIndex,int currentWriteIndex,List<BitSet> result)	{
			PartitionPiece piece=pieces.get(currentListIndex);
			int currentKey=piece.key;
			int quot=totalSize/currentKey;
			for (int i=currentWriteIndex;i<quot;++i)	{
				boolean isValid=true;
				for (int j=0;j<currentKey;++j) if (currentSet.get(i+j*quot))	{
					isValid=false;
					break;
				}
				if (isValid)	{
					BitSet nextSet=(BitSet)(currentSet.clone());
					for (int j=0;j<currentKey;++j) nextSet.set(i+j*quot);
					int nextPieceIndex=1+currentPieceIndex;
					if (nextPieceIndex>=piece.counter)	{
						int nextListIndex=1+currentListIndex;
						if (nextListIndex>=pieces.size()) result.add(nextSet);
						else getFullSolutionsRecursive(pieces,nextSet,nextListIndex,0,0,result);
					}	else getFullSolutionsRecursive(pieces,nextSet,currentListIndex,nextPieceIndex,1+i,result);
				}
			}
		}
	}
	
	/*
	 * At this point it's definitely clear that I'm missing something very important. The program definitely does what I want it to do, and I've
	 * finally nailed the inclusion-exclusion (as proven by partial brute force). But there is something I'm missing.
	 * 
	 * Either numbers that don't divide the HOLES variable must be taken into account somehow (how? They don't form cycles...), or there are
	 * solutions outside of the "cycles" approach, which at this point I don't have any hope of finding.
	 * 
	 * Side note, I'm surprised by the fact that every incl-excl coefficient seems to be either 0, 1 or -1. It kind of makes sense, but I
	 * expected weird results.
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		for (int h=10;h<=50;++h) for (int c=2;c<=20;++c)	{
			System.out.println("HOLES="+h+", CANDLES="+c+":");
			CompatibilitySummary problemSummary=new CompatibilitySummary(h,c);
			BigInteger result=BigInteger.ZERO;
			Map<BitSet,ObjIntMap<SortedPartition>> realSolutions=new HashMap<>();
			for (SortedPartition p:problemSummary.getAllPartitions())	{
				List<BitSet> tmpSolutions=problemSummary.getFullSolutions(p);
				for (BitSet b:tmpSolutions)	{
					ObjIntMap<SortedPartition> counters=realSolutions.computeIfAbsent(b,(BitSet unused)->HashObjIntMaps.newMutableMap());
					counters.addValue(p,1);
				}
				int coeff=problemSummary.getInclusionExclussionCoefficient(p);
				if (coeff==0) System.out.println("\t"+p+": coeff=0!!!!!");
				else	{
					BigInteger combinations=problemSummary.getCombinations(p);
					System.out.println(p+" (coeff="+coeff+"): "+combinations+"!!!!!");
					result=result.add(combinations.multiply(BigInteger.valueOf(coeff)));
				}
			}
			ObjIntMap<ObjIntMap<SortedPartition>> furtherSummariser=HashObjIntMaps.newMutableMap();
			for (ObjIntMap<SortedPartition> partitions:realSolutions.values()) furtherSummariser.addValue(partitions,1);
			System.out.println(furtherSummariser);
			System.out.println(result);
			System.out.println("Pero en realidad hay... ¡"+realSolutions.size()+"!");
			if (result.longValueExact()!=realSolutions.size()) throw new RuntimeException("ENDUT! HOCH HECH!");
			System.out.println();
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
