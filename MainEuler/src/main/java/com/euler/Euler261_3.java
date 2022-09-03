package com.euler;

import java.util.List;

import com.euler.common.DivisorHolder;
import com.euler.common.Primes.PrimeDecomposer;
import com.euler.common.Primes.StandardPrimeDecomposer;
import com.euler.common.alpertron.Diophantine2dSolver;
import com.euler.common.alpertron.DiophantineSolution;
import com.euler.common.alpertron.RecursiveSolution;
import com.google.common.math.IntMath;
import com.koloboke.collect.IntCursor;

public class Euler261_3 {
	private static boolean hasSquare(long n,PrimeDecomposer decomposer)	{
		DivisorHolder decomp=decomposer.decompose(n);
		for (IntCursor c=decomp.getFactorMap().values().cursor();c.moveNext();) if (c.elem()>=2) return true;
		return false;
	}
	
	public static void main(String[] args)	{
		PrimeDecomposer decomposer=new StandardPrimeDecomposer(IntMath.pow(10,8));
		long tic=System.nanoTime();
		for (long m=2;m<=1000;++m)	{
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
			if (solutions.size()!=1) throw new RuntimeException("Hipótesis 1 errónea: más de un grupo de soluciones para m="+m+".");
			DiophantineSolution solGroup=solutions.get(0);
			if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Hipótesis 2 errónea: la solución para "+m+" no es recursiva.");
			RecursiveSolution recSol=(RecursiveSolution)solGroup;
			int s=recSol.getBaseSolutions().size();
			int r=recSol.getRecursions().size();
			if (r!=10) System.out.println("Caso interesante: para m="+m+" me encuentro "+r+" recursiones, en vez de 10.");
			if (hasSquare(m,decomposer)&&hasSquare(m+1,decomposer)) System.out.println("Logueando caso que me interesa (m="+m+"): "+s+" soluciones base.");
			else if (s!=6) System.out.println("Caso interesante: "+m+" no está en el grupo guay, pero me salen "+s+" soluciones base en vez de 6.");
			if (s!=6) System.out.println("Caso interesante: m="+m+", s="+s+".");
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
