package com.euler;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.euler.common.Timing;

public class Euler109 {
	private final static int LIMIT=100;
	
	private static NavigableMap<Integer,Integer> getTwoThrowsSet()	{
		NavigableMap<Integer,Integer> oneThrow=new TreeMap<>();
		oneThrow.put(25,1);
		oneThrow.put(50,1);
		for (int i=1;i<=20;++i) for (int j=1;j<=3;++j) EulerUtils.increaseCounter(oneThrow,i*j);
		NavigableMap<Integer,Integer> result=new TreeMap<>();
		for (Map.Entry<Integer,Integer> entry:oneThrow.entrySet())	{
			int n=entry.getKey();
			int combinations=entry.getValue();
			// Single throws plus miss.
			EulerUtils.increaseCounter(result,n,combinations);
			// Combinations with itself.
			int twofoldCombinations=(combinations*(combinations+1))/2;
			EulerUtils.increaseCounter(result,2*n,twofoldCombinations);
			// Combinations with larger numbers.
			for (Map.Entry<Integer,Integer> entry2:oneThrow.tailMap(n,false).entrySet()) EulerUtils.increaseCounter(result,n+entry2.getKey(),combinations*entry2.getValue());
		}
		EulerUtils.increaseCounter(result,0);
		return result;
	}
	
	private static void sum(NavigableMap<Integer,Integer> source,int amount,NavigableMap<Integer,Integer> result)	{
		for (Map.Entry<Integer,Integer> entry:source.entrySet()) EulerUtils.increaseCounter(result,entry.getKey()+amount,entry.getValue());
	}
	
	private static NavigableMap<Integer,Integer> getThreeThrowsSet()	{
		NavigableMap<Integer,Integer> twoThrows=getTwoThrowsSet();
		NavigableMap<Integer,Integer> result=new TreeMap<>();
		for (int i=2;i<=40;i+=2) sum(twoThrows,i,result);
		sum(twoThrows,50,result);
		return result;
	}
	
	private static int countUpTo(NavigableMap<Integer,Integer> map,int upTo)	{
		int result=0;
		for (int combs:map.headMap(upTo,false).values()) result+=combs;
		return result;
	}
	
	private static long solve()	{
		return countUpTo(getThreeThrowsSet(),LIMIT);
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler109::solve);
	}
}
