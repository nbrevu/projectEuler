package com.euler.common;

import java.util.stream.LongStream;

import com.euler.common.LongConvergents.LongContinuedFraction;
import com.koloboke.collect.LongCursor;

public class LongFiniteContinuedFraction implements LongContinuedFraction {
	public final long[] array;
	private LongFiniteContinuedFraction(long[] array)	{
		this.array=array;
	}
	@Override
	public LongCursor getTermsAsCursor() {
		return OtherUtils.arrayToCursor(array);
	}
	public static LongFiniteContinuedFraction getFor(long a,long b)	{
		if (b==0) throw new ArithmeticException();
		LongStream.Builder elements=LongStream.builder();
		elements.accept(a/b);
		long c=a%b;
		while (c!=0)	{
			elements.accept(b/c);
			long mod=b%c;
			b=c;
			c=mod;
		}
		long[] longArray=elements.build().toArray();
		return new LongFiniteContinuedFraction(longArray);
	}
}
