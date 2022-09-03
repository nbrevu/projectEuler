package com.euler.common;

import java.math.BigInteger;

import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.LongObjCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

/**
 * Given a BigInteger X and its factorisation, this iterator will get all the square roots R such that R^2 divides X.
 * For example, if X is 450 = 2*3^2*5^2, R can be 1, 3, 5 or 15.
 * For each value of R, this iterator will provide:
 * - The values of R itself, as a long.
 * - The decomposition of R in the form of a LongIntMap.
 * - The value of X/(R^2), as a BigInteger.
 * - The decomposition of X/(R^2) in the form of an ObjIntMap<BigInteger>. Ready to be used in DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber (wink, wink, nudge, nudge).
 * - Call hasNext() to retrieve the next set of numbers, then call the getters to retrieve the data you need. All this data is calculated on demand.
 */
public class SquareFactorsIterator {
	private final BigInteger number;
	private final ObjIntMap<BigInteger> decomposition;
	private final LongObjCursor<LongIntMap> sqrtCursor;
	public SquareFactorsIterator(BigInteger number,DivisorHolder decomposition)	{
		this.number=number;
		this.decomposition=makeBig(decomposition);
		sqrtCursor=sqrt(decomposition).getUnsortedListOfDivisorsWithDecomposition().cursor();
	}
	public boolean hasNext()	{
		return sqrtCursor.moveNext();
	}
	public long getRoot()	{
		return sqrtCursor.key();
	}
	public LongIntMap getRootDecomposition()	{
		return sqrtCursor.value();
	}
	public BigInteger getQuotient()	{
		BigInteger root=BigInteger.valueOf(sqrtCursor.key());
		return number.divide(root.multiply(root));
	}
	public ObjIntMap<BigInteger> getQuotientDecomposition()	{
		ObjIntMap<BigInteger> result=HashObjIntMaps.newMutableMap(decomposition);
		for (LongIntCursor cursor=sqrtCursor.value().cursor();cursor.moveNext();)	{
			BigInteger factor=BigInteger.valueOf(cursor.key());
			int remaining=result.getInt(factor)-2*cursor.value();
			if (remaining==0) result.removeAsInt(factor);
			else result.addValue(factor,-2*cursor.value());
		}
		return result;
	}
	private static ObjIntMap<BigInteger> makeBig(DivisorHolder decomposition)	{
		ObjIntMap<BigInteger> result=HashObjIntMaps.newMutableMap();
		for (LongIntCursor cursor=decomposition.getFactorMap().cursor();cursor.moveNext();) result.put(BigInteger.valueOf(cursor.key()),cursor.value());
		return result;
	}
	private static DivisorHolder sqrt(DivisorHolder parent)	{
		DivisorHolder result=new DivisorHolder();
		for (LongIntCursor cursor=parent.getFactorMap().cursor();cursor.moveNext();) if (cursor.value()>=2) result.addFactor(cursor.key(),cursor.value()/2);
		return result;
	}
}
