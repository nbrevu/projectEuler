package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.euler.common.Primes.PrimeDecomposer;

/**
 * Transformation of the form
 * 	det*x = X+alpha;
 * 	det*y = Y+beta.
 * This transformation will convert an equation of the form Ax^2+Bxy+Cy^2+Dx+Ey+F = 0 into a simpler one, AX^2+BXY+CY^2=F'.
 * Note that A, B and C remain unchanged, but F does not. Note also that F changes place (see the sign at the very end of the rightSide calculation
 * inside applyLegendreTransformIfNecessary).
 */
public class LegendreTransformation	{
	public final BigInteger det;
	public final BigInteger alpha;
	public final BigInteger beta;
	private LegendreTransformation(BigInteger det,BigInteger alpha,BigInteger beta)	{
		this.det=det;
		this.alpha=alpha;
		this.beta=beta;
	}
	private <T extends LegendreTransformableSolution<T>> void revertAll(List<T> baseSolutions,List<? super T> result)	{
		for (T sol:baseSolutions) sol.revertLegendreTransform(this).ifPresent(result::add);
	}
	/*
	 * Main method of this class. This class will provide Legendre transforms to any equation needing them, but note that the constructor is private.
	 */
	public static List<DiophantineSolution> applyLegendreTransformIfNecessary(BigInteger a,BigInteger b,BigInteger c,BigInteger d,BigInteger e,BigInteger f,BigInteger det,PrimeDecomposer decomposer,RecursiveSolutionSource recursiveSolutions)	{
		if ((d.signum()==0)&&(e.signum()==0)) return getHomogeneousEquationSolutions(a,b,c,det,f.negate(),decomposer,null,recursiveSolutions);
		BigInteger alpha=BigInteger.TWO.multiply(c).multiply(d).subtract(b.multiply(e));
		BigInteger beta=BigInteger.TWO.multiply(a).multiply(e).subtract(b.multiply(d));
		BigInteger rightSide=a.multiply(e.multiply(e)).subtract(b.multiply(e).multiply(d)).add(c.multiply(d.multiply(d))).add(f.multiply(det)).multiply(det).negate();
		LegendreTransformation transformation=new LegendreTransformation(det,alpha,beta);
		return getHomogeneousEquationSolutions(a,b,c,det,rightSide,decomposer,transformation,recursiveSolutions);
	}
	/*
	 * This method has all the dispatching logic. There are 4 cases depending on whether F=0 and whether det=B^2-4AC is a perfect square. Note
	 * that at this point we do know for sure that the equation is NOT parabolic and therefore B^2-4AC is not 0.
	 * 
	 * The four cases are:
	 * 	- det is a perfect square and F=0: the equation can be decomposed as the product of two linear equations. Recursive solutions are not needed.
	 * 	- det is a perfect square and F!=0: the left side can be decomposed, but we need to decompose F into factors and try all the cases. Recursive solutions are not needed.
	 * 	- det is not a perfect square and F=0: single solution (0,0). Recursive solutions might be needed.
	 * 	- det is not a perfect square and F!=0: standard case. Convergents are needed (after possibly applying an unimodular transform). Recursive solutions might be needed.
	 * The legendre transform, if present, will always be applied solving each equation. The recursion is only called when needed.
	 * Yes, this mechanism is an awkard way of sidestepping over the fact that the linear solutions provided by the first case can't be treated
	 * by the same code that relies on FixedSolution objects for the recursive solutions.
	 */
	private static List<DiophantineSolution> getHomogeneousEquationSolutions(BigInteger a,BigInteger b,BigInteger c,BigInteger det,BigInteger rightSide,PrimeDecomposer decomposer,LegendreTransformation transform,RecursiveSolutionSource recursiveSolutions)	{
		boolean isTotallyHomogeneous=rightSide.signum()==0;
		Optional<BigInteger> sq=Optional.empty();
		if (det.signum()>0)	{
			BigInteger tmpSq=det.sqrt();
			if (tmpSq.multiply(tmpSq).equals(det)) sq=Optional.of(tmpSq);
		}
		if (sq.isPresent())	{
			BigInteger k=sq.get();
			if (isTotallyHomogeneous)	{
				Diophantine2dEquation<LinearSolution> innerEquation=new HomogeneousEquationWithSquareDet(a,b,k);
				return applyLegendre(innerEquation,transform);
			}	else	{
				Diophantine2dEquation<FixedSolution> innerEquation=new PureSecondGradeEquationWithSquareDet(a,b,rightSide,k,decomposer);
				return applyLegendre(innerEquation,transform);
			}
		}	else	{
			List<FixedSolution> baseSolutions;
			if (isTotallyHomogeneous)	{
				baseSolutions=Collections.singletonList(new FixedSolution(BigInteger.ZERO,BigInteger.ZERO));
				return applyLegendre(baseSolutions,transform);
			}	else	{
				baseSolutions=new StandardHomogeneousEquation(a,b,c,rightSide,det,decomposer).solveSpecific();
				return applyLegendreAndRecursion(baseSolutions,transform,recursiveSolutions);
			}
		}
	}
	private static List<DiophantineSolution> applyLegendreAndRecursion(List<FixedSolution> baseSolutions,LegendreTransformation transform,RecursiveSolutionSource recursiveSolutions)	{
		List<FixedSolution> revertedSolutions;
		if (transform==null) revertedSolutions=baseSolutions;
		else	{
			revertedSolutions=new ArrayList<>();
			transform.revertAll(baseSolutions,revertedSolutions);
		}
		return recursiveSolutions.getSolutions(revertedSolutions);
	}
	private static <T extends LegendreTransformableSolution<T>> List<DiophantineSolution> applyLegendre(Diophantine2dEquation<T> equation,LegendreTransformation transform)	{
		return applyLegendre(equation.solveSpecific(),transform);
	}
	private static <T extends LegendreTransformableSolution<T>> List<DiophantineSolution> applyLegendre(List<T> baseSolutions,LegendreTransformation transform)	{
		List<DiophantineSolution> result=new ArrayList<>();
		if (transform==null) result.addAll(baseSolutions);
		else transform.revertAll(baseSolutions,result);
		return result;
	}
}
