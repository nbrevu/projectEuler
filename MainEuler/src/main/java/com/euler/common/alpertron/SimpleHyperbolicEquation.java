package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.euler.common.Primes.PrimeDecomposer;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

/**
 * Special, simple case of the hyperbolic equation where A=C=0, i.e., the equation is Bxy+Ex+Dy+F=0.
 * 
 * There are two cases. Either DE=BF and the equation can be decomposed as (Bx+E)(By+D)=0, so that Bx+E=0 generates a linear solution (if B
 * divides E) and By+D=0 generates another one (if B divides D), or DE!=BF and the equation is a hyperbola but it's nevertheless easy to solve.
 * In the second case we still have (Bx+E)(By+D)=R for some right side value, so we need to factor R and try every solution.
 * For each divisor u of R, we try Bx+E=u and By+D=R/u. R/u is an integer by construction, but x and y might not be. If they are integer, we have
 * found a solution. Every divisor of R must be tried, which is why the decomposer is needed.
 */
public class SimpleHyperbolicEquation implements Diophantine2dEquation<DiophantineSolution> {
	private final BigInteger b;
	private final BigInteger d;
	private final BigInteger e;
	private final BigInteger f;
	private final PrimeDecomposer decomposer;
	public SimpleHyperbolicEquation(BigInteger b,BigInteger d,BigInteger e,BigInteger f,PrimeDecomposer decomposer) {
		this.b=b;
		this.d=d;
		this.e=e;
		this.f=f;
		this.decomposer=decomposer;
	}
	@Override
	public List<DiophantineSolution> solve()	{
		return solveSpecific();
	}
	@Override
	public List<DiophantineSolution> solveSpecific() {
		BigInteger de_bf=d.multiply(e).subtract(b.multiply(f));
		List<DiophantineSolution> result=new ArrayList<>();
		if (de_bf.signum()==0)	{
			BigInteger[] div=e.divideAndRemainder(b);
			if (div[1].signum()==0) result.add(LinearSolution.fixedX(div[0].negate()));
			div=d.divideAndRemainder(b);
			if (div[1].signum()==0) result.add(LinearSolution.fixedY(div[0].negate()));
			return result;
		}
		LongSet divisors=decomposer.getPositiveAndNegativeDivisors(de_bf.abs());
		for (LongCursor cursor=divisors.cursor();cursor.moveNext();)	{
			BigInteger di=BigInteger.valueOf(cursor.elem());
			BigInteger n1=di.subtract(e);
			BigInteger n2=de_bf.divide(di).subtract(d);
			BigInteger[] n1DivB=n1.divideAndRemainder(b);
			BigInteger[] n2DivB=n2.divideAndRemainder(b);
			if ((n1DivB[1].signum()==0)&&(n2DivB[1].signum()==0)) result.add(new FixedSolution(n1DivB[0],n2DivB[0]));
		}
		return result;
	}
}
