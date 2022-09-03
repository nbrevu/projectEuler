package com.euler;

import java.util.Locale;

import com.euler.common.DoubleMatrix;
import com.google.common.math.LongMath;

public class Euler444 {
	private final static int K=20;
	
	private final static double EULER_MASCHERONI=0.57721566490153286060651209008240243104215933593992d;	// Thanks, wikipedia.
	
	// Wrong, but I'm sure that it's because of precision issues... let's use rationals for the coefficient calculation.
	public static void main(String[] args)	{
		long N=LongMath.pow(10l,14);
		double[] e=new double[K];
		e[0]=1d;
		for (int i=1;i<K;++i) e[i]=e[i-1]+1d/(i+1);
		double[] s=new double[K];
		// S_1:
		s[0]=e[0];
		for (int i=1;i<K;++i) s[i]=s[i-1]+e[i];
		// S_N:
		for (int j=2;j<=K;++j) for (int i=1;i<K;++i) s[i]+=s[i-1];
		double[] fs=new double[K];
		double factInv=1;
		for (int i=2;i<=K;++i) factInv/=i;
		for (int i=0;i<K;++i)	{
			double sub=e[i]*factInv;
			for (int j=0;j<K;++j) sub*=i+j+2;
			fs[i]=s[i]-sub;
		}
		DoubleMatrix m=new DoubleMatrix(K);
		for (int i=0;i<K;++i)	{
			double pow=i+1;
			m.assign(i,0,pow);
			for (int j=1;j<K;++j)	{
				pow*=i+1;
				m.assign(i,j,pow);
			}
		}
		DoubleMatrix inv=DoubleMatrix.destructiveInverse(m);
		double[] coeffs=inv.multiply(fs);
		double eApprox;
		if (N<1_000_000_000_000l)	{
			eApprox=1;
			for (int i=2;i<=N;++i) eApprox+=1d/i;
		}	else eApprox=Math.log(N)+EULER_MASCHERONI;
		double extraFactor=factInv;
		for (int j=1;j<=K;++j) extraFactor*=N+j;
		double polyVal=0;
		for (int i=K-1;i>=0;--i)	{
			polyVal+=coeffs[i];
			polyVal*=N;
		}
		double result=eApprox*extraFactor+polyVal;
		System.out.println(String.format(Locale.UK,"%.9e",result));
	}
}
