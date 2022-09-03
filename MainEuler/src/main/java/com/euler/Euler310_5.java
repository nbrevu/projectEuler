package com.euler;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;
import com.google.common.math.IntMath;

public class Euler310_5 {
	private final static int LIMIT=100000;
	
	private static int[] getNimGrundy(int limit)	{
		int[] result=new int[1+limit];
		result[0]=0;
		boolean[] tmpStorage=new boolean[1+IntMath.sqrt(limit,RoundingMode.CEILING)];
		for (int i=1;i<=limit;++i)	{
			int maxFound=0;
			for (int r=1;;++r)	{
				int r2=r*r;
				if (r2>i) break;
				int prev=result[i-r2];
				maxFound=Math.max(maxFound,prev);
				tmpStorage[prev]=true;
			}
			int j=0;
			while (tmpStorage[j]) ++j;
			result[i]=j;
			Arrays.fill(tmpStorage,0,1+maxFound,false);
		}
		return result;
	}
	
	private static long[] createCountersArray(int[] values)	{
		NavigableMap<Integer,Integer> counters=new TreeMap<>();
		for (int i=0;i<values.length;++i) EulerUtils.increaseCounter(counters,values[i]);
		long[] result=new long[1+counters.lastKey()];
		for (int i=0;i<result.length;++i) result[i]=counters.get(i);
		return result;
	}
	
	private static long calculateAcceptableVariations(long[] counters)	{
		// Different number combinations: a, b, c. Acceptable iif a^b^c==0.
		long result=0;
		for (int a=0;a<counters.length-2;++a) for (int b=a+1;b<counters.length-1;++b) for (int c=b+1;c<counters.length;++c) if ((a^(b^c))==0) result+=counters[a]*counters[b]*counters[c];
		// Combinations with one number repeated: a^a^b=0^b=b. Acceptable only if b==0, a!=0.
		for (int a=1;a<counters.length;++a) result+=counters[0]*(counters[a]*(counters[a]+1))/2;
		// Combinations with the same number: a^a^a=0^a=a. We only accept the case a=0.
		result+=(counters[0]*(counters[0]+1)*(counters[0]+2))/6;
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int[] nimGrundy=getNimGrundy(LIMIT);
		long[] counters=createCountersArray(nimGrundy);
		long result=calculateAcceptableVariations(counters);
		System.out.println(result);
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))/1e9;
		System.out.println("Solved in "+seconds+" seconds.");
	}
}