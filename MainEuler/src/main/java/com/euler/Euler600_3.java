package com.euler;

import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;

public class Euler600_3 {
	private static boolean isValid(char[] cases)	{
		CharIntMap map=HashCharIntMaps.newMutableMap();
		for (char c:cases) map.addValue(c,1);
		int last=6;
		for (int i=0;i<map.size();++i)	{
			char key=(char)('a'+i);
			if (!map.containsKey(key)) return false;
			int value=map.get(key);
			if (value>last) return false;
			last=value;
		}
		return true;
	}
	
	private static String getCanonicalForm(char[] cases)	{
		NavigableSet<String> sets=new TreeSet<>();
		for (int i=0;i<6;++i)	{
			StringBuilder sb=new StringBuilder();
			for (int j=0;j<6;++j) sb.append(cases[(i+j)%6]);
			sets.add(sb.toString());
			sets.add(sb.reverse().toString());
		}
		return sets.first();
	}
	
	public static void main(String[] args)	{
		ListMultimap<String,String> representations=MultimapBuilder.treeKeys().arrayListValues().build();
		char[] values=new char[6];
		values[0]='a';
		char max1='b';
		for (values[1]='a';values[1]<=max1;++values[1])	{
			char max2=(char)Math.max(max1,values[1]+1);
			for (values[2]='a';values[2]<=max2;++values[2])	{
				char max3=(char)Math.max(max2,values[2]+1);
				for (values[3]='a';values[3]<=max3;++values[3])	{
					char max4=(char)Math.max(max3,values[3]+1);
					for (values[4]='a';values[4]<=max4;++values[4])	{
						char max5=(char)Math.max(max4,values[4]+1);
						for (values[5]='a';values[5]<=max5;++values[5])	{
							if (!isValid(values)) continue;
							String me=String.copyValueOf(values);
							String canonical=getCanonicalForm(values);
							representations.put(canonical,me);
						}
					}
				}
			}
		}
		for (Map.Entry<String,Collection<String>> entry:representations.asMap().entrySet()) System.out.println(entry.getKey()+"=>"+entry.getValue()+".");
	}
}
