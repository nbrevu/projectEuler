package com.euler.common;

import java.math.BigInteger;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;

import com.koloboke.collect.LongCursor;

public class OtherUtils {
	public static <T,U extends T> List<U> filterAndCast(List<? extends T> baseList,Class<U> castInto)	{
		return baseList.stream().filter(castInto::isInstance).map(castInto::cast).collect(Collectors.toUnmodifiableList());
	}
	public static void appendPolynomial(StringBuilder builder,String var,BigInteger... coeffs)	{
		boolean starting=true;
		for (int i=0;i<coeffs.length;++i)	{
			BigInteger coeff=coeffs[i];
			if (coeff.signum()==0) continue;
			int degree=coeffs.length-1-i;
			if (coeff.signum()<0) builder.append('-');
			else if (!starting) builder.append('+');
			BigInteger abs=coeff.abs();
			if ((degree==0)||!abs.equals(BigInteger.ONE)) builder.append(abs);
			if (degree>=2) builder.append(var).append('^').append(degree);
			else if (degree==1) builder.append(var);
			starting=false;
		}
		if (starting) builder.append("0");
	}
	public static void appendPolynomial(StringBuilder builder,BigInteger... coeffs)	{
		appendPolynomial(builder,"t",coeffs);
	}
	public static void appendCustomPolynomial(StringBuilder builder,BigInteger[] coeffs,String[] vars)	{
		boolean starting=true;
		for (int i=0;i<coeffs.length;++i)	{
			BigInteger coeff=coeffs[i];
			if (coeff.signum()==0) continue;
			if (coeff.signum()<0) builder.append('-');
			else if (!starting) builder.append('+');
			BigInteger abs=coeff.abs();
			String var=vars[i];
			if (var.isBlank()||!abs.equals(BigInteger.ONE)) builder.append(abs);
			builder.append(var);
			starting=false;
		}
		if (starting) builder.append("0");
	}
	
	public static void appendWithSign(StringBuilder builder,BigInteger number)	{
		if (number.signum()>=0) builder.append('+');
		builder.append(number.toString());
	}
	public static LongCursor arrayToCursor(long[] array)	{
		return new LongCursor()	{
			int position=-1;
			@Override
			public boolean moveNext() {
				++position;
				return position<array.length;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			@Override
			public void forEachForward(LongConsumer action) {
				while (moveNext()) action.accept(elem());
			}
			@Override
			public long elem() {
				return array[position];
			}
		};
	}
}
