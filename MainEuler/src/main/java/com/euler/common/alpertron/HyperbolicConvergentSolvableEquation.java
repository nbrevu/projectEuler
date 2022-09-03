package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.Iterator;

import com.euler.common.Convergents.Convergent;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.QuadraticRationalContinuedFraction;
import com.euler.common.QuadraticRationalContinuedFraction.IterationType;
import com.google.common.collect.Iterables;

public class HyperbolicConvergentSolvableEquation extends ConvergentSolvableEquation	{
	public HyperbolicConvergentSolvableEquation(BigInteger a,BigInteger b,BigInteger c,BigInteger rightSide,PrimeDecomposer decomposer,BigInteger det) {
		super(a,b,c,rightSide,decomposer,det);
	}
	
	private static Iterable<Convergent> reverse(Iterable<Convergent> base)	{
		return ()->new Iterator<>()	{
			private Iterator<Convergent> original=base.iterator();
			@Override
			public boolean hasNext() {
				return original.hasNext();
			}
			@Override
			public Convergent next() {
				Convergent origConv=original.next();
				return new Convergent(origConv.q,origConv.p);
			}
		};
	}

	@Override
	protected Iterable<Convergent> getConvergentForEquation(BigInteger p,BigInteger q,BigInteger r) {
		/*
		 * We use Iterables.concat() here to have a single loop, avoiding duplication. It's a bit of a Frankenstein iterator, but thanks to the
		 * "SINGLE_PASS" iteration method, it should finish eventually.
		 */
		/*-
		BigInteger pp=p.add(p);
		QuadraticRationalContinuedFraction contFraction1=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(q.negate(),det,pp,IterationType.SINGLE_PASS);
		QuadraticRationalContinuedFraction contFraction2=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(q,det,pp.negate(),IterationType.SINGLE_PASS);
		// Maybe BASE_SOLUTION can be removed?
		return Iterables.concat(BASE_SOLUTION,contFraction1,contFraction2);
		*/
		/*-
		BigInteger discr=(det.testBit(0))?det:det.divide(BigInteger.valueOf(4));
		BigInteger rr=r.add(r);
		QuadraticRationalContinuedFraction contFraction2=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(q,discr,rr.negate(),IterationType.SINGLE_PASS);
		QuadraticRationalContinuedFraction contFraction1=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(q.negate(),discr,rr,IterationType.SINGLE_PASS);
		*/
		BigInteger detX;
		if (!det.testBit(0))	{
			q=q.divide(BigInteger.TWO);
			detX=det.divide(Diophantine2dEquation.FOUR);
		}	else	{
			detX=det;
			r=r.add(r);
		}
		QuadraticRationalContinuedFraction contFraction2=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(q,detX,r.negate(),IterationType.SINGLE_PASS);
		QuadraticRationalContinuedFraction contFraction1=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(q.negate(),detX,r,IterationType.SINGLE_PASS);
		// Maybe BASE_SOLUTION can be removed?
		return Iterables.concat(BASE_SOLUTION,reverse(contFraction1),reverse(contFraction2));
	}

}
