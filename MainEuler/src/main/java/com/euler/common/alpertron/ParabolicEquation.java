package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.euler.common.DiophantineUtils;
import com.euler.common.Primes.PrimeDecomposer;
import com.koloboke.collect.map.ObjIntMap;

/**
 * Equation of the form Ax^2+Bxy+Cy^2+Dx+Ey+F=0 where B^2-4AC=0. Note that C is absent because we know it's equal to B^2/A.
 */
public class ParabolicEquation implements Diophantine2dEquation<DiophantineSolution> {
	private final BigInteger a;
	private final BigInteger b;
	private final BigInteger d;
	private final BigInteger e;
	private final BigInteger f;
	private final PrimeDecomposer decomposer;
	public ParabolicEquation(BigInteger a,BigInteger b,BigInteger d,BigInteger e,BigInteger f,PrimeDecomposer decomposer)	{
		this.a=a;
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
	/*
	 * First we multiply times 4A. Since 4Ax^2+4ABxy+4ACy^2 = 4Ax^2+4ABxy+By^2 = (2Ax+By)^2, the equation is now
	 *  (2Ax+By)^2+4ADx+4AEy+4AF=0.
	 * We then do
	 * 	t=(2Ax+By),
	 * so that the equation is transformed into
	 * 	(t+D)^2 = alpha*y + beta,
	 * where
	 * 	alpha=2(BD-2AE),
	 * 	beta=D^2-4AF.
	 * We then solve the equation
	 * 	T^2==beta (mod |alpha|),
	 * where T=t+D, using the standard Tonelli-Shanks + Hensel lifting + Chinese Remainder Theorem method.
	 * This can be accomplished using the DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber method.
	 * After solving this equation we have several linear solutions of the form T=n+K*alpha where K is any integer and 0<=n<|alpha|.
	 * This gives values of the form (t+D)^2=alpha*y+beta, so that y can be cleared from (t+D)^2=alpha*y+beta.
	 * Then we clear x from the t=2Ax+By case. While y is guaranteed to be an integer, x is not, so we need to ensure that the solutions fit.
	 * There is a special, simpler case when alpha=0.
	 */
	@Override
	public List<DiophantineSolution> solveSpecific() {
		BigInteger alpha=BigInteger.TWO.multiply(b.multiply(d).subtract(BigInteger.TWO.multiply(a).multiply(e)));
		BigInteger beta=d.multiply(d).subtract(FOUR.multiply(a).multiply(f));
		BigInteger aa=a.add(a);
		if (alpha.signum()==0)	{
			// Simplified case. There are solutions only if beta is a perfect square.
			if (beta.signum()==0)	{
				// Special case.
				return new LinearEquation(aa,b,d).solve();
			}	else if (beta.signum()<0) return Collections.emptyList();
			BigInteger sq=beta.sqrt();
			if (!sq.multiply(sq).equals(beta)) return Collections.emptyList();
			// There might be solutions.
			List<DiophantineSolution> result=new LinearEquation(aa,b,d.add(sq)).solve();
			result.addAll(new LinearEquation(aa,b,d.subtract(sq)).solveSpecific());
			return result;
		}
		// alpha!=0. Time to actually solve this.
		ObjIntMap<BigInteger> alphaDecomp=Diophantine2dEquation.decompose(alpha.abs(),decomposer);
		Set<BigInteger> baseSolutions=DiophantineUtils.quadraticPolynomialRootsModuloAnyNumber(BigInteger.ONE,BigInteger.ZERO,beta.negate(),alphaDecomp);
		if (baseSolutions.isEmpty()) return Collections.emptyList();
		List<DiophantineSolution> result=new ArrayList<>(baseSolutions.size());
		for (BigInteger n:baseSolutions)	{
			BigInteger y0=n.multiply(n).subtract(beta).divide(alpha);
			BigInteger quadX=alpha.multiply(b).negate();
			BigInteger linX=BigInteger.TWO.multiply(n).multiply(b).negate().add(alpha);
			BigInteger x0=y0.multiply(b).negate().add(n).subtract(d);
			BigInteger[] divLin=linX.divideAndRemainder(aa);
			BigInteger[] div0=x0.divideAndRemainder(aa);
			if ((divLin[1].signum()==0)&&(div0[1].signum()==0))	{
				quadX=quadX.divide(aa);	// Always a multiple, no need to verify.
				linX=divLin[0];
				x0=div0[0];
				// quadY=alpha, linY=2n.
				result.add(new QuadraticSolution(x0,linX,quadX,y0,n.add(n),alpha));
			}
		}
		return result;
	}
}
