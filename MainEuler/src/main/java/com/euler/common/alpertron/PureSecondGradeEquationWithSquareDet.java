package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.Primes.PrimeDecomposer;
import com.koloboke.collect.set.LongSet;

/**
 * Equations of the form Ax^2+Bxy+Cy^2=rightSide, where B^2-4AC is not 0 (therefore the equation is not parabolic), but it's a perfect square.
 * In these cases the solution comes from a prime decomposition of the independent factor.
 */
public class PureSecondGradeEquationWithSquareDet implements Diophantine2dEquation<FixedSolution> {
	private final BigInteger a;
	private final BigInteger b;
	/*
	 * ACHTUNG, alpertron uses a different sign than me here. My "AF4" is Alpertron's -A*F*4 because "rightSide" is -F, not F.
	 */
	private final BigInteger af4;
	private final BigInteger k;
	private final Set<BigInteger> af4Divisors;
	public PureSecondGradeEquationWithSquareDet(BigInteger a,BigInteger b,BigInteger rightSide,BigInteger k,PrimeDecomposer decomposer) {
		// a,b,rightSide,k,decomposer
		this.a=a;
		this.b=b;
		af4=Diophantine2dEquation.FOUR.multiply(a).multiply(rightSide);
		this.k=k;
		LongSet longDivisors=decomposer.getPositiveAndNegativeDivisors(af4.abs());
		af4Divisors=new HashSet<>();
		for (long div:longDivisors) af4Divisors.add(BigInteger.valueOf(div));
	}
	/*
	 * Let k=sqrt(B^2-4AC). The equation can be reduced to
	 * 	(2Ax+(B+k)y) * (2Ax+(B-k)y) = 4A*R (where R=-F is the rightSide variable).
	 * So, let u be any divisor of 4AR. We try to solve simultaneously these two equations:
	 * 	2Ax+(B+k)y = u.
	 * 	2Ax+(B-k)y = 4AR/u.
	 * Subtracting we get
	 * 	2ky=(u-4AR/u),
	 * Therefore
	 * 	y=(u-4AR/u)/(2k).
	 * 4AR/u is guaranteed to be integer by construction, but we need to ensure that the quotient for 2k is integer, otherwise this solution is invalid.
	 * Then can we clear x in the first equation as
	 * 	x=(u-(B+k)y)/(2A).
	 * Again we must be sure that the quotient is integer. If it is, the (x,y) pair is a valid solution.
	 * We must iterate over every single divisor of 4AR looking for valid values of u.
	 */
	@Override
	public List<FixedSolution> solveSpecific() {
		List<FixedSolution> result=new ArrayList<>();
		BigInteger bk=b.add(k);
		BigInteger kk=k.add(k);
		BigInteger aa=a.add(a);
		for (BigInteger u:af4Divisors)	{
			BigInteger numY=u.subtract(af4.divide(u));
			BigInteger[] divY=numY.divideAndRemainder(kk);
			if (divY[1].signum()!=0) continue;
			BigInteger y=divY[0];
			BigInteger numX=u.subtract(bk.multiply(y));
			BigInteger[] divX=numX.divideAndRemainder(aa);
			if (divX[1].signum()!=0) continue;
			result.add(new FixedSolution(divX[0],y));
		}
		return result;
	}
}
