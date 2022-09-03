package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.Convergents.Convergent;
import com.euler.common.QuadraticRationalContinuedFraction;
import com.euler.common.QuadraticRationalContinuedFraction.IterationType;
import com.google.common.collect.Iterables;

public class ConvergentSolutions {
	/*
	 * Calculate solutions for Ax^2+Bxy+Cy^2=1, knowing that the determinant B^2-4AC is det.
	 */
	public static List<FixedSolution> getSolutions(BigInteger a,BigInteger b,BigInteger c,BigInteger det,boolean useDoublePass)	{
		IterationType iterations=useDoublePass?IterationType.DOUBLE_PASS:IterationType.SINGLE_PASS;
		BigInteger aa=a.add(a);
		QuadraticRationalContinuedFraction contFraction1=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(b.negate(),det,aa,iterations);
		QuadraticRationalContinuedFraction contFraction2=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(b,det,aa.negate(),iterations);
		/*
		 * We use Iterables.concat() here to have a single loop, avoiding duplication. It's a bit of a Frankenstein iterator, but thanks to the
		 * "limited" variable set to true, it should finish at some point.
		 */
		Iterable<Convergent> bothFractions=Iterables.concat(contFraction1,contFraction2);
		List<FixedSolution> result=new ArrayList<>();
		for (Convergent conv:bothFractions)	{
			BigInteger x=conv.p;
			BigInteger y=conv.q;
			BigInteger polyval=a.multiply(x.multiply(x)).add(b.multiply(x.multiply(y))).add(c.multiply(y.multiply(y)));
			if (polyval.equals(BigInteger.ONE))	{
				result.add(new FixedSolution(x,y));
				// (-y,-k) is also a solution because all terms are 2nd degree.
				result.add(new FixedSolution(x.negate(),y.negate()));
			}
		}
		return result;
	}
}
