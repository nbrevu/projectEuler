package com.euler;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;

import com.euler.common.Primes.PrimeFactorsLattice;
import com.euler.common.Primes.PrimeFactorsLatticeGenerator;
import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.set.LongSet;

public class Euler441_4 {
	private final static int M=IntMath.pow(10,7);
	
	private final static MathContext CONTEXT=new MathContext(30,RoundingMode.HALF_UP);
	
	private static class DifferenceCalculator	{
		private final BigDecimal[] hs;
		private final BigDecimal[] fs;
		private final PrimeFactorsLattice[] primeLattices;
		public DifferenceCalculator(int limit)	{
			hs=new BigDecimal[1+limit];
			hs[1]=BigDecimal.ONE;
			for (int n=2;n<=limit;++n) hs[n]=hs[n-1].add(BigDecimal.ONE.divide(BigDecimal.valueOf(n),CONTEXT),CONTEXT);
			fs=new BigDecimal[1+limit];
			fs[1]=BigDecimal.ZERO;
			fs[2]=BigDecimal.ONE.divide(BigDecimal.valueOf(2),CONTEXT);
			for (int i=3;i<=limit;++i)	{
				BigDecimal n=BigDecimal.valueOf(i);
				BigDecimal factor1=BigDecimal.valueOf(2*i-3).divide(BigDecimal.valueOf(i),CONTEXT);
				BigDecimal n1=BigDecimal.valueOf(i-1);
				BigDecimal n2=BigDecimal.valueOf(i-2);
				BigDecimal factor2=n2.multiply(n2).divide(n.multiply(n1),CONTEXT);
				fs[i]=factor1.multiply(fs[i-1],CONTEXT).subtract(factor2.multiply(fs[i-2],CONTEXT),CONTEXT);
			}
			PrimeFactorsLatticeGenerator latticeGenerator=new PrimeFactorsLatticeGenerator(limit);
			primeLattices=new PrimeFactorsLattice[1+limit];
			for (int i=2;i<=limit;++i) primeLattices[i]=latticeGenerator.getFor(i);
		}
		public BigDecimal getRPlus(int m)	{
			PrimeFactorsLattice lattice=primeLattices[m];
			BigDecimal result=BigDecimal.ZERO;
			LongSet casesToAdd=lattice.positiveMöbius;
			LongSet casesToSubtract=lattice.negativeMöbius;
			for (LongCursor cursor=casesToAdd.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				result=result.add(hs[(int)(m/d)].divide(BigDecimal.valueOf(d),CONTEXT),CONTEXT);
			}
			for (LongCursor cursor=casesToSubtract.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				result=result.subtract(hs[(int)(m/d)].divide(BigDecimal.valueOf(d),CONTEXT),CONTEXT);
			}
			return result.divide(BigDecimal.valueOf(m),CONTEXT);
		}
		public BigDecimal getRMinus(int m)	{
			int m_=m-1;
			PrimeFactorsLattice lattice=primeLattices[m_];
			BigDecimal result=BigDecimal.ZERO;
			LongSet casesToAdd=lattice.positiveMöbius;
			LongSet casesToSubtract=lattice.negativeMöbius;
			for (LongCursor cursor=casesToAdd.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				int index=(int)(m_/d);
				result=result.add(fs[index].divide(BigDecimal.valueOf(d*d),CONTEXT),CONTEXT);
			}
			for (LongCursor cursor=casesToSubtract.cursor();cursor.moveNext();)	{
				long d=cursor.elem();
				int index=(int)(m_/d);
				result=result.subtract(fs[index].divide(BigDecimal.valueOf(d*d),CONTEXT),CONTEXT);
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		DifferenceCalculator calc=new DifferenceCalculator(M);
		BigDecimal r=BigDecimal.ONE;
		BigDecimal result=BigDecimal.valueOf(1.5);
		for (int i=4;i<=M;++i)	{
			r=r.add(calc.getRPlus(i),CONTEXT).subtract(calc.getRMinus(i),CONTEXT);
			result=result.add(r,CONTEXT);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.4f",result.doubleValue()));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
