package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.euler.common.EulerUtils.Pair;

/**
 * Solution consisting of a single (x,y) pair.
 */
public class FixedSolution implements LegendreTransformableSolution<FixedSolution> {
	public final BigInteger x;
	public final BigInteger y;
	private final List<Pair<BigInteger,BigInteger>> asList;
	public FixedSolution(BigInteger x,BigInteger y)	{
		this.x=x;
		this.y=y;
		asList=Arrays.asList(new Pair<>(x,y));
	}
	@Override
	public Iterator<Pair<BigInteger,BigInteger>> iterator() {
		return asList.iterator();
	}
	@Override
	public Optional<FixedSolution> revertLegendreTransform(LegendreTransformation transform) {
		BigInteger numX=x.add(transform.alpha);
		BigInteger[] divX=numX.divideAndRemainder(transform.det);
		if (divX[1].signum()!=0) return Optional.empty();
		BigInteger numY=y.add(transform.beta);
		BigInteger[] divY=numY.divideAndRemainder(transform.det);
		if (divY[1].signum()!=0) return Optional.empty();
		return Optional.of(new FixedSolution(divX[0],divY[0]));
	}
	@Override
	public int hashCode()	{
		return Objects.hash(x,y);
	}
	@Override
	public boolean equals(Object other)	{
		if (other instanceof FixedSolution)	{
			FixedSolution fsOther=(FixedSolution)other;
			return x.equals(fsOther.x)&&y.equals(fsOther.y);
		}	else return false;
	}
	@Override
	public String toString()	{
		return String.format("(%s,%s)",x.toString(),y.toString());
	}
}
