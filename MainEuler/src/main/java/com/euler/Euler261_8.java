package com.euler;

import java.math.BigInteger;
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
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Euler261_8 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private final static int PRIME_LIMIT=IntMath.pow(10,8);
	
	private static class DiophantineInterface	{
		private final BigInteger bigLimit;
		private final PrimeDecomposer decomposer;
		public DiophantineInterface(long limit,int primeLimit)	{
			bigLimit=BigInteger.valueOf(limit);
			decomposer=new StandardPrimeDecomposer(primeLimit);
		}
		public boolean addSolutions(long m,LongSet knownKs)	{
			long m_=m+1;
			long m_m=-m*m_;
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,0,-m,m_m,m_m,0,decomposer);
			if (solutions.size()!=1) throw new RuntimeException("Hipótesis 1 errónea: más de un grupo de soluciones para m="+m+".");
			DiophantineSolution solGroup=solutions.get(0);
			if (!(solGroup instanceof RecursiveSolution)) throw new RuntimeException("Hipótesis 2 errónea: la solución para "+m+" no es recursiva.");
			RecursiveSolution recSol=(RecursiveSolution)solGroup;
			Set<FixedSolution> baseSols=recSol.getBaseSolutions();
			Set<FixedSolution> manualSolutions=new HashSet<>();
			List<DiophantineSolutionRecursion> recursions=recSol.getRecursions();
			// Checked: the first element is always the "base recursion" we want ✅.
			DiophantineSolutionRecursion baseRecursion=recursions.get(0);
			// First we create the manual solution set.
			for (FixedSolution f:baseSols)	{
				FixedSolution mySol=f;
				if ((mySol.x.signum()<0)||(mySol.y.signum()<0)) continue;
				for (;;)	{
					if (mySol.x.compareTo(bigLimit)>0) break;
					if (!manualSolutions.add(mySol)) break;	// We have already found this solution, therefore we have also found its children.
					mySol=baseRecursion.apply(mySol);
				}
			}
			if (manualSolutions.isEmpty()) return false;
			boolean any=false;
			for (FixedSolution sol:manualSolutions) if (sol.x.compareTo(sol.y)<=0)	{
				knownKs.add(sol.x.longValueExact());
				any=true;
			}
			return any;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DiophantineInterface solver=new DiophantineInterface(LIMIT,PRIME_LIMIT);
		LongSet passedMs=HashLongSets.newMutableSet();
		LongSet knownKs=HashLongSets.newMutableSet();
		// First pass: m=o^2-1 where o is odd. So o=2x+1, m=(2x+1)^2-1=4x*(x+1).
		for (long x=1;;++x)	{
			long m=4*x*(x+1);
			System.out.println("x="+x+" -> m="+m+"...");
			passedMs.add(m);
			if (!solver.addSolutions(m,knownKs)) break;
		}
		// Second pass: m is such that m=a*b^2 and m+1=(a+1)*c^2 for some integers a, b and c. We iterate over a.
		boolean canContinue=true;
		for (long a=1;;++a)	{
			System.out.println("a="+a+"...");
			long b=1;
			long c=1;
			long _2a=2*a;
			long _2a1=_2a+1;
			long _2a2=_2a+2;
			canContinue=false;
			for(;;)	{
				long nextB=_2a1*b+_2a2*c;
				long nextC=_2a*b+_2a1*c;
				long m=a*nextB*nextB;
				System.out.println("\tm="+m+"...");
				if (!passedMs.add(m)) continue;
				if (!solver.addSolutions(m,knownKs)) break;
				else canContinue=true;
				b=nextB;
				c=nextC;
			}
			if (!canContinue) break;
		}
		// Third pass: standard solutions.
		for (long m=1;;++m)	{
			System.out.println("m="+m+"...");
			if (passedMs.contains(m)) continue;
			long k=m;
			long n=0;
			long _2m=2*m;
			long _2m1=_2m+1;
			long _2m2=_2m+2;
			canContinue=false;
			for (;;)	{
				long nextK=_2m1*k+_2m*n+m;
				long nextN=_2m2*k+_2m1*n;
				if (nextK>LIMIT) break;
				canContinue=true;
				k=nextK;
				n=nextN;
				if (k<=n) knownKs.add(k);
			}
			if (!canContinue) break;
		}
		long result=0;
		for (LongCursor cursor=knownKs.cursor();cursor.moveNext();) result+=cursor.elem();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
