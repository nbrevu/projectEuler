package com.euler;

import java.util.ArrayList;
import java.util.List;

import com.euler.common.BaseSquareDecomposition;
import com.euler.common.BaseSquareDecomposition.PrimePowerDecompositionFinder;
import com.euler.common.DivisorHolder;
import com.euler.common.EulerUtils.CombinatorialNumberCache;
import com.euler.common.EulerUtils.LongPair;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class SumSquareDecomposer	{
	private final static BaseSquareDecomposition ONE=new BaseSquareDecomposition(new LongPair(0,1));
	private class InternalCache	{
		private final List<BaseSquareDecomposition> decomps;
		private final PrimePowerDecompositionFinder finder;
		private InternalCache(long prime)	{
			decomps=new ArrayList<>();
			finder=new PrimePowerDecompositionFinder(prime,combis);
		}
		private BaseSquareDecomposition getFor(int pow)	{
			while (pow>decomps.size()) decomps.add(finder.getFor(decomps.size()+1));
			return decomps.get(pow-1);
		}
	}
	private final CombinatorialNumberCache combis;
	private final LongObjMap<InternalCache> baseDecomps;
	public SumSquareDecomposer()	{
		combis=new CombinatorialNumberCache(10);
		baseDecomps=HashLongObjMaps.newMutableMap();
	}
	public BaseSquareDecomposition getFor(long prime,int pow)	{
		return baseDecomps.computeIfAbsent(prime,InternalCache::new).getFor(pow);
	}
	public BaseSquareDecomposition getFor(DivisorHolder divs)	{
		return getFor(divs.getFactorMap());
	}
	public BaseSquareDecomposition getFor(LongIntMap decomp)	{
		if (decomp.isEmpty()) return ONE;
		LongIntCursor cursor=decomp.cursor();
		cursor.moveNext();
		BaseSquareDecomposition result=getFor(cursor.key(),cursor.value());
		while (cursor.moveNext()) result=result.combineWith(getFor(cursor.key(),cursor.value()));
		return result;
	}
}