package com.euler;

import com.euler.common.EulerUtils;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler483_2 {
	private final static int N=350;
	
	private static long max(LongSet s)	{
		long result=1l;
		for (LongCursor c=s.cursor();c.moveNext();) result=Math.max(result,c.elem());
		return result;
	}
	
	public static void main(String[] args)	{
		LongSet[] values=new LongSet[1+N];
		values[1]=HashLongSets.newImmutableSetOf(1l);
		for (int i=2;i<=N;++i)	{
			LongSet v=HashLongSets.newMutableSet();
			v.add(i);
			for (int j=i/2;j<i;++j)	{
				int extra=i-j;
				for (LongCursor c=values[j].cursor();c.moveNext();) v.add(EulerUtils.lcm(extra,c.elem()));
			}
			System.out.println(String.format("Para n=%d me salen %d posibles valores, de los cuales el mayor es %d.",i,v.size(),max(v)));
			values[i]=v;
		}
		System.out.println(values[N]);
	}
}
