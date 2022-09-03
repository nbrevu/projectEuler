package com.euler.common;

import com.koloboke.collect.map.LongLongMap;
import com.koloboke.collect.map.hash.HashLongLongMaps;

public class InverseModCache	{
	private final long mod;
	private final LongLongMap cache;
	public InverseModCache(long mod)	{
		this.mod=mod;
		cache=HashLongLongMaps.newMutableMap();
	}
	public long get(long in)	{
		return cache.computeIfAbsent(in,(long x)->{
			return EulerUtils.modulusInverse(x,mod);
		});
	}
}