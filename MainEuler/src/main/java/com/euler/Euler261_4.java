package com.euler;

import java.util.List;
import java.util.Set;

import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.euler.common.alpertron.FixedSolution;
import com.euler.common.alpertron.RecursiveSolution;
import com.google.common.math.IntMath;

public class Euler261_4 {
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		/*
		 * Caso particular: buscamos soluciones para m=44 y/o otras pruebas.
		 */
		long m=1;
		long m_=m+1;
		long m_m=-m*m_;
		List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
		if (solutions.size()!=1) throw new RuntimeException("Hipótesis 1 errónea: más de un grupo de soluciones para m="+m+".");
		DiophantineSolution solGroup=solutions.get(0);
		if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Hipótesis 2 errónea: la solución para "+m+" no es recursiva.");
		RecursiveSolution recSol=(RecursiveSolution)solGroup;
		Set<FixedSolution> s=recSol.getBaseSolutions();
		for (FixedSolution f:s)	if ((f.x.signum()>=0)&&(f.y.signum()>=0)) System.out.println("Solución de ALPERTRON: k="+f.x+", n="+f.y+".");
		for (long k=0;k<=m*m;++k) for (long n=0;n<=m*m;++n)	{
			long l=m_*k*k-m*n*n+m_m*(k+n);
			if (l==0) System.out.println("Solución a manubrio: k="+k+", n="+n+".");
		}
	}
}
