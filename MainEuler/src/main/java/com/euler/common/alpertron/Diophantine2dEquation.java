package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes.PrimeDecomposer;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

/**
 * Standard interface for diophantine equations in two degrees.
 * 
 * @param <T> The type of solution returned by the solveSpecific() method. This is convenient for cases where we want the output of the class
 * to be used by another class. The solve() method will always return a List<DiophantineSolution>, making it more suitable for generic methods.
 */
public interface Diophantine2dEquation<T extends DiophantineSolution> {
	public final static BigInteger FOUR=BigInteger.valueOf(4l);
	public static ObjIntMap<BigInteger> decompose(BigInteger number,PrimeDecomposer decomposer)	{
		LongIntMap baseDecomp=decomposer.decompose(number).getFactorMap();
		ObjIntMap<BigInteger> result=HashObjIntMaps.newMutableMap();
		for (LongIntCursor cursor=baseDecomp.cursor();cursor.moveNext();) result.put(BigInteger.valueOf(cursor.key()),cursor.value());
		return result;
	}
	public List<T> solveSpecific();
	public default List<DiophantineSolution> solve()	{
		return new ArrayList<>(solveSpecific());
	}
}
