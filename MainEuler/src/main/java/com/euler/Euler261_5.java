package com.euler;

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

public class Euler261_5 {
	/*
	 * This code has a very favourable consequence: the "restricted" iteration scheme is good enough. Even more, I can hijack the first element
	 * of the "Recursions" array and just use it, no need to iterate manually. 
	 */
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		for (long m=1;m<=10000;++m)	{
			//long m=4374;
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
			if (solutions.size()!=1) throw new RuntimeException("Hipótesis 1 errónea: más de un grupo de soluciones para m="+m+".");
			DiophantineSolution solGroup=solutions.get(0);
			if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Hipótesis 2 errónea: la solución para "+m+" no es recursiva.");
			RecursiveSolution recSol=(RecursiveSolution)solGroup;
			Set<FixedSolution> baseSols=recSol.getBaseSolutions();
			Set<FixedSolution> manualSolutions=new HashSet<>();
			Set<FixedSolution> exponentialSolutionSet=new HashSet<>();
			List<DiophantineSolutionRecursion> recursions=recSol.getRecursions();
			// Checked: the first element is always the "base recursion" we want ✅.
			DiophantineSolutionRecursion baseRecursion=recursions.get(0);
			// First we create the manual solution set.
			for (FixedSolution f:baseSols)	{
				FixedSolution mySol=f;
				if ((mySol.x.signum()<0)||(mySol.y.signum()<0)) continue;
				for (int i=0;i<5;++i)	{
					manualSolutions.add(mySol);
					mySol=baseRecursion.apply(mySol);
					if (baseSols.contains(mySol)) break;
				}
			}
			// Then we create the "deep" set of solutions.
			{
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
			int x=0;
			for (FixedSolution f:exponentialSolutionSet)	{
				if ((f.x.signum()<0)||(f.y.signum()<0)) continue;
				else if (!manualSolutions.contains(f)) throw new RuntimeException("Noooo 😢. For m="+m+" there is a solution I can't find \"properly\": k="+f.x+", n="+f.y+".");
				else ++x;
			}
			if (x>0) System.out.println("He escarbado para m="+m+" y he encontrado "+x+" soluciones, pero todas ellas se podían encontrar normalmente.");
			else throw new RuntimeException("Pero ¿qué ha pasado? Was passiert?");
		}
	}
}
