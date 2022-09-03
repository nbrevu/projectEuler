package com.euler;

import com.euler.common.EulerUtils;
import com.euler.common.Primes.PrimeFactorsLattice;
import com.euler.common.Primes.PrimeFactorsLatticeGenerator;
import com.google.common.math.LongMath;
import com.koloboke.collect.LongCursor;

public class Euler769_5 {
	private final static long N=LongMath.pow(10l,14);
	
	private final static double S3=Math.sqrt(3d);
	private final static double P_BOUND_DENOM=5*Math.sqrt(3)-6;
	
	private static long trueMod(long x,long mod)	{
		return ((x%mod)+mod)%mod;
	}
	/*
	 * Returns the amount of values in the set [low,high) that match the congruence x==b (mod a).
	 * That is, values of the form a*x+b.
	 */
	private static long countInstances(long low,long high,long a,long b)	{
		long lowMod=low%a;
		long highMod=high%a;
		low+=trueMod(b-lowMod,a);
		high+=trueMod(b-highMod,a);
		return (low>=high)?0l:((high-low)/a);
	}
	
	private static long countInstancesSpecial(long low,long high,long k,long p)	{
		long baseMod=(9*p)%13;
		long finalMod=EulerUtils.solveChineseRemainder(0,k,baseMod,13);
		return countInstances(low,high,k*13,finalMod);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		long maxP1=(long)Math.sqrt(N/P_BOUND_DENOM);
		long maxP2=(long)Math.sqrt(13*N/P_BOUND_DENOM);
		PrimeFactorsLatticeGenerator gen=new PrimeFactorsLatticeGenerator(maxP2);
		for (long p=1;p<=maxP1;++p)	{
			PrimeFactorsLattice lattice=gen.getFor(p);
			long pp=p*p;
			long delta1=Math.max(0l,13*pp-4*N);
			long q1=(long)Math.ceil(S3*p);
			long q2_1=(long)Math.ceil((5*p-Math.sqrt(delta1))/2);
			for (LongCursor cursor=lattice.positiveMöbius.cursor();cursor.moveNext();) result+=countInstances(q1,q2_1,cursor.elem(),0);
			for (LongCursor cursor=lattice.negativeMöbius.cursor();cursor.moveNext();) result-=countInstances(q1,q2_1,cursor.elem(),0);
			if ((p%13)!=0)	{
				long delta13=Math.max(0l,13*p-52*N);
				long q2_13=(long)Math.ceil((5*p-Math.sqrt(delta13))/2);
				for (LongCursor cursor=lattice.positiveMöbius.cursor();cursor.moveNext();) result+=countInstancesSpecial(q2_1,q2_13,cursor.elem(),p);
				for (LongCursor cursor=lattice.negativeMöbius.cursor();cursor.moveNext();) result-=countInstancesSpecial(q2_1,q2_13,cursor.elem(),p);
			}
		}
		for (long p=1+maxP1;p<=maxP2;++p)	{
			if ((p%13)==0) continue;
			PrimeFactorsLattice lattice=gen.getFor(p);
			long delta=Math.max(0l,13*p*p-52*N);
			long q1=(long)Math.ceil(S3*p);
			long q2=(long)Math.ceil((5*p-Math.sqrt(delta))/2);
			for (LongCursor cursor=lattice.positiveMöbius.cursor();cursor.moveNext();) result+=countInstancesSpecial(q1,q2,cursor.elem(),p);
			for (LongCursor cursor=lattice.negativeMöbius.cursor();cursor.moveNext();) result-=countInstancesSpecial(q1,q2,cursor.elem(),p);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
