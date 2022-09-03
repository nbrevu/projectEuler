package com.euler;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.euler.common.alpertron.FixedSolution;
import com.euler.common.alpertron.RecursiveSolution;
import com.google.common.math.IntMath;

public class Euler261_7 {
	// Yet another successful check.
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		Comparator<FixedSolution> sortByX=(FixedSolution f1,FixedSolution f2)->f1.x.compareTo(f2.x);
		for (long x=2;x<=100;++x)	{
			long odd=2*x-1;
			long m=odd*odd-1;
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutionForM=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
			if (solutionForM.size()!=1) throw new RuntimeException("Pues se ve que la he cagado (modo 1).");
			DiophantineSolution solGroup=solutionForM.get(0);
			if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Pues se ve que la he cagado (modo 2).");
			RecursiveSolution recSol=(RecursiveSolution)solGroup;
			Set<FixedSolution> baseSols=recSol.getBaseSolutions();
			FixedSolution[] minimumK=baseSols.stream().filter((FixedSolution sol)->(sol.x.compareTo(BigInteger.ZERO)>0)&&(sol.x.compareTo(sol.y)<=0)).sorted(sortByX).limit(1).toArray(FixedSolution[]::new);
			if (minimumK.length!=1) throw new RuntimeException("Pues se ve que la he cagado (modo 3).");
			System.out.println("x="+x+", m="+m+", minimum K="+minimumK[0].x);
		}
	}
}
