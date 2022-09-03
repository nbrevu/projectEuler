package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Euler625_3 {
	private static int bruteForce()	{
		int sum=0;
		for (int i=1;i<=16;++i) for (int j=1;j<=i;++j) sum+=EulerUtils.gcd(i,j);
		return sum;
	}
	
	private static int triangular()	{
		int sum=0;
		IntObjMap<Pair<IntSet,IntSet>> divsMap=HashIntObjMaps.newMutableMap();
		divsMap.put(1,new Pair<>(HashIntSets.newImmutableSetOf(1),HashIntSets.newMutableSet()));
		divsMap.put(2,new Pair<>(HashIntSets.newImmutableSetOf(2),HashIntSets.newImmutableSetOf(1)));
		divsMap.put(3,new Pair<>(HashIntSets.newImmutableSetOf(3),HashIntSets.newImmutableSetOf(1)));
		divsMap.put(4,new Pair<>(HashIntSets.newImmutableSetOf(4),HashIntSets.newImmutableSetOf(2)));
		divsMap.put(5,new Pair<>(HashIntSets.newImmutableSetOf(5),HashIntSets.newImmutableSetOf(1)));
		divsMap.put(6,new Pair<>(HashIntSets.newImmutableSetOf(6,1),HashIntSets.newImmutableSetOf(2,3)));
		divsMap.put(7,new Pair<>(HashIntSets.newImmutableSetOf(7),HashIntSets.newImmutableSetOf(1)));
		divsMap.put(8,new Pair<>(HashIntSets.newImmutableSetOf(8),HashIntSets.newImmutableSetOf(4)));
		divsMap.put(9,new Pair<>(HashIntSets.newImmutableSetOf(9),HashIntSets.newImmutableSetOf(3)));
		divsMap.put(10,new Pair<>(HashIntSets.newImmutableSetOf(10,1),HashIntSets.newImmutableSetOf(2,5)));
		divsMap.put(11,new Pair<>(HashIntSets.newImmutableSetOf(11),HashIntSets.newImmutableSetOf(1)));
		divsMap.put(12,new Pair<>(HashIntSets.newImmutableSetOf(12,2),HashIntSets.newImmutableSetOf(4,6)));
		divsMap.put(13,new Pair<>(HashIntSets.newImmutableSetOf(13),HashIntSets.newImmutableSetOf(1)));
		divsMap.put(14,new Pair<>(HashIntSets.newImmutableSetOf(14,1),HashIntSets.newImmutableSetOf(2,7)));
		divsMap.put(15,new Pair<>(HashIntSets.newImmutableSetOf(15,1),HashIntSets.newImmutableSetOf(3,5)));
		divsMap.put(16,new Pair<>(HashIntSets.newImmutableSetOf(16),HashIntSets.newImmutableSetOf(8)));
		for (int i=1;i<=16;++i)	{
			Pair<IntSet,IntSet> data=divsMap.get(i);
			IntSet toAdd=data.first;
			IntSet toRemove=data.second;
			int factor=0;
			for (int a:toAdd) factor+=a;
			for (int r:toRemove) factor-=r;
			int linear=16/i;
			int triangular=(linear*(linear+1))/2;
			sum+=triangular*factor;
		}
		return sum;
	}
	
	public static void main(String[] args)	{
		System.out.println(bruteForce());
		System.out.println(triangular());
	}
}
