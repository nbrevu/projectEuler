package com.euler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
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

public class Euler261_6 {
	/*
	 * This code attempts to answer an interesting question: can I use the "standard" iteration scheme to solve the "a,b,c" equation in m?
	 * If the answer is no, I need to use Alpertron as well for these equations.
	 */
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		FixedSolution commonBaseSolution=new FixedSolution(BigInteger.ONE,BigInteger.ONE);
		Comparator<FixedSolution> sortByX=(FixedSolution f1,FixedSolution f2)->f1.x.compareTo(f2.x);
		for (long a=1;a<=100;++a)	{
			Set<FixedSolution> manualSolutions=new HashSet<>();
			BigInteger a2=BigInteger.valueOf(a+a);
			BigInteger a2_1=a2.add(BigInteger.ONE);
			BigInteger a2_2=a2.add(BigInteger.TWO);
			DiophantineSolutionRecursion recursion=new DiophantineSolutionRecursion(a2_1,a2_2,a2,a2_1,BigInteger.ZERO,BigInteger.ZERO);
			FixedSolution s=commonBaseSolution;
			for (int i=0;i<10;++i)	{
				manualSolutions.add(s);
				s=recursion.apply(s);
			}
			Set<FixedSolution> exponentialSolutionSet=new HashSet<>();
			{
				List<DiophantineSolution> solutions=Diophantine2dSolver.solve(a,0,-a-1,0,0,1,decomposer);
				if (solutions.size()!=1) throw new RuntimeException("Hipótesis 1 errónea: más de un grupo de soluciones para m="+a+".");
				DiophantineSolution solGroup=solutions.get(0);
				if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Hipótesis 2 errónea: la solución para "+a+" no es recursiva.");
				RecursiveSolution recSol=(RecursiveSolution)solGroup;
				Set<FixedSolution> baseSols=recSol.getBaseSolutions();
				List<DiophantineSolutionRecursion> recursions=recSol.getRecursions();
				Set<FixedSolution> currentGeneration=baseSols;
				for (int i=0;i<3;++i)	{
					Set<FixedSolution> nextGeneration=new HashSet<>();
					for (FixedSolution f:currentGeneration)	{
						if (exponentialSolutionSet.contains(f)) continue;
						exponentialSolutionSet.add(f);
						for (DiophantineSolutionRecursion r:recursions) nextGeneration.add(r.apply(f));
					}
					currentGeneration=nextGeneration;
				}
			}
			// Now let's see if we have missed any important solution.
			for (FixedSolution f:exponentialSolutionSet)	{
				if ((f.x.signum()<0)||(f.y.signum()<0)) continue;
				else if (!manualSolutions.contains(f)) throw new RuntimeException("Noooo 😢. For m="+a+" there is a solution I can't find \"properly\": k="+f.x+", n="+f.y+".");
			}
			FixedSolution[] sortedSolutions=manualSolutions.stream().filter((FixedSolution f)->f.x.compareTo(BigInteger.ONE)>0).sorted(sortByX).limit(2).toArray(FixedSolution[]::new);
			BigInteger[] minKs=new BigInteger[sortedSolutions.length];
			BigInteger bigA=BigInteger.valueOf(a);
			for (int i=0;i<sortedSolutions.length;++i)	{
				FixedSolution f=sortedSolutions[i];
				BigInteger m=f.x.multiply(f.x).multiply(bigA);
				/*-
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
				 */
				BigInteger m_=m.add(BigInteger.ONE);
				BigInteger m_m=m.multiply(m_).negate();
				List<DiophantineSolution> solutionForM=Diophantine2dSolver.solve(m_,BigInteger.ZERO,m.negate(),m_m,m_m,BigInteger.ZERO,decomposer);
				if (solutionForM.size()!=1) throw new RuntimeException("Pues se ve que la he cagado (modo 1).");
				DiophantineSolution solGroup=solutionForM.get(0);
				if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Pues se ve que la he cagado (modo 2).");
				RecursiveSolution recSol=(RecursiveSolution)solGroup;
				Set<FixedSolution> baseSols=recSol.getBaseSolutions();
				FixedSolution[] minimumK=baseSols.stream().filter((FixedSolution sol)->(sol.x.compareTo(BigInteger.ZERO)>0)&&(sol.x.compareTo(sol.y)<=0)).sorted(sortByX).limit(1).toArray(FixedSolution[]::new);
				if (minimumK.length!=1) throw new RuntimeException("Pues se ve que la he cagado (modo 3).");
				minKs[i]=minimumK[0].x;
			}
			System.out.println("a="+a+": "+Arrays.toString(minKs)+".");
		}
	}
}
