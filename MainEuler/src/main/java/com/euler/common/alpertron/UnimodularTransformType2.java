package com.euler.common.alpertron;

import java.math.BigInteger;

/**
 * "Type 2" unimodular transformation. Given some integer m,
 * x=U+V.
 * y=(m-1)*U+m*V.
 * This transforms an equation in x and y into another one in U and V that can be readily solved by convergents.
 */
public class UnimodularTransformType2 implements UnimodularTransform {
	private final BigInteger m;
	public UnimodularTransformType2(BigInteger m)	{
		this.m=m;
	}
	@Override
	public FixedSolution revertTransform(FixedSolution solution) {
		BigInteger u=solution.x;
		BigInteger v=solution.y;
		BigInteger x=u.add(v);
		BigInteger y=m.subtract(BigInteger.ONE).multiply(u).add(m.multiply(v));
		return new FixedSolution(x,y);
	}
	@Override
	public BigInteger[] transformCoeffs(BigInteger a,BigInteger b,BigInteger c) {
		BigInteger m1=m.subtract(BigInteger.ONE);
		BigInteger m1_2=m1.multiply(m1);
		BigInteger[] result=new BigInteger[3];
		result[0]=a.add(m1.multiply(b)).add(m1_2.multiply(c));
		result[1]=BigInteger.TWO.multiply(a).add(m.add(m1).multiply(b)).add(BigInteger.TWO.multiply(m).multiply(m1).multiply(c));
		result[2]=a.add(m.multiply(b)).add(m.multiply(m).multiply(c));
		return result;
	}
}
