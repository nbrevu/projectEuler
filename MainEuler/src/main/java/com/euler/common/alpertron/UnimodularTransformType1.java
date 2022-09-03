package com.euler.common.alpertron;

import java.math.BigInteger;

/**
 * "Type 1" unimodular transformation. Given some integer m,
 * x=m*U+(m-1)*V.
 * y=U+V.
 * This transforms an equation in x and y into another one in U and V that can be readily solved by convergents.
 */
public class UnimodularTransformType1 implements UnimodularTransform {
	private final BigInteger m;
	public UnimodularTransformType1(BigInteger m)	{
		this.m=m;
	}
	@Override
	public FixedSolution revertTransform(FixedSolution solution) {
		BigInteger u=solution.x;
		BigInteger v=solution.y;
		BigInteger x=m.multiply(u).add(m.subtract(BigInteger.ONE).multiply(v));
		BigInteger y=u.add(v);
		return new FixedSolution(x,y);
	}
	@Override
	public BigInteger[] transformCoeffs(BigInteger a,BigInteger b,BigInteger c) {
		BigInteger m1=m.subtract(BigInteger.ONE);
		BigInteger m1_2=m1.multiply(m1);
		BigInteger[] result=new BigInteger[3];
		result[0]=m.multiply(m).multiply(a).add(m.multiply(b)).add(c);
		result[1]=BigInteger.TWO.multiply(m).multiply(m1).multiply(a).add(m.add(m1).multiply(b)).add(BigInteger.TWO.multiply(c));
		result[2]=m1_2.multiply(a).add(m1.multiply(b)).add(c);
		return result;
	}
}
