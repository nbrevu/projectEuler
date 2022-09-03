package com.euler.common;

import java.util.List;

public class LongFenwickTree extends FenwickTree<Long>	{
	public LongFenwickTree(int maxValue) {
		super(maxValue);
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
		long la=(a==null)?0:a.longValue();
		long lb=(b==null)?0:b.longValue();
		return la+lb;
	}
}