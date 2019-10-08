package com.euler;

import java.util.Arrays;

import com.euler.common.Rational;
import com.euler.common.Timing;

public class Euler101 {
	private final static long[] COEFFICIENTS=new long[] {1,-1,1,-1,1,-1,1,-1,1,-1,1};
	
	private static class Polynomial	{
		private final Rational[] coeffs;
		public Polynomial(int n)	{
			coeffs=new Rational[n];
		}
		public Polynomial(long[] coeffs)	{
			this.coeffs=new Rational[coeffs.length];
			for (int i=0;i<coeffs.length;++i) this.coeffs[i]=new Rational(coeffs[i]);
		}
		public Rational apply(long in)	{
			int N=coeffs.length;
			Rational result=coeffs[N-1];
			Rational rIn=new Rational(in);
			for (int i=N-2;i>=0;--i) result=result.multiply(rIn).sum(coeffs[i]);
			return result;
		}
		public static Polynomial add(Polynomial p1,Polynomial p2)	{
			int N=Math.max(p1.coeffs.length,p2.coeffs.length);
			Polynomial result=new Polynomial(Math.max(p1.coeffs.length,p2.coeffs.length));
			for (int i=0;i<N;++i)	{
				Rational c1=(p1.coeffs.length>i)?p1.coeffs[i]:Rational.ZERO;
				Rational c2=(p2.coeffs.length>i)?p2.coeffs[i]:Rational.ZERO;
				result.coeffs[i]=c1.sum(c2);
			}
			return result;
		}
		public static Polynomial multiply(Polynomial p1,Polynomial p2)	{
			int N1=p1.coeffs.length;
			int N2=p2.coeffs.length;
			Polynomial result=new Polynomial(N1+N2-1);
			Arrays.fill(result.coeffs,Rational.ZERO);
			for (int i=0;i<N1;++i) for (int j=0;j<N2;++j) result.coeffs[i+j]=result.coeffs[i+j].sum(p1.coeffs[i].multiply(p2.coeffs[j]));
			return result;
		}
		public static Polynomial generateLagrangeElement(long[] values,int index,int maxOrder)	{
			Polynomial monomial=new Polynomial(2);
			monomial.coeffs[1]=Rational.ONE;
			Polynomial result=new Polynomial(new long[] {values[index-1]});
			long denom=1;
			for (int i=1;i<=maxOrder;++i) if (i!=index)	{
				denom*=index-i;
				monomial.coeffs[0]=new Rational(-i);
				result=multiply(result,monomial);
			}
			Rational rDenom=new Rational(denom);
			for (int i=0;i<maxOrder;++i) result.coeffs[i]=result.coeffs[i].divide(rDenom);
			return result;
		}
		public static Polynomial interpolate(long[] values)	{
			Polynomial result=new Polynomial(new long[0]);
			for (int i=1;i<=values.length;++i) result=add(result,generateLagrangeElement(values,i,values.length));
			return result;
		}
	}
	
	private static long solve()	{
		Rational result=Rational.ZERO;
		Polynomial mainPoly=new Polynomial(COEFFICIENTS);
		int N=COEFFICIENTS.length;
		long[] values=new long[0];
		for (int i=1;i<N;++i)	{
			values=Arrays.copyOf(values,i);
			values[i-1]=mainPoly.apply(i).getIntegerValue();
			Polynomial interpolated=Polynomial.interpolate(values);
			for (long j=i+1;;++j)	{
				Rational value=interpolated.apply(j);
				if (!mainPoly.apply(j).equals(value))	{
					result=result.sum(value);
					break;
				}
			}
		}
		return result.getIntegerValue();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler101::solve);
	}
}
