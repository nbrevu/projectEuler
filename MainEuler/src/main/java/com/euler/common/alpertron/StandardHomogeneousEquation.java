package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import com.euler.common.EulerUtils;
import com.euler.common.Primes.PrimeDecomposer;

/**
 * Class for equations of the form Ax^2+Bxy+Cx = rightSide, where the precalculated det=B^2-4AC is not a perfect square. It might be negative,
 * meaning that this solves elliptical equation as well as hyperbolic ones (however, elliptical ones never have recursive solutions, whereas
 * hyperbolic ones almost always have).
 * 
 * This equation defers the solution to another class, after calculating an appropriate unimodular transformation.
 */
public class StandardHomogeneousEquation implements Diophantine2dEquation<FixedSolution> {
	private static class UnimodularIterator implements Iterator<UnimodularTransform>	{
		private BigInteger m=BigInteger.ZERO;
		private boolean isType1Passed=false;
		@Override
		public boolean hasNext() {
			return true;
		}
		private void nextM()	{
			m=m.add(BigInteger.ONE);
			isType1Passed=false;
			
		}
		@Override
		public UnimodularTransform next() {
			if (m.signum()==0)	{
				nextM();
				return NoUnimodularTransform.INSTANCE;
			}
			else if (!isType1Passed)	{
				isType1Passed=true;
				return new UnimodularTransformType1(m);
			}	else	{
				UnimodularTransform result=new UnimodularTransformType2(m);
				nextM();
				return result;
			}
		}
	}
	private final static Iterable<UnimodularTransform> UNIMODULAR_TRANSFORMS=()->new UnimodularIterator();
	private final BigInteger a;
	private final BigInteger b;
	private final BigInteger c;
	private final BigInteger rightSide;
	private final BigInteger det;
	private final PrimeDecomposer decomposer;
	public StandardHomogeneousEquation(BigInteger a,BigInteger b,BigInteger c,BigInteger rightSide,BigInteger det,PrimeDecomposer decomposer) {
		this.a=a;
		this.b=b;
		this.c=c;
		this.rightSide=rightSide;
		this.det=det;
		this.decomposer=decomposer;
	}
	@Override
	public List<FixedSolution> solveSpecific() {
		for (UnimodularTransform transform:UNIMODULAR_TRANSFORMS)	{
			BigInteger[] coeffs=transform.transformCoeffs(a,b,c);
			if ((coeffs[0].signum()!=0)&&EulerUtils.gcd(coeffs[0],rightSide).equals(BigInteger.ONE))	{
				Diophantine2dEquation<FixedSolution> solvableEquation=getEquation(coeffs);
				List<FixedSolution> originalSolutions=solvableEquation.solveSpecific();
				return transform.revertTransform(originalSolutions);
			}
		}
		throw new IllegalStateException("You just reached the end of a literally infinite iterator. Are you Chuck Norris?");
	}
	private Diophantine2dEquation<FixedSolution> getEquation(BigInteger[] coeffs)	{
		if (det.signum()<0) return new EllipticalConvergentSolvableEquation(coeffs[0],coeffs[1],coeffs[2],rightSide,decomposer,det);
		else return new HyperbolicConvergentSolvableEquation(coeffs[0],coeffs[1],coeffs[2],rightSide,decomposer,det);
	}
}
