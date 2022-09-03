package com.euler.common;

import java.util.List;

public class LongWithModFenwickTree extends FenwickTree<Long>	{
	private final long mod;
	public LongWithModFenwickTree(int maxValue,long mod) {
		super(maxValue);
		this.mod=mod;
	}
	@Override
	protected Long[] createArray(int length) {
		return new Long[length];
	}
	@Override
	protected Long getZeroElement(int index) {
		return 0l;
	}
	@Override
	protected Long sum(List<Long> values) {
		return values.stream().reduce(this::sumTwo).get();
	}
	@Override
	protected Long sumTwo(Long a,Long b)	{
		return (a+b)%mod;
	}
}