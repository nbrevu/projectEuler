package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.euler.common.EisenstenianTriples120;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.Timing;
import com.euler.common.Triangle;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler143 {
	private final static long LIMIT=120000;
	
	private static long solve()	{
		List<Pair<Long,Long>> pairs=new ArrayList<>();
		for (Triangle t:EisenstenianTriples120.getSimpleTriplesUpTo(LongMath.sqrt(LIMIT,RoundingMode.DOWN)))	{
			long a=t.a;
			long b=t.b;
			long maxI=LIMIT/(a+b);
			for (int i=1;i<=maxI;++i) pairs.add(new Pair<>(i*a,i*b));
		}
		SetMultimap<Long,Long> triangleMap=MultimapBuilder.hashKeys().hashSetValues().build();
		for (Pair<Long,Long> p:pairs)	{
			triangleMap.put(p.first,p.second);
			triangleMap.put(p.second,p.first);
		}
		LongSet found=HashLongSets.newMutableSet();
		for (Pair<Long,Long> p:pairs)	{
			Set<Long> setA=triangleMap.get(p.first);
			Set<Long> setB=triangleMap.get(p.second);
			long sumA=p.first+p.second;
			for (Long c:Sets.intersection(setA,setB)) if (c<=LIMIT-sumA) found.add(sumA+c);
		}
		long result=0;
		for (LongCursor cursor=found.cursor();cursor.moveNext();) result+=cursor.elem();
		return result;
	}

	public static void main(String[] args)	{
		Timing.time(Euler143::solve);
	}
}
