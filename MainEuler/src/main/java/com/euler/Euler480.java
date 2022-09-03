package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler480 {
	private final static String PHRASE="thereisasyetinsufficientdataforameaningfulanswer";
	
	private static SortedMap<Character,Integer> synthetizeMap(String phrase)	{
		SortedMap<Character,Integer> result=new TreeMap<>();
		for (char c:phrase.toCharArray()) EulerUtils.increaseCounter(result,c);
		return result;
	}
	
	private static class CharCounts	{
		private final int[] array;
		private CharCounts(int[] array)	{
			this.array=array;
		}
		public static CharCounts fromCharMap(Map<Character,Integer> charMap)	{
			int[] array=new int[charMap.size()];
			int index=0;
			for (int howMany:charMap.values())	{
				array[index]=howMany;
				++index;
			}
			Arrays.sort(array);
			return new CharCounts(array);
		}
		public CharCounts[] getChildren()	{
			CharCounts[] result=new CharCounts[array.length];
			for (int i=0;i<array.length;++i) if (array[i]==1) result[i]=new CharCounts(Arrays.copyOfRange(array,1,array.length));
			else	{
				int[] newArray=Arrays.copyOf(array,array.length);
				--newArray[i];
				Arrays.sort(newArray);
				result[i]=new CharCounts(newArray);
			}
			return result;
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(array);
		}
		@Override
		public boolean equals(Object other)	{
			CharCounts ccOther=(CharCounts)other;
			return Arrays.equals(array,ccOther.array);
		}
	}
	
	private static class VariationsCache	{
		public Table<CharCounts,Integer,BigInteger> cache;
		public Map<CharCounts,CharCounts[]> childCache;
		public VariationsCache()	{
			cache=HashBasedTable.create();
			childCache=new HashMap<>();
		}
		public BigInteger getVariations(CharCounts counts,Integer howMany)	{
			if (howMany.intValue()==0) return BigInteger.ONE;
			BigInteger result=cache.get(counts,howMany);
			if (result==null)	{
				result=calculateVariations(counts,howMany);
				cache.put(counts,howMany,result);
			}
			return result;
		}
		private BigInteger calculateVariations(CharCounts counts,int howMany)	{
			BigInteger result=BigInteger.ONE;
			for (CharCounts child:getChildren(counts)) result=result.add(getVariations(child,howMany-1));
			return result;
		}
		private CharCounts[] getChildren(CharCounts counts)	{
			return childCache.computeIfAbsent(counts,CharCounts::getChildren);
		}
	}
	
	public static void main(String[] args)	{
		CharCounts fullMap=CharCounts.fromCharMap(synthetizeMap(PHRASE));
		System.out.println(new VariationsCache().getVariations(fullMap,15)); 
	}
}
