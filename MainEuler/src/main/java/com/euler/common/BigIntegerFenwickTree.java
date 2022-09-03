package com.euler.common;

import java.math.BigInteger;
import java.util.List;

public class BigIntegerFenwickTree extends FenwickTree<BigInteger>	{
	public BigIntegerFenwickTree(int maxValue) {
		super(maxValue);
	}
	@Override
	protected BigInteger[] createArray(int length) {
		return new BigInteger[length];
	}
	@Override
	protected BigInteger getZeroElement(int index) {
		return BigInteger.ZERO;
	}
	@Override
	protected BigInteger sum(List<BigInteger> values) {
		BigInteger result=BigInteger.ZERO;
		for (BigInteger i:values) result=result.add(i);
		return result;
	}
	@Override
	protected BigInteger sumTwo(BigInteger a,BigInteger b)	{
		return a.add(b);
	}
}