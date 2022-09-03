package com.euler.common.alpertron;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.Lists;

public class RecursiveSolutionGenerator implements RecursiveSolutionSource {
	private final BigInteger a;
	private final BigInteger b;
	private final BigInteger c;
	private final BigInteger d;
	private final BigInteger e;
	private final BigInteger det;
	public RecursiveSolutionGenerator(BigInteger a,BigInteger b,BigInteger c,BigInteger d,BigInteger e,BigInteger det) {
		this.a=a;
		this.b=b;
		this.c=c;
		this.d=d;
		this.e=e;
		this.det=det;
	}
	@Override
	public List<DiophantineSolution> getSolutions(List<FixedSolution> baseSolutions) {
		/*
		 * This one might be the hardest method in the entire library. At least it's kind of the weirdest.
		 */
		List<DiophantineSolutionRecursion> recursions=new ArrayList<>();
		List<FixedSolution> characteristicSolutions=ConvergentSolutions.getSolutions(BigInteger.ONE,b,a.multiply(c),det,true);
		if (characteristicSolutions.isEmpty()) return Lists.transform(baseSolutions,DiophantineSolution.class::cast);
		BigInteger minusDet=det.negate();
		for (FixedSolution sol:characteristicSolutions)	{
			BigInteger r=sol.x;
			BigInteger s=sol.y;
			BigInteger P=r;
			BigInteger Q=c.multiply(s).negate();
			BigInteger R=a.multiply(s);
			BigInteger S=r.add(b.multiply(s));
			BigInteger commonFactorA=P.add(S).subtract(BigInteger.TWO);
			BigInteger commonFactorB=b.subtract(b.multiply(r)).subtract(BigInteger.TWO.multiply(a).multiply(c).multiply(s));
			BigInteger numK=c.multiply(d).multiply(commonFactorA).add(e.multiply(commonFactorB));
			BigInteger[] divK=numK.divideAndRemainder(minusDet);
			if (divK[1].signum()!=0) continue;
			BigInteger numL=d.multiply(commonFactorB).add(a.multiply(e).multiply(commonFactorA));
			BigInteger[] divL=numL.divideAndRemainder(minusDet);
			if (divL[1].signum()!=0) continue;
			recursions.add(new DiophantineSolutionRecursion(P,Q,R,S,divK[0],divL[0].add(d.multiply(s))));
		}
		if (recursions.isEmpty())	{
			// I don't know what I'm doing. This is almost surely wrong. Kill me and end my suffering.
			FixedSolution someSol=characteristicSolutions.get(0);
			BigInteger r=someSol.x;
			BigInteger s=someSol.y;
			BigInteger r2=r.multiply(r);
			BigInteger s2=s.multiply(s);
			BigInteger bs=b.multiply(s);
			BigInteger rbs=r.add(bs);
			BigInteger rrbs=r.add(rbs);
			BigInteger P=r2.subtract(a.multiply(c).multiply(s2));
			BigInteger Q=c.multiply(s).multiply(rrbs).negate();
			BigInteger R=a.multiply(s).multiply(rrbs);
			BigInteger S=r2.add(bs.multiply(r).multiply(BigInteger.TWO)).add(det.multiply(s2));
			BigInteger K=c.multiply(d).multiply(s2).add(e.multiply(r).multiply(s)).negate();
			BigInteger L=d.multiply(s).multiply(rbs).subtract(a.multiply(e).multiply(s2));
			recursions.add(new DiophantineSolutionRecursion(P,Q,R,S,K,L));
		}
		return Arrays.asList(new RecursiveSolution(new HashSet<>(baseSolutions),recursions));
	}
}
