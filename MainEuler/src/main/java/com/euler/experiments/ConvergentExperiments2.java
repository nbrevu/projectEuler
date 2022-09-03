package com.euler.experiments;

import java.util.List;

import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.QuadraticRationalContinuedFraction;
import com.euler.common.QuadraticRationalContinuedFraction.IterationType;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.euler.common.alpertron.RecursiveSolution;
import com.google.common.math.IntMath;

public class ConvergentExperiments2 {
	public static void main(String[] args)	{
		QuadraticRationalContinuedFraction mierdas=QuadraticRationalContinuedFraction.getForGenericQuadraticRational(-2428790l,2450l,5882450l,IterationType.SINGLE_PASS);
		long tic=System.nanoTime();
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		long m=49;
		{
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
			if (solutions.size()!=1)	{
				System.out.println("Mierda. Cosa rara inesperada para m="+m+'.');
			}	else if (!(solutions.get(0) instanceof RecursiveSolution))	{
				System.out.println("Mierda. Cosa sehr rara inesperada para m="+m+'.');
			}
			System.out.println(solutions);
		}
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
