package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.euler.common.Convergents.Convergent;
import com.euler.common.DiophantineUtils;
import com.euler.common.EulerUtils;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.SquareFactorsIterator;

/**
 * Equation that can be readily solved using convergents, because it already verifies A!=0, gcd(A,rightSide)=1.
 */
public abstract class ConvergentSolvableEquation implements Diophantine2dEquation<FixedSolution> {
	public final static List<Convergent> BASE_SOLUTION=Collections.singletonList(new Convergent(BigInteger.ONE,BigInteger.ZERO));
	private final BigInteger a;
	private final BigInteger b;
	private final BigInteger c;
	private final BigInteger rightSide;
	private final PrimeDecomposer decomposer;
	protected final BigInteger det;
	public ConvergentSolvableEquation(BigInteger a,BigInteger b,BigInteger c,BigInteger rightSide,PrimeDecomposer decomposer,BigInteger det)	{
		this.a=a;
		this.b=b;
		this.c=c;
		this.rightSide=rightSide;
		this.decomposer=decomposer;
		this.det=det;
	}
	@Override
	public List<FixedSolution> solveSpecific() {
		SquareFactorsIterator squareFactorsDecomposer=new SquareFactorsIterator(rightSide,decomposer.decompose(rightSide.abs()));
		List<FixedSolution> result=new ArrayList<>();
		while (squareFactorsDecomposer.hasNext())	{
			Set<BigInteger> characteristicSolutions=DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber(a,b,c,squareFactorsDecomposer.getQuotientDecomposition());
			if (characteristicSolutions.isEmpty()) continue;
			BigInteger rescaler=BigInteger.valueOf(squareFactorsDecomposer.getRoot());
			BigInteger modulus=squareFactorsDecomposer.getQuotient();
			for (BigInteger n:characteristicSolutions)	{
				List<FixedSolution> baseSolutions=solveUsingConvergents(n,modulus);
				for (FixedSolution sol:baseSolutions) result.add(rescale(sol,rescaler));
			}
		}
		return result;
	}
	private FixedSolution rescale(FixedSolution sol,BigInteger rescaler)	{
		return new FixedSolution(sol.x.multiply(rescaler),sol.y.multiply(rescaler));
	}
	private FixedSolution getSolution(BigInteger y,BigInteger k,BigInteger n,BigInteger modulus)	{
		BigInteger x=n.multiply(y).subtract(modulus.multiply(k));
		return new FixedSolution(x,y);
	}
	private List<FixedSolution> solveUsingConvergents(BigInteger n,BigInteger modulus)	{
		/*
		 * We transform the equation in (x,y) given by Ax^2+Bxy+Cy^2=R into a different one.
		 * The transformation is x=n*y-modulus*k (note that "modulus" might be negative).
		 * The resulting equation has unknowns y and k, and it's Py^2 + Qyk + Rk^2 = 1, where
		 * 	P=(a*n^2+b*n+c)/modulus,
		 * 	Q=-(2*a*n+b),
		 * 	R=a*modulus.
		 * Its discriminant is det, no need to recalculate it.
		 * Note that what I call n is what Alpertron calls T, and what I call modulus is what Alpertron calls n.
		 * Mind your ns!
		 * 
		 * ACHTUNG! What's the sign of q? This looks wrong.
		 */
		BigInteger p=a.multiply(n).add(b).multiply(n).add(c).divide(modulus);
		BigInteger q=BigInteger.TWO.multiply(a).multiply(n).add(b).negate();
		BigInteger r=a.multiply(modulus);
		BigInteger g=EulerUtils.gcd(p,EulerUtils.gcd(q,r));
		if (!g.equals(BigInteger.ONE)) return Collections.emptyList();
		Iterable<Convergent> convergentIterator=getConvergentForEquation(p,q,r);
		List<FixedSolution> result=new ArrayList<>();
		for (Convergent conv:convergentIterator)	{
			BigInteger y=conv.p;
			BigInteger k=conv.q;
			BigInteger polyval=p.multiply(y.multiply(y)).add(q.multiply(y.multiply(k))).add(r.multiply(k.multiply(k)));
			if (polyval.equals(BigInteger.ONE))	{
				result.add(getSolution(y,k,n,modulus));
				// (-y,-k) is also a solution because all terms are 2nd degree.
				result.add(getSolution(y.negate(),k.negate(),n,modulus));
			}
		}
		return result;
	}
	protected abstract Iterable<Convergent> getConvergentForEquation(BigInteger p,BigInteger q,BigInteger r);
}
