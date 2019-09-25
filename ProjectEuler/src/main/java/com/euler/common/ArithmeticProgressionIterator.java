package com.euler.common;

import java.util.function.IntConsumer;

import com.koloboke.collect.IntCursor;

public class ArithmeticProgressionIterator implements IntCursor	{
	private final int initial;
	private final int increment;
	private final int maxVal;
	private int current;
	public ArithmeticProgressionIterator(int initial,int increment,int maxVal)	{
		this.initial=initial;
		this.increment=increment;
		this.maxVal=maxVal;
		reset();
	}
	@Override
	public boolean moveNext() {
		if (current+increment>maxVal) return false;
		current+=increment;
		return true;
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void forEachForward(IntConsumer action) {
		while (moveNext()) action.accept(elem());
	}
	@Override
	public int elem() {
		return current;
	}
	public void reset()	{
		current=initial-increment;
	}
}