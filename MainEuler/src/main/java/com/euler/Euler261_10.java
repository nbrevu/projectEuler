package com.euler;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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

public class Euler261_10 {
	private final static long LIMIT=LongMath.pow(10l,10);
	
	private final static int PRIME_LIMIT=IntMath.pow(10,8);
	
	private static class DiophantineInterface	{
		private final long limit;
		private final BigInteger bigLimit;
		private final PrimeDecomposer decomposer;
		public DiophantineInterface(long limit,int primeLimit)	{
			this.limit=limit;
			bigLimit=BigInteger.valueOf(limit);
			decomposer=new StandardPrimeDecomposer(primeLimit);
		}
		private boolean iterateSolution(long baseK,long baseN,long m,LongSet knownKs)	{
			long _2m=2*m;
			long _2m1=_2m+1;
			long _2m2=_2m+2;
			boolean any=false;
			long k=baseK;
			long n=baseN;
			for (;;)	{
				long nextK=_2m1*k+_2m*n+m;
				long nextN=_2m2*k+_2m1*n;
				if (nextK>limit) return any;
				k=nextK;
				n=nextN;
				any=true;
				if (k<=n) knownKs.add(k);
			}
		}
		public boolean getStandardSolutions(long m,LongSet knownKs)	{
			return iterateSolution(m,0,m,knownKs);
		}
		public boolean getSpecialSolutions(long i,LongSet passedMs,LongSet knownKs)	{
			long m=4*i*(i+1);
			/*
			 * If add() returns false, we have already done calculations for this m, so we don't need to do the calculations; but we can still
			 * iterate, so we return true.
			 */
			if (!passedMs.add(m)) return true;
			long k=(m-i)*(2*i+2);
			long n=(m-i)*(2*i+1);
			boolean specialCase=iterateSolution(k,n,m,knownKs);
			boolean normalCase=getStandardSolutions(m,knownKs);
			return specialCase||normalCase;
		}
		public boolean getSolutionsWithAlpertron(long m,LongSet knownKs)	{
			return getSolutionsWithAlpertron(BigInteger.valueOf(m),knownKs);
		}
		public boolean getSolutionsWithAlpertron(BigInteger m,LongSet knownKs)	{
			BigInteger m_=m.add(BigInteger.ONE);
			BigInteger m_m=m.multiply(m_).negate();
			List<DiophantineSolution> solutions=Diophantine2dSolver.solve(m_,BigInteger.ZERO,m.negate(),m_m,m_m,BigInteger.ZERO,decomposer);
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
			for (FixedSolution sol:manualSolutions) if ((sol.x.signum()>0)&&(sol.x.compareTo(sol.y)<=0))	{
				knownKs.add(sol.x.longValueExact());
				System.out.println("\t\tK="+sol.x+"!!!!!");
				any=true;
			}
			return any;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DiophantineInterface solver=new DiophantineInterface(LIMIT,PRIME_LIMIT);
		SortedMap<Long,SortedSet<Long>> groupedPassedIs=new TreeMap<>();
		LongSet passedMs=HashLongSets.newMutableSet();
		LongSet knownKs=HashLongSets.newMutableSet();
		// First pass: iterate for solutions to the equation a*b^2+1=(a+1)*c^2, taking m=a*b^2.
		for (long a=1;;++a)	{
			SortedSet<Long> specialIs=new TreeSet<>();
			System.out.println("a="+a+"...");
			long b=1;
			long c=1;
			long _2a=2*a;
			long _2a1=_2a+1;
			long _2a2=_2a+2;
			for(;;)	{
				long nextB=_2a1*b+_2a2*c;
				long nextC=_2a*b+_2a1*c;
				long m=a*nextB*nextB;
				System.out.println("\tm="+m+"...");
				if (!passedMs.add(m)) continue;
				if (!solver.getSolutionsWithAlpertron(m,knownKs)) break;
				specialIs.add(m+1);
				b=nextB;
				c=nextC;
			}
			if (specialIs.isEmpty()) break;
			else groupedPassedIs.put(a,specialIs);
		}
		// Aside from the previous case, we need to store odd squares.
		{
			SortedSet<Long> oddSquares=new TreeSet<>();
			for (long r=3;;r+=2)	{
				long s=r*r;
				if (s>=LIMIT) break;
				oddSquares.add(s);
			}
			groupedPassedIs.put(0l,oddSquares);
		}
		// Iterate special values of i found in the previous steps.
		LongSet ungroupedPassedIs=HashLongSets.newMutableSet();
		for (Map.Entry<Long,SortedSet<Long>> entry:groupedPassedIs.entrySet())	{
			System.out.println("Special cases for a="+entry.getKey()+"...");
			for (long i:entry.getValue())	{
				long m=4*i*(i-1);
				System.out.println("\ti="+i+", m="+m+"...");
				if (!passedMs.add(m)) continue;
				if (!solver.getSolutionsWithAlpertron(m,knownKs)) break;
			}
		}
		// Iterate "normal" values of i ("special" values of m) which have two patterns of solutions.
		System.out.println("Iterating special values...");
		for (long i=2;;++i) if (ungroupedPassedIs.contains(i)) continue;
		else if (!solver.getSpecialSolutions(i,passedMs,knownKs)) break;
		// Iterate "normal" values of m which have one pattern of solutions.
		for (long m=1;;++m) if (passedMs.contains(m)) continue;
		else if (!solver.getStandardSolutions(m,knownKs)) break;
		// The solution is the sum of all Ks.
		long result=0;
		for (LongCursor cursor=knownKs.cursor();cursor.moveNext();) result+=cursor.elem();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
