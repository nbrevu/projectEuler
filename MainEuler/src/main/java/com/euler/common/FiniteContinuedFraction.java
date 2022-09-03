package com.euler.common;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.Convergents.ContinuedFraction;
import com.koloboke.collect.LongCursor;

public class FiniteContinuedFraction implements ContinuedFraction {
	public final long[] array;
	private FiniteContinuedFraction(long[] array)	{
		this.array=array;
	}
	@Override
	public LongCursor getTermsAsCursor() {
		return OtherUtils.arrayToCursor(array);
	}
	public static FiniteContinuedFraction getFor(long a,long b)	{
		return getFor(BigInteger.valueOf(a),BigInteger.valueOf(b));
	}
	public static FiniteContinuedFraction getFor(BigInteger a,BigInteger b)	{
		if (b.signum()==0) throw new ArithmeticException();
		List<BigInteger> elements=new ArrayList<>();
		BigInteger[] div=a.divideAndRemainder(b);
		elements.add(div[0]);
		BigInteger c=div[1];
		while (c.signum()!=0)	{
			div=b.divideAndRemainder(c);
			elements.add(div[0]);
			b=c;
			c=div[1];
		}
		long[] longArray=elements.stream().mapToLong(BigInteger::longValueExact).toArray();
		return new FiniteContinuedFraction(longArray);
	}
}
