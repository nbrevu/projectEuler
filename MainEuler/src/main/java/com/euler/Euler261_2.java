package com.euler;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.euler.common.alpertron.DiophantineSolutionRecursion;
import com.euler.common.alpertron.FixedSolution;
import com.euler.common.alpertron.RecursiveSolution;
import com.google.common.math.IntMath;

public class Euler261_2 {
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		long tic=System.nanoTime();
		long m=24;
		long m_=m+1;
		long m_m=-m*m_;
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
		Set<FixedSolution> sols=((RecursiveSolution)solutions.get(0)).getBaseSolutions();
		// System.out.println("m="+m+": "+sols+".");
		BigInteger a=BigInteger.valueOf(m_);
		BigInteger c=BigInteger.valueOf(-m);
		BigInteger d=BigInteger.valueOf(m_m);
		BigInteger e=d;
		System.out.println("Soluciones: "+sols.size()+".");
		for (FixedSolution s:sols)	{
			System.out.println("k="+s.x+", n="+s.y+".");
			BigInteger remX=a.multiply(s.x).add(d).multiply(s.x);
			BigInteger remY=c.multiply(s.y).add(e).multiply(s.y);
			BigInteger rem=remX.add(remY);
			/*
			 * Vale, esto no pasa. GUAY. PEEEEEEEEEERO me salen soluciones de más y querría al menos depurar para ver de dónde salen.
			 * De todos modos es fácil ver el patrón: 6 soluciones (en vez de 2, como antes) = caso estándar; más de 6 = caso raro.
			 */
			if (rem.signum()!=0) throw new IllegalStateException("La has cagado pero bien.");
		}
		List<DiophantineSolutionRecursion> recursions=((RecursiveSolution)solutions.get(0)).getRecursions();
		System.out.println("Recursiones: "+recursions.size()+".");
		int index=0;
		for (DiophantineSolutionRecursion r:recursions)	{
			++index;
			System.out.println("R"+index+":");
			StringBuilder firstLine=new StringBuilder();
			firstLine.append("x_(n+1)=");
			firstLine.append(r.p);
			firstLine.append("x_n");
			if (r.q.signum()>=0) firstLine.append("+");
			firstLine.append(r.q);
			firstLine.append("y_n");
			if (r.k.signum()!=0)	{
				if (r.k.signum()>0) firstLine.append("+");
				firstLine.append(r.k);
			}
			StringBuilder secondLine=new StringBuilder();
			secondLine.append("y_(n+1)=");
			secondLine.append(r.r);
			secondLine.append("x_n");
			if (r.s.signum()>=0) secondLine.append("+");
			secondLine.append(r.s);
			secondLine.append("y_n");
			if (r.l.signum()!=0)	{
				if (r.l.signum()>0) secondLine.append("+");
				secondLine.append(r.l);
			}
			System.out.println("\t"+firstLine);
			System.out.println("\t"+secondLine);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
