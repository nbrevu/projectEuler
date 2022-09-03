package com.euler;

import java.util.Locale;

import com.euler.common.BigRational;
import com.euler.common.BigRationalMatrix;
import com.google.common.math.LongMath;

public class Euler444_2 {
	private final static int K=20;
	
	private final static double EULER_MASCHERONI=0.57721566490153286060651209008240243104215933593992d;	// Thanks, wikipedia.
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long N=LongMath.pow(10l,14);
		BigRational[] e=new BigRational[K];
		e[0]=BigRational.ONE;
		for (int i=1;i<K;++i) e[i]=e[i-1].sum(new BigRational(1,i+1));
		BigRational[] s=new BigRational[K];
		// S_1:
		s[0]=e[0];
		for (int i=1;i<K;++i) s[i]=s[i-1].sum(e[i]);
		// S_N:
		for (int j=2;j<=K;++j) for (int i=1;i<K;++i) s[i]=s[i].sum(s[i-1]);
		BigRational[] fs=new BigRational[K];
		BigRational factInv=BigRational.ONE;
		for (int i=2;i<=K;++i) factInv=factInv.multiply(new BigRational(1,i));
		for (int i=0;i<K;++i)	{
			BigRational sub=e[i].multiply(factInv);
			for (int j=0;j<K;++j) sub=sub.multiply(new BigRational(i+j+2));
			fs[i]=s[i].subtract(sub);
		}
		BigRationalMatrix m=new BigRationalMatrix(K);
		for (int i=0;i<K;++i)	{
			BigRational factor=new BigRational(i+1);
			BigRational pow=factor;
			m.assign(i,0,pow);
			for (int j=1;j<K;++j)	{
				pow=pow.multiply(factor);
				m.assign(i,j,pow);
			}
		}
		BigRationalMatrix inv=BigRationalMatrix.destructiveInverse(m);
		BigRational[] coeffs=inv.multiply(fs);
		double eApprox;
		if (N<1_000_000_000_000l)	{
			BigRational bigApprox=BigRational.ONE;
			for (int i=2;i<=N;++i) bigApprox=bigApprox.sum(new BigRational(1,i));
			eApprox=bigApprox.toDouble();
		}	else eApprox=Math.log(N)+EULER_MASCHERONI;
		BigRational extraFactor=factInv;
		for (int j=1;j<=K;++j) extraFactor=extraFactor.multiply(new BigRational(N+j));
		BigRational polyVal=BigRational.ZERO;
		BigRational bigN=new BigRational(N);
		for (int i=K-1;i>=0;--i)	{
			polyVal=polyVal.sum(coeffs[i]);
			polyVal=polyVal.multiply(bigN);
		}
		double result=eApprox*extraFactor.toDouble()+polyVal.toDouble();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.9e",result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
