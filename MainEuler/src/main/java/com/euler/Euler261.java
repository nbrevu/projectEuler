package com.euler;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.euler.common.alpertron.FixedSolution;
import com.euler.common.alpertron.RecursiveSolution;
import com.google.common.math.IntMath;

public class Euler261 {
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		long tic=System.nanoTime();
		for (long m=1;m<100000;++m)	{
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
			if (solutions.size()!=1)	{
				System.out.println("Mierda. Cosa rara inesperada para m="+m+'.');
				continue;
			}	else if (!(solutions.get(0) instanceof RecursiveSolution))	{
				System.out.println("Mierda. Cosa sehr rara inesperada para m="+m+'.');
				continue;
			}
			Set<FixedSolution> sols=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
			// System.out.println("m="+m+": "+sols+".");
			BigInteger a=BigInteger.valueOf(m_);
			BigInteger c=BigInteger.valueOf(-m);
			BigInteger d=BigInteger.valueOf(m_m);
			BigInteger e=d;
			for (FixedSolution f:sols)	{
				BigInteger remX=a.multiply(f.x).add(d).multiply(f.x);
				BigInteger remY=c.multiply(f.y).add(e).multiply(f.y);
				BigInteger rem=remX.add(remY);
				/*
				 * Vale, esto no pasa. GUAY. PEEEEEEEEEERO me salen soluciones de más y querría al menos depurar para ver de dónde salen.
				 * De todos modos es fácil ver el patrón: 6 soluciones (en vez de 2, como antes) = caso estándar; más de 6 = caso raro.
				 */
				if (rem.signum()!=0) throw new IllegalStateException("La has cagado pero bien.");
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
