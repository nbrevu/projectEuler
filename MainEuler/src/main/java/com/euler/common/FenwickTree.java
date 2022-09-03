package com.euler.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;

import com.koloboke.collect.IntCursor;

public abstract class FenwickTree<T> {
	private static class ReadIndexCalculator implements IntCursor	{
		private int currentValue;
		private int remaining;
		public ReadIndexCalculator(int value)	{
			currentValue=0;
			remaining=value;
		}
		@Override
		public boolean moveNext() {
			if (remaining==0) return false;
			int highBit=Integer.highestOneBit(remaining);
			currentValue+=highBit;
			remaining-=highBit;
			return true;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int elem() {
			return currentValue;
		}
		@Override
		public void forEachForward(IntConsumer arg0) {
			while (moveNext()) arg0.accept(elem());
		}
	}
	protected abstract T[] createArray(int length);
	protected abstract T getZeroElement(int index);
	protected abstract T sum(List<T> values);
	protected T sumTwo(T a,T b)	{
		// Feel free to redefine this for efficiency.
		return sum(Arrays.asList(a,b));
	}
	
	private final int size;
	private final T[] data;
	public FenwickTree(int maxValue)	{
		size=2*Integer.highestOneBit(1+maxValue);
		data=createArray(size);
		for (int i=1;i<size;++i) data[i]=getZeroElement(i);
	}
	private int getNextWriteIndex(int x)	{
		return x+Integer.lowestOneBit(x);
	}
	public void putData(int value,T newData)	{
		for (int i=value;i<size;i=getNextWriteIndex(i)) data[i]=sumTwo(data[i],newData);
	}
	public T readData(int value)	{
		List<T> collectedData=new ArrayList<>();
		for (IntCursor indexCursor=new ReadIndexCalculator(value);indexCursor.moveNext();) collectedData.add(data[indexCursor.elem()]);
		return sum(collectedData);
	}
	public T getTotal()	{
		return readData(data.length-1);
	}
}
