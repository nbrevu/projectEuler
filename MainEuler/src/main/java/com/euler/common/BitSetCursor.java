package com.euler.common;

import java.util.BitSet;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import com.koloboke.collect.IntCursor;

public class BitSetCursor implements IntCursor {
	private final IntUnaryOperator updateFunction;
	private int currentIndex;
	public BitSetCursor(long number,boolean useSetBits)	{
		this(BitSet.valueOf(new long[]{number}),useSetBits);
	}
	public BitSetCursor(BitSet bitset,boolean valueToIterateOver)	{
		updateFunction=valueToIterateOver?bitset::nextSetBit:bitset::nextClearBit;
		currentIndex=-1;
	}
	public BitSetCursor(BitSet bitset,int startFrom)	{
		updateFunction=bitset::nextSetBit;
		currentIndex=startFrom;
	}
	@Override
	public boolean moveNext() {
		currentIndex=updateFunction.applyAsInt(currentIndex+1);
		return currentIndex>=0;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int elem() {
		return currentIndex;
	}

	@Override
	public void forEachForward(IntConsumer arg0) {
		while (moveNext()) arg0.accept(elem());
	}
}
