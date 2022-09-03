package com.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Euler792 {
	public static void main(String[] args)	{
		int n=1024;
		BigInteger combi=BigInteger.ONE;
		BigInteger base=BigInteger.TWO.negate();
		BigInteger factor=BigInteger.ONE;
		BigInteger three=BigInteger.valueOf(3l);
		BigInteger four=BigInteger.valueOf(4l);
		BigInteger sum=BigInteger.ZERO;
		IntObjMap<List<Long>> sortedPerValue=HashIntObjMaps.newMutableMap();
		for (long i=1;i<=n;++i)	{
			long ii=i+i;
			BigInteger mult=BigInteger.valueOf(ii*(ii-1));
			BigInteger div=BigInteger.valueOf(i*i);
			combi=combi.multiply(mult).divide(div);
			factor=factor.multiply(base);
			sum=sum.add(combi.multiply(factor));
			System.out.println(String.format("S(%d)=%s (f=%s) => %s.",i,sum,combi.multiply(factor).toString(),sum.toString(2)));
			int u=three.multiply(sum).add(four).getLowestSetBit();
			sortedPerValue.computeIfAbsent((int)(u-i),(int unused)->new ArrayList<>()).add(i);
		}
		int min=Integer.MAX_VALUE;
		int max=0;
		for (IntCursor cursor=sortedPerValue.keySet().cursor();cursor.moveNext();)	{
			min=Math.min(min,cursor.elem());
			max=Math.max(max,cursor.elem());
		}
		for (int i=min;i<=max;++i)	{
			List<Long> values=sortedPerValue.get(i);
			if (values!=null) System.out.println(String.format("%d=%s.",i,values.toString()));
		}
	}
}
