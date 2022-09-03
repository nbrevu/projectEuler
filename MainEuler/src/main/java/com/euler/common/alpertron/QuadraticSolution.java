package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Iterator;

import com.euler.common.EulerUtils.Pair;
import com.euler.common.OtherUtils;

/**
 * Solution of diophantine equation of the form x=At^2+Bt+C, y=Dt^2+Et+F, for every t integer.
 * These solutions appear sometimes in parabolic equations.
 */
public class QuadraticSolution implements DiophantineSolution {
	public final BigInteger x0;
	public final BigInteger xLin;
	public final BigInteger xQuad;
	public final BigInteger y0;
	public final BigInteger yLin;
	public final BigInteger yQuad;
	public QuadraticSolution(BigInteger x0,BigInteger xLin,BigInteger xQuad,BigInteger y0,BigInteger yLin,BigInteger yQuad)	{
		this.x0=x0;
		this.xLin=xLin;
		this.xQuad=xQuad;
		this.y0=y0;
		this.yLin=yLin;
		this.yQuad=yQuad;
	}
	@Override
	public Iterator<Pair<BigInteger,BigInteger>> iterator() {
		return new Iterator<>()	{
			private BigInteger currentX=x0;
			private BigInteger currentY=y0;
			private BigInteger quadElement=BigInteger.ONE;
			@Override
			public boolean hasNext() {
				return true;
			}
			@Override
			public Pair<BigInteger,BigInteger> next() {
				Pair<BigInteger,BigInteger> result=new Pair<>(currentX,currentY);
				currentX=currentX.add(xLin).add(quadElement.multiply(xQuad));
				currentY=currentY.add(yLin).add(quadElement.multiply(yQuad));
				quadElement=quadElement.add(BigInteger.TWO);
				return result;
			}
		};
	}
	@Override
	public String toString()	{
		StringBuilder result=new StringBuilder();
		result.append("x=");
		OtherUtils.appendPolynomial(result,xQuad,xLin,x0);
		result.append("; y=");
		OtherUtils.appendPolynomial(result,yQuad,yLin,y0);
		return result.toString();
	}
}
