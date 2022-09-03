package com.euler;

import java.util.Locale;

import com.euler.common.Primes.PrimeFactorsLattice;
import com.euler.common.Primes.PrimeFactorsLatticeGenerator;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler441_3 {
	private final static int M=IntMath.pow(10,7);
	
	private static class DifferenceCalculator	{
		private final double[] hs;
		private final double[] fs;
		private final PrimeFactorsLattice[] primeLattices;
		public DifferenceCalculator(int limit)	{
			hs=new double[1+limit];
			hs[1]=1;
			for (int n=2;n<=limit;++n) hs[n]=hs[n-1]+1d/n;
			fs=new double[1+limit];
			fs[2]=0.5;
			for (int i=3;i<=limit;++i)	{
				long n=i;
				double num1=2*n-3;
				double den1=n;
				double num2=(n-2)*(n-2);
				double den2=(n-1)*n;
				fs[i]=num1/den1*fs[i-1]-num2/den2*fs[i-2];
			}
			PrimeFactorsLatticeGenerator latticeGenerator=new PrimeFactorsLatticeGenerator(limit);
			primeLattices=new PrimeFactorsLattice[1+limit];
			for (int i=2;i<=limit;++i) primeLattices[i]=latticeGenerator.getFor(i);
		}
		public double getRPlus(int m)	{
			PrimeFactorsLattice lattice=primeLattices[m];
			double result=0;
			LongSet casesToAdd=lattice.positiveMöbius;
			LongSet casesToSubtract=lattice.negativeMöbius;
			for (LongCursor cursor=casesToAdd.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				result+=1d/d*hs[(int)(m/d)];
			}
			for (LongCursor cursor=casesToSubtract.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				result-=1d/d*hs[(int)(m/d)];
			}
			return result/m;
		}
		public double getRMinus(int m)	{
			int m_=m-1;
			PrimeFactorsLattice lattice=primeLattices[m_];
			double result=0;
			LongSet casesToAdd=lattice.positiveMöbius;
			LongSet casesToSubtract=lattice.negativeMöbius;
			for (LongCursor cursor=casesToAdd.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				int index=(int)(m_/d);
				double factor=1d/(d*d);
				result+=factor*fs[index];
			}
			for (LongCursor cursor=casesToSubtract.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				int index=(int)(m_/d);
				double factor=1d/(d*d);
				result-=factor*fs[index];
			}
			return result;
		}
	}
	
	/*
	 * Ooooh, precision issues. It's mostly right, at least.
	 * Actual result: 5000088.8395.
	 * I'm getting: 5000116.9200
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DifferenceCalculator calc=new DifferenceCalculator(M);
		double r=1;
		double result=1.5;
		for (int i=4;i<=M;++i)	{
			r+=calc.getRPlus(i)-calc.getRMinus(i);
			result+=r;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.4f",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
