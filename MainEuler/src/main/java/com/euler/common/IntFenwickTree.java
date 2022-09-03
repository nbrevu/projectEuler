package com.euler.common;

import java.util.List;

public class IntFenwickTree extends FenwickTree<Integer>	{
	public IntFenwickTree(int maxValue) {
		super(maxValue);
	}
	@Override
	protected Integer[] createArray(int length) {
		return new Integer[length];
	}
	@Override
	protected Integer getZeroElement(int index) {
		return 0;
	}
	@Override
	protected Integer sum(List<Integer> values) {
		return values.stream().reduce(this::sumTwo).get();
	}
	@Override
	protected Integer sumTwo(Integer a,Integer b)	{
		return a+b;
	}
}