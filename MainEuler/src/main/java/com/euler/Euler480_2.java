package com.euler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class Euler480_2 {
	private final static String PHRASE="thereisasyetinsufficientdataforameaningfulanswer";
	private final static int CHAR_LIMIT=15;
	
	private static class SortedCharCounts	{
		private final int[] array;
		private SortedCharCounts(int[] array)	{
			this.array=array;
		}
		public static SortedCharCounts fromCharMap(Map<Character,Integer> charMap)	{
			int[] array=new int[charMap.size()];
			int index=0;
			for (int howMany:charMap.values())	{
				array[index]=howMany;
				++index;
			}
			Arrays.sort(array);
			return new SortedCharCounts(array);
		}
		public SortedCharCounts[] getChildren()	{
			SortedCharCounts[] result=new SortedCharCounts[array.length];
			for (int i=0;i<array.length;++i) if (array[i]==1) result[i]=new SortedCharCounts(Arrays.copyOfRange(array,1,array.length));
			else	{
				int[] newArray=Arrays.copyOf(array,array.length);
				--newArray[i];
				Arrays.sort(newArray);
				result[i]=new SortedCharCounts(newArray);
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			SortedCharCounts ccOther=(SortedCharCounts)other;
			return Arrays.equals(array,ccOther.array);
		}
	}
	
	private static class VariationsCache	{
		public Table<SortedCharCounts,Integer,Long> cache;
		public Map<SortedCharCounts,SortedCharCounts[]> childCache;
		public VariationsCache()	{
			cache=HashBasedTable.create();
			childCache=new HashMap<>();
		}
		public long getVariations(SortedCharCounts counts,Integer howMany)	{
			if (howMany.intValue()==0) return 1l;
			Long result=cache.get(counts,howMany);
			if (result==null)	{
				result=calculateVariations(counts,howMany);
				cache.put(counts,howMany,result);
			}
			return result;
		}
		private long calculateVariations(SortedCharCounts counts,int howMany)	{
			long result=1;
			for (SortedCharCounts child:getChildren(counts)) result+=getVariations(child,howMany-1);
			return result;
		}
		private SortedCharCounts[] getChildren(SortedCharCounts counts)	{
			return childCache.computeIfAbsent(counts,SortedCharCounts::getChildren);
		}
	}
	
	private static class WordConverter	{
		private static SortedMap<Character,Integer> synthetizeMap(String phrase)	{
			SortedMap<Character,Integer> result=new TreeMap<>();
			for (char c:phrase.toCharArray()) EulerUtils.increaseCounter(result,c);
			return result;
		}
		private final SortedMap<Character,Integer> mainCharMap;
		private final int maxLength;
		private final VariationsCache varCalculator;
		public WordConverter(String phrase,int maxLength)	{
			mainCharMap=synthetizeMap(phrase);
			this.maxLength=maxLength;
			varCalculator=new VariationsCache();
		}
		private long getTotalCount(SortedMap<Character,Integer> charMap,int howMany)	{
			if (howMany==0) return 1l;
			return varCalculator.calculateVariations(SortedCharCounts.fromCharMap(charMap),howMany);
		}
		private SortedMap<Character,Integer> getChild(SortedMap<Character,Integer> charMap,Character toRemove)	{
			SortedMap<Character,Integer> result=new TreeMap<>(charMap);
			EulerUtils.decreaseCounter(result,toRemove);
			return result;
		}
		public long stringToLong(String in)	{
			Iterator<Character> iter=Lists.charactersOf(in).iterator();
			return stringToLongRecursive(iter,mainCharMap,maxLength);
		}
		private long stringToLongRecursive(Iterator<Character> iterator,SortedMap<Character,Integer> charMap,int length)	{
			if (!iterator.hasNext()) return 0l;
			else if (length==0) throw new IllegalArgumentException("String too long.");
			char currentChar=iterator.next();
			long result=0;
			for (char c:charMap.keySet()) if (c<currentChar) result+=getTotalCount(getChild(charMap,c),length-1);
			else if (c==currentChar) return result+1+stringToLongRecursive(iterator,getChild(charMap,c),length-1);
			else break;
			throw new IllegalArgumentException("Unexpected char.");
		}
		public String longToString(long in)	{
			StringBuilder result=new StringBuilder();
			longToStringRecursive(in,result,mainCharMap,maxLength);
			return result.toString();
		}
		private void longToStringRecursive(long in,StringBuilder result,SortedMap<Character,Integer> charMap,int maxLength)	{
			if (in==0) return;
			else if (maxLength==0) throw new IllegalArgumentException("Input number too big.");
			else if (in<0) throw new IllegalArgumentException("Wrong calculations.");
			long currentSum=0l;
			for (Map.Entry<Character,Integer> entry:charMap.entrySet())	{
				char c=entry.getKey();
				SortedMap<Character,Integer> child=getChild(charMap,c);
				long newSum=getTotalCount(child,maxLength-1);
				if (currentSum+newSum>=in)	{
					result.append(c);
					longToStringRecursive(in-currentSum-1,result,child,maxLength-1);
					return;
				}	else currentSum+=newSum;
			}
			throw new IllegalArgumentException("Input number too big.");
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		WordConverter converter=new WordConverter(PHRASE,CHAR_LIMIT);
		long resultLong=0l;
		resultLong+=converter.stringToLong("legionary");
		resultLong+=converter.stringToLong("calorimeters");
		resultLong-=converter.stringToLong("annihilate");
		resultLong+=converter.stringToLong("orchestrated");
		resultLong-=converter.stringToLong("fluttering");
		String result=converter.longToString(resultLong);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
