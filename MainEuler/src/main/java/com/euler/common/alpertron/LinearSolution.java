package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Optional;

import com.euler.common.DiophantineUtils;
import com.euler.common.EulerUtils;
import com.euler.common.EulerUtils.Pair;
import com.euler.common.OtherUtils;

/**
 * Solutions to diophantine equations that can be represented as x=At+B, y=Ct+D. A would be xLin, B would be x0, C would be yLin and D would be y0. 
 */
public class LinearSolution implements LegendreTransformableSolution<LinearSolution> {
	public final BigInteger x0;
	public final BigInteger xLin;
	public final BigInteger y0;
	public final BigInteger yLin;
	public LinearSolution(BigInteger x0,BigInteger xLin,BigInteger y0,BigInteger yLin)	{
		this.x0=x0;
		this.xLin=xLin;
		this.y0=y0;
		this.yLin=yLin;
	}
	public static LinearSolution fixedX(BigInteger x)	{
		return new LinearSolution(x,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ONE);
	}
	public static LinearSolution fixedY(BigInteger y)	{
		return new LinearSolution(BigInteger.ZERO,BigInteger.ONE,y,BigInteger.ZERO);
	}
	@Override
	public Iterator<Pair<BigInteger,BigInteger>> iterator() {
		return new Iterator<>()	{
			private BigInteger currentX=x0;
			private BigInteger currentY=y0;
			@Override
			public boolean hasNext() {
				return true;
			}
			@Override
			public Pair<BigInteger,BigInteger> next() {
				Pair<BigInteger,BigInteger> result=new Pair<>(currentX,currentY);
				currentX=currentX.add(xLin);
				currentY=currentY.add(yLin);
				return result;
			}
		};
	}
	private static class LinearValidParametersExpression	{
		public final BigInteger v0;
		public final BigInteger vLin;
		public LinearValidParametersExpression(BigInteger v0,BigInteger vLin)	{
			this.v0=v0;
			this.vLin=vLin;
		}
	}
	/*
	 * Auxiliary function used during the Legendre transform inversion. Given an equation of the form V=At+B (where A=origVLin and B=origV0),
	 * this method determines the general form of the values of t so that At+B is a multiple of det. The general form is itself a linear form,
	 * t=mK+n where K is any integer.
	 */
	private static LinearValidParametersExpression getValidParameterValues(BigInteger origV0,BigInteger origVLin,BigInteger det)	{
		if (det.abs().equals(BigInteger.ONE)) return new LinearValidParametersExpression(origV0,origVLin);
		if (origVLin.signum()==0)	{
			/*
			 * The slope is 0. If the intercept is a multiple of the modulus, every integer is a solution. Otherwise, no solutions.
			 */
			if (origV0.mod(det).signum()!=0) return null;
			else return new LinearValidParametersExpression(BigInteger.ZERO,BigInteger.ONE);
		}
		// The easy case is out of the way. Now we have: x=(At+B)/det. So At+B==0 (mod det).
		BigInteger g=EulerUtils.gcd(origVLin,det);
		BigInteger[] div=origV0.divideAndRemainder(g);
		if (div[1].signum()!=0) return null;
		BigInteger a=origVLin.divide(g);
		BigInteger b=div[0].negate();
		BigInteger mod=det.divide(g).abs();
		// Since a and mod are coprime, the inverse modulus can be calculated.
		BigInteger inv=EulerUtils.modulusInverse(a,mod);
		// inv*a == 1 (mod).
		// inv*a*b == b (mod).
		// Therefore if t=(inv*b+k*mod), then a*t = a*inv*b + a*k*mod == a*inv*b == b (mod). 
		return new LinearValidParametersExpression(inv.multiply(b).mod(mod),mod);
	}
	/*
	 * Reverting Legendre transforms might not be possible in every case. We have x=At+B and y=Ct+D for certain A, B, C, D values; the first
	 * thing we will do is to search for values of t that make x an integer (t=mK+n) and y an integer (t=pK+n). These solutions are then
	 * combined into a single one (if possible), resulting in a single expression of the form t=rZ+s, where Z is any integer, which is the most
	 * general expression that guarantees that x=At+B+alpha and y=Ct+D+beta are both multiples of det. After replacing the expression of t we get
	 * x=eZ+f, y=gZ+h for some integer values e, f, g, h; these values constitute the reverse transformed solution.
	 */
	@Override
	public Optional<LinearSolution> revertLegendreTransform(LegendreTransformation transform) {
		LinearValidParametersExpression tx=getValidParameterValues(x0.add(transform.alpha),xLin,transform.det);
		if (tx==null) return Optional.empty();
		LinearValidParametersExpression ty=getValidParameterValues(y0.add(transform.beta),yLin,transform.det);
		if (ty==null) return Optional.empty();
		Optional<BigInteger> baseSolution=DiophantineUtils.solveChineseRemainderNonCoprime(tx.v0,tx.vLin,ty.v0,ty.vLin);
		if (baseSolution.isEmpty()) return Optional.empty();
		BigInteger n=baseSolution.get();
		BigInteger m=tx.vLin.multiply(ty.vLin).divide(EulerUtils.gcd(tx.vLin,ty.vLin));
		BigInteger newX0=xLin.multiply(n).add(x0).add(transform.alpha).divide(transform.det);
		BigInteger newXLin=xLin.multiply(m).divide(transform.det);
		BigInteger newY0=yLin.multiply(n).add(y0).add(transform.beta).divide(transform.det);
		BigInteger newYLin=yLin.multiply(m).divide(transform.det);
		return Optional.of(new LinearSolution(newX0,newXLin,newY0,newYLin));
	}
	@Override
	public String toString()	{
		StringBuilder result=new StringBuilder();
		result.append("x=");
		OtherUtils.appendPolynomial(result,xLin,x0);
		result.append("; y=");
		OtherUtils.appendPolynomial(result,yLin,y0);
		return result.toString();
	}
}
