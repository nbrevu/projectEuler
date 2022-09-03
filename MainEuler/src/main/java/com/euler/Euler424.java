package com.euler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

import com.euler.common.CombinationIterator;
import com.euler.common.EulerUtils.Pair;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;
import com.koloboke.collect.map.hash.HashCharObjMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler424 {
	private final static Splitter COMMA_SPLITTER=Splitter.on(',');
	private final static String NO_CELL="X";
	private final static String EMPTY_CELL="O";
	private final static int MAX_KAKURO_SIZE=6;
	
	private final static String FILE_PATH="p424_kakuro200.txt";
	
	private static class KakuroCell	{
		private final int row;
		private final int col;
		private final Optional<Character> hint;
		private final BitSet available;
		public KakuroCell(int row,int col)	{
			this(row,col,Optional.empty());
		}
		public KakuroCell(int row,int col,char hint)	{
			this(row,col,Optional.of(hint));
		}
		private KakuroCell(int row,int col,Optional<Character> hint)	{
			this.row=row;
			this.col=col;
			this.hint=hint;
			available=new BitSet(10);
			init();
		}
		public Optional<Character> getHint()	{
			return hint;
		}
		public void init()	{
			available.set(1,10,true);
		}
		public boolean isFeasible()	{
			return !available.isEmpty();
		}
		public void setSingleValue(int value)	{
			available.clear();
			available.set(value);
		}
		public OptionalInt getSingleValue()	{
			if (available.cardinality()==1) return OptionalInt.of(available.nextSetBit(1));
			else return OptionalInt.empty();
		}
		public void assign(BitSet values)	{
			available.clear();
			available.or(values);
		}
		public void setNotAvailable(int value)	{
			available.clear(value);
		}
		public int remainingPossibilities()	{
			return available.cardinality();
		}
		public BitSet remainingBitSet()	{
			return available;
		}
		@Override
		public String toString()	{
			return "Cell at ("+row+","+col+"): "+available;
		}
	}
	
	private static class BuildingSet	{
		private final String hint;
		private final List<KakuroCell> cells;
		public BuildingSet(String hint)	{
			this.hint=hint;
			cells=new ArrayList<>();
		}
		public void addCell(KakuroCell cell)	{
			cells.add(cell);
		}
		public Pair<String,List<KakuroCell>> build()	{
			return new Pair<>(hint,cells);
		}
	}
	
	private static class KakuroBuilder	{
		private final int size;
		private final BuildingSet[] buildingRows;
		private final BuildingSet[] buildingCols;
		private final List<Pair<String,List<KakuroCell>>> sets;
		private final KakuroCell[][] cells;
		private int curRow;
		private int curCol;
		public KakuroBuilder(int size)	{
			this.size=size;
			buildingRows=new BuildingSet[size-1];
			buildingCols=new BuildingSet[size-1];
			sets=new ArrayList<>();
			cells=new KakuroCell[size-1][size-1];
			curRow=-1;
			curCol=-1;
		}
		public void addCell(String id)	{
			if (id.equals(NO_CELL))	{
				if ((curRow>=0)&&(curCol>=0)) closeBuilding();
			}	else if (id.equals(EMPTY_CELL))	{
				addCell(new KakuroCell(curRow,curCol));
			}	else if (id.length()==1)	{
				char charId=id.charAt(0);
				if ((charId<'A')||(charId>'J')) throw new IllegalArgumentException();
				addCell(new KakuroCell(curRow,curCol,charId));
			}	else if (id.startsWith("(")&&id.endsWith(")"))	{
				if ((curRow>=0)&&(curCol>=0)) closeBuilding();
				List<String> cases=COMMA_SPLITTER.splitToList(id.substring(1,id.length()-1));
				switch (cases.size())	{
					case 1:
						build(cases.get(0));
						break;
					case 2:
						if ((cases.get(0).charAt(0)!='h')||(cases.get(1).charAt(0)!='v')) throw new IllegalArgumentException();
						build(cases.get(0));
						build(cases.get(1));
						break;
					default:
						throw new IllegalArgumentException();
				}
			}	else throw new IllegalArgumentException();
			++curCol;
			if (curCol==size-1)	{
				++curRow;
				curCol=-1;
			}
		}
		private void closeBuilding()	{
			BuildingSet currentRow=buildingRows[curRow];
			if (currentRow!=null)	{
				sets.add(currentRow.build());
				buildingRows[curRow]=null;
			}
			BuildingSet currentCol=buildingCols[curCol];
			if (currentCol!=null)	{
				sets.add(currentCol.build());
				buildingCols[curCol]=null;
			}
		}
		private void addCell(KakuroCell cell)	{
			cells[curRow][curCol]=cell;
			BuildingSet currentRow=buildingRows[curRow];
			if (currentRow!=null) currentRow.addCell(cell);
			BuildingSet currentCol=buildingCols[curCol];
			if (currentCol!=null) currentCol.addCell(cell);
		}
		private void build(String id)	{
			int charId=id.charAt(0);
			String hint=id.substring(1);
			if (!hint.matches("[A-J][A-J]?")) throw new IllegalArgumentException();
			switch (charId)	{
				case 'h':
					buildingRows[curRow]=new BuildingSet(hint);
					break;
				case 'v':
					buildingCols[curCol]=new BuildingSet(hint);
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		private void finish()	{
			for (int i=0;i<size-1;++i)	{
				BuildingSet rowSet=buildingRows[i];
				if (rowSet!=null)	{
					sets.add(rowSet.build());
					buildingRows[i]=null;
				}
				BuildingSet colSet=buildingCols[i];
				if (colSet!=null)	{
					sets.add(colSet.build());
					buildingRows[i]=null;
				}
			}
		}
		public KakuroCell[][] getCells()	{
			return cells;
		}
		public List<Pair<String,List<KakuroCell>>> getSets()	{
			return sets;
		}
	}
	
	private static class LetterKakuro	{
		private final KakuroCell[][] cells;
		private final List<Pair<String,List<KakuroCell>>> sets;
		private LetterKakuro(KakuroCell[][] cells,List<Pair<String,List<KakuroCell>>> sets)	{
			this.cells=cells;
			this.sets=sets;
		}
		public static LetterKakuro fromString(String in)	{
			Iterator<String> entries=COMMA_SPLITTER.split(in).iterator();
			int size=Integer.parseInt(entries.next());
			KakuroBuilder builder=new KakuroBuilder(size);
			for (int i=0;i<size;++i) for (int j=0;j<size;++j)	{
				String id=entries.next();
				if (id.startsWith("(")&&!id.endsWith(")")) id+=","+entries.next();
				builder.addCell(id);
			}
			if (entries.hasNext()) throw new IllegalArgumentException();
			builder.finish();
			return new LetterKakuro(builder.getCells(),builder.getSets());
		}
		public void resetCells()	{
			for (KakuroCell[] array:cells) for (KakuroCell cell:array) if (cell!=null) cell.init();
		}
		public KakuroCell[][] getCells()	{
			return cells;
		}
		public List<Pair<String,List<KakuroCell>>> getSets()	{
			return sets;
		}
		public CharObjMap<BitSet> getPossibleCases()	{
			CharObjMap<BitSet> result=initializeBitSets();
			// This can be improved slightly. Considering that the rest of the algorithm is basically a brute force, this is good enough.
			for (Pair<String,List<KakuroCell>> set:sets)	{
				CharObjMap<BitSet> restriction=getRestriction(set.first,set.second.size());
				restriction.forEach((char symbol,BitSet restricted)->result.get(symbol).and(restricted));
			}
			postProcessPossibleCases(result);
			return result;
		}
		private static char idToChar(int id)	{
			return (char)('A'+id);
		}
		private static CharObjMap<BitSet> initializeBitSets()	{
			char[] chars=new char[10];
			for (int i=0;i<10;++i) chars[i]=idToChar(i);
			BitSet[] sets=new BitSet[10];
			for (int i=0;i<10;++i)	{
				sets[i]=new BitSet(10);
				sets[i].set(0,10,true);
			}
			return HashCharObjMaps.newImmutableMap(chars,sets);
		}
		private static CharObjMap<BitSet> getRestriction(String hint,int howManyCells)	{
			switch (hint.length())	{
				case 1:	{
					char hintChar=hint.charAt(0);
					int minimum=getMinimumSum(howManyCells);
					return HashCharObjMaps.newImmutableMapOf(hintChar,atLeast(minimum));
				}
				case 2:	{
					char tens=hint.charAt(0);
					int minimum=Math.max(10,getMinimumSum(howManyCells))/10;
					int maximum=getMaximumSum(howManyCells)/10;
					return HashCharObjMaps.newImmutableMapOf(tens,between(minimum,maximum));
				}
				default:throw new IllegalArgumentException();
			}
		}
		private static int getMinimumSum(int howManyCells)	{
			int result=0;
			for (int i=1;i<=howManyCells;++i) result+=i;
			return result;
		}
		private static int getMaximumSum(int howManyCells)	{
			int result=0;
			for (int i=0;i<=howManyCells;++i) result+=9-i;
			return result;
		}
		private static BitSet atLeast(int minimum)	{
			BitSet result=new BitSet(10);
			result.set(minimum,10,true);
			return result;
		}
		private static BitSet between(int minimum,int maximum)	{
			BitSet result=new BitSet(10);
			result.set(minimum,1+maximum,true);
			return result;
		}
		private static void postProcessPossibleCases(CharObjMap<BitSet> cases)	{
			for (int i=1;i<=4;++i)	{
				findSubsetsLetterToNumber(cases,i);
				findSubsetsNumberToLetter(cases,i);
			}
		}
		private static void findSubsetsNumberToLetter(CharObjMap<BitSet> cases,int card) {
			// If certain subset of X numbers appears only in certain X letters, then in these letters there can only be said X numbers.
			new CombinationIterator(card,10,false,false).forEach((int[] numberValues)->	{
				BitSet toLookFor=new BitSet();
				for (int value:numberValues) toLookFor.set(value);
				// Note the 10-card instead of card. We are iterating over the complement.
				new CombinationIterator(10-card,10,false,false).forEach((int[] letterValues)->	{
					boolean isPresent=false;
					for (int letterId:letterValues)	{
						char letter=idToChar(letterId);
						if (cases.get(letter).intersects(toLookFor))	{
							isPresent=true;
							break;
						}
					}
					if (!isPresent) for (int i=0;i<10;++i) if (!arrayContains(letterValues,i)) cases.get(idToChar(i)).and(toLookFor);
				});
			});
		}
		private static void findSubsetsLetterToNumber(CharObjMap<BitSet> cases,int card) {
			// If certain subset of X letters only contains certain X numbers, the other 10-X letters can't contain any of the X numbers.
			new CombinationIterator(card,10,false,false).forEach((int[] letterValues)->	{
				new CombinationIterator(card,10,false,false).forEach((int[] numberValues)->	{
					BitSet maximumSuperset=new BitSet(10);
					for (int value:numberValues) maximumSuperset.set(value);
					boolean isRestricted=true;
					for (int letterId:letterValues)	{
						char letter=idToChar(letterId);
						BitSet copy=(BitSet)(cases.get(letter).clone());
						copy.andNot(maximumSuperset);
						if (!copy.isEmpty())	{
							isRestricted=false;
							break;
						}
					}
					if (isRestricted) for (int i=0;i<10;++i) if (!arrayContains(letterValues,i)) cases.get(idToChar(i)).andNot(maximumSuperset);
				});
			});
		}
		private static boolean arrayContains(int[] array,int value)	{
			return Arrays.stream(array).anyMatch((int inArray)->inArray==value);
		}
	}
	
	private static class SumStorage	{
		/*
		 * Key 1: amount of numbers.
		 * Key 2: bitset with the available numbers.
		 * Key 3: sum.
		 * Values: bitset with the OR of the BitSets that actually could arrive to that sum.
		 * For example, if "amount of numbers" = 3, "available numbers" = {1, 2, 3, 4, 6, 7, 9} and "sum"=14, the available sums are:
		 * 	1+4+9
		 *  1+6+7
		 *  3+4+7
		 * So the bitset result would include 1, 3, 4, 6, 7, 9, but not 2.
		 */
		private final IntObjMap<Map<BitSet,IntObjMap<BitSet>>> sums;
		private SumStorage(IntObjMap<Map<BitSet,IntObjMap<BitSet>>> sums)	{
			this.sums=sums;
		}
		public BitSet getFor(int howManyNumbers,BitSet availableDigits,int sum)	{
			Map<BitSet,IntObjMap<BitSet>> map1=sums.get(howManyNumbers);
			if (map1!=null)	{
				IntObjMap<BitSet> map2=map1.get(availableDigits);
				if (map2!=null)	{
					BitSet result=map2.get(sum);
					if (result!=null) return result;
				}
			}
			return new BitSet(10);
		}
		public static SumStorage create(int maxNumber)	{
			IntObjMap<Map<BitSet,IntObjMap<BitSet>>> sums=HashIntObjMaps.newMutableMap();
			long[] helperArray=new long[1];
			for (int i=1;i<=maxNumber;++i)	{
				Map<BitSet,IntObjMap<BitSet>> bitsetMap=new HashMap<>();
				for (helperArray[0]=2;helperArray[0]<1024;helperArray[0]+=2)	{
					BitSet availableDigits=BitSet.valueOf(helperArray);
					if (availableDigits.cardinality()<i) continue;
					IntObjMap<BitSet> sumMap=HashIntObjMaps.newMutableMap();
					int[] digits=new int[availableDigits.cardinality()];
					int index=0;
					for (int number=availableDigits.nextSetBit(1);number>=0;number=availableDigits.nextSetBit(1+number)) {
						digits[index]=number;
						++index;
					}
					new CombinationIterator(i,digits.length,false,false).forEach((int[] combination)->	{
						BitSet digitsInSum=new BitSet(10);
						int sum=0;
						for (int arrayIdx:combination)	{
							int number=digits[arrayIdx];
							digitsInSum.set(number);
							sum+=number;
						}
						BitSet currentBitSet=sumMap.computeIfAbsent(sum,(int unused)->new BitSet(10));
						currentBitSet.or(digitsInSum);
					});
					bitsetMap.put(availableDigits,sumMap);
				}
				sums.put(i,bitsetMap);
			}
			return new SumStorage(sums);
		}
	}
	
	private static class KakuroInstance	{
		private final static SumStorage STORAGE_HELPER=SumStorage.create(MAX_KAKURO_SIZE);
		private final Multimap<Integer,Pair<Integer,List<KakuroCell>>> sets;
		private final Map<KakuroCell,BitSet> originalValues;
		private KakuroInstance(Multimap<Integer,Pair<Integer,List<KakuroCell>>> sets,Map<KakuroCell,BitSet> originalValues)	{
			this.sets=sets;
			this.originalValues=originalValues;
		}
		public static Optional<KakuroInstance> getInitial(LetterKakuro notInstanced,CharIntMap instantiation)	{
			notInstanced.resetCells();
			KakuroCell[][] cells=notInstanced.getCells();
			for (int i=0;i<cells.length;++i) for (int j=0;j<cells[0].length;++j)	{
				KakuroCell cell=cells[i][j];
				if (cell==null) continue;
				if (cell.getHint().isPresent())	{
					int value=instantiation.get(cell.getHint().get().charValue());
					if (value==0) return Optional.empty();
					cell.setSingleValue(value);
				}
			}
			List<Pair<Integer,List<KakuroCell>>> unsortedSets=new ArrayList<>();
			for (Pair<String,List<KakuroCell>> set:notInstanced.getSets()) unsortedSets.add(new Pair<>(convert(set.first,instantiation),new ArrayList<>(set.second)));
			return refine(unsortedSets);
		}
		private static Optional<KakuroInstance> refine(List<Pair<Integer,List<KakuroCell>>> unsortedSets)	{
			if (!propagateAndShrink(unsortedSets)) return Optional.empty();
			Map<KakuroCell,BitSet> originalValues=getValuesFromSets(unsortedSets);
			Multimap<Integer,Pair<Integer,List<KakuroCell>>> sortedSets=sortBySize(unsortedSets);
			return Optional.of(new KakuroInstance(sortedSets,originalValues));
		}
		private Optional<KakuroInstance> setNumber(KakuroCell cell,int value)	{
			cell.setSingleValue(value);
			return refine(new ArrayList<>(sets.values()));
		}
		public void resetCells()	{
			originalValues.forEach(KakuroCell::assign);
		}
		public boolean isSolvable()	{
			if (sets.isEmpty()) return true;
			Pair<Integer,List<KakuroCell>> firstEntry=sets.entries().iterator().next().getValue();
			int sum=firstEntry.first;
			List<KakuroCell> affectedCells=firstEntry.second;
			KakuroCell candidate=null;
			BitSet availableDigits=new BitSet(10);
			for (KakuroCell cell:affectedCells)	{
				if ((candidate==null)||(candidate.remainingPossibilities()>cell.remainingPossibilities())) candidate=cell;
				availableDigits.or(cell.remainingBitSet());
			}
			BitSet possibleSums=STORAGE_HELPER.getFor(affectedCells.size(),availableDigits,sum);
			BitSet realAvailable=(BitSet)(candidate.remainingBitSet().clone());
			realAvailable.and(possibleSums);
			for (int number=realAvailable.nextSetBit(1);number>=0;number=realAvailable.nextSetBit(1+number)) {
				resetCells();
				Optional<KakuroInstance> child=setNumber(candidate,number);
				if (child.isPresent()&&child.get().isSolvable()) return true;
			}
			return false;
		}
		private static BitSet getAvailable(List<KakuroCell> cellList)	{
			BitSet result=new BitSet(10);
			for (KakuroCell cell:cellList) result.or(cell.remainingBitSet());
			return result;
		}
		private static boolean propagateAndShrink(List<Pair<Integer,List<KakuroCell>>> unsortedSets)	{
			boolean anyChange=false;
			List<Pair<Integer,List<KakuroCell>>> toAdd=new ArrayList<>();
			List<Pair<Integer,List<KakuroCell>>> toRemove=new ArrayList<>();
			for (Pair<Integer,List<KakuroCell>> pair:unsortedSets)	{
				int sum=pair.first;
				List<KakuroCell> cells=new ArrayList<>(pair.second);
				if (sum<0) return false;
				else if (sum==0)	{
					if (!cells.isEmpty()) return false;
					toRemove.add(pair);
				}
				BitSet currentlyAvailable=getAvailable(cells);
				BitSet acceptable=STORAGE_HELPER.getFor(cells.size(),currentlyAvailable,sum);
				// First pass: AND to ensure that the values are properly restricted.
				for (KakuroCell cell:cells)	{
					int howManyBefore=cell.remainingPossibilities();
					cell.remainingBitSet().and(acceptable);
					if (cell.remainingPossibilities()<howManyBefore) anyChange=true;
				}
				// Rest of passes: looking for singletons and unfeasible cells.
				for (;;)	{
					List<KakuroCell> fixedCells=new ArrayList<>();
					for (KakuroCell cell:cells) if (!cell.isFeasible()) return false;
					else if (cell.remainingPossibilities()==1)	{
						anyChange=true;
						int value=cell.getSingleValue().getAsInt();
						sum-=value;
						for (KakuroCell cell2:cells) if (cell!=cell2) cell2.setNotAvailable(value);
						fixedCells.add(cell);
					}
					if (fixedCells.isEmpty()) break;
					cells.removeAll(fixedCells);
				}
				if (sum!=pair.first)	{
					toRemove.add(pair);
					toAdd.add(new Pair<>(sum,cells));
				}
			}
			unsortedSets.removeAll(toRemove);
			unsortedSets.addAll(toAdd);
			if (anyChange) return propagateAndShrink(unsortedSets);
			return true;
		}
		
		private static Map<KakuroCell,BitSet> getValuesFromSets(List<Pair<Integer,List<KakuroCell>>> sets)	{
			Map<KakuroCell,BitSet> result=new HashMap<>();
			for (Pair<Integer,List<KakuroCell>> pair:sets) for (KakuroCell cell:pair.second) result.computeIfAbsent(cell,(KakuroCell theCell)->(BitSet)theCell.remainingBitSet().clone());
			return result;
		}
		
		private static Multimap<Integer,Pair<Integer,List<KakuroCell>>> sortBySize(List<Pair<Integer,List<KakuroCell>>> unsortedSets)	{
			Multimap<Integer,Pair<Integer,List<KakuroCell>>> result=MultimapBuilder.treeKeys().arrayListValues().build();
			for (Pair<Integer,List<KakuroCell>> pair:unsortedSets)	{
				int index=Integer.MAX_VALUE;
				for (KakuroCell cell:pair.second) index=Math.min(index,cell.remainingPossibilities());
				result.put(index,pair);
			}
			return result;
		}
		private static int convert(String str,CharIntMap instantiation)	{
			int result=0;
			for (char letter:str.toCharArray()) result=result*10+instantiation.get(letter);
			return result;
		}
	}
	
	private static class LetterAssignmentIterator	{
		private final List<Map.Entry<Character,BitSet>> available;
		public LetterAssignmentIterator(CharObjMap<BitSet> available)	{
			this.available=new ArrayList<>(available.entrySet());
		}
		public CharIntMap findValidCombination(Predicate<? super CharIntMap> fun)	{
			CharIntMap tempMap=HashCharIntMaps.newMutableMap();
			BitSet alreadyAssigned=new BitSet(10);
			return findValidCombinationRecursive(0,tempMap,alreadyAssigned,fun);
		}
		private CharIntMap findValidCombinationRecursive(int index,CharIntMap tempMap,BitSet alreadyAssigned,Predicate<? super CharIntMap> fun) {
			if (index==10) return fun.test(tempMap)?tempMap:null;
			char letter=available.get(index).getKey().charValue();
			BitSet availableNumbers=available.get(index).getValue();
			for (int number=availableNumbers.nextSetBit(0);number>=0;number=availableNumbers.nextSetBit(1+number)) if (!alreadyAssigned.get(number))	{
				tempMap.put(letter,number);
				alreadyAssigned.set(number);
				CharIntMap result=findValidCombinationRecursive(1+index,tempMap,alreadyAssigned,fun);
				if (result!=null) return result;
				alreadyAssigned.clear(number);
				tempMap.remove(letter);
			}
			return null;
		}
	}
	
	private static long getSolutionIdentifier(CharIntMap solution)	{
		long result=0;
		for (char c='A';c<='J';++c)	{
			result*=10;
			result+=solution.get(c);
		}
		return result;
	}
	
	private static long solveKakuro(String id)	{
		LetterKakuro kakuro=LetterKakuro.fromString(id);
		LetterAssignmentIterator iterator=new LetterAssignmentIterator(kakuro.getPossibleCases());
		CharIntMap theSolution=iterator.findValidCombination((CharIntMap possibleSol)->	{
			Optional<KakuroInstance> instance=KakuroInstance.getInitial(kakuro, possibleSol);
			return instance.isPresent()&&instance.get().isSolvable();
		});
		return getSolutionIdentifier(theSolution);
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		List<String> lines=Files.readAllLines(Paths.get(FILE_PATH));
		long result=lines.parallelStream().mapToLong(Euler424::solveKakuro).sum();
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(result);
	}
}
