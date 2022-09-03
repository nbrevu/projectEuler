package com.euler;

import com.google.common.math.IntMath;
import com.koloboke.collect.map.IntLongCursor;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;

public class Euler269_2 {
	private static class SolutionCounter	{
		public final long totalSolution;
		public final long solutionWith0;
		public SolutionCounter(long totalSolution,long solutionWith0)	{
			this.totalSolution=totalSolution;
			this.solutionWith0=solutionWith0;
		}
	}
	
	private static IntLongMap oneDigitCases()	{
		IntLongMap result=HashIntLongMaps.newMutableMap();
		for (int i=1;i<=9;++i) result.put(i,1);
		return result;
	}
	
	private static IntLongMap evolve(IntLongMap prevDigits,int root,int maxValue)	{
		IntLongMap result=HashIntLongMaps.newMutableMap();
		for (IntLongCursor cursor=prevDigits.cursor();cursor.moveNext();)	{
			int base=cursor.key()*root;
			if (Math.abs(base)>=maxValue) continue;
			long count=cursor.value();
			for (int i=0;i<=9;++i) result.addValue(base+i,count);
		}
		return result;
	}
	
	private static SolutionCounter countFor(int root,int digits)	{
		int posRoot=-root;
		int maxValue=100*IntMath.pow(posRoot,posRoot/2);
		IntLongMap cases=oneDigitCases();
		long prevCount=0;
		long curCount=0;
		for (int i=2;i<=digits;++i)	{
			cases=evolve(cases,root,maxValue);
			prevCount=curCount;
			curCount+=cases.get(0);
		}
		return new SolutionCounter(curCount,prevCount);
	}
	
	public static void main(String[] args)	{
		for (int i=-1;i>=-10;--i)	{
			SolutionCounter result=countFor(i,16);
			System.out.println(String.format("For root %d, the total is %d, with %d cases with 0",i,result.totalSolution,result.solutionWith0));
		}
	}
}
