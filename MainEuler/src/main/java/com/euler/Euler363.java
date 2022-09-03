package com.euler;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.signum;
import static java.lang.Math.sqrt;

public class Euler363 {
	// B(t) = [(2-3v)t^3 + (3v-3)t^2 + 1,
	//         (3v-2)t^3 + (3-6v)t^2 + 3vt]
	// B'(t) = [(6-9v)t^2 + (6v-6)t,
	//          (9v-6)t^2 + (6-12v)t + 3v]
	private final static double ROOT_TOLERANCE=1e-15;
	private final static int INTEGRATION_RES=10000000;
	
	private static class Polynomial	{
		private double[] coeffs;
		public Polynomial(double... coeffs)	{
			this.coeffs=coeffs;
		}
		public double eval(double t)	{
			double res=coeffs[0];
			for (int i=1;i<coeffs.length;++i) res=(t*res)+coeffs[i];
			return res;
		}
		public double findRoot(double x0,double x1,double tol)	{
			double f0=eval(x0);
			if (f0==0) return x0;
			double f1=eval(x1);
			if (f1==0) return x1;
			assert(signum(f0)!=signum(f1));
			for (;;)	{
				double x2=(x0+x1)/2.0;
				double f2=eval(x2);
				if (abs(f2)<tol) return x2;
				if (signum(f0)==signum(f2)) x0=x2;
				else x1=x2;
			}
		}
		public double findRoot(double val,double x0,double x1,double tol)	{
			double[] coeffs2=coeffs.clone();
			coeffs2[coeffs2.length-1]-=val;
			return (new Polynomial(coeffs2).findRoot(x0,x1,tol));
		}
	}
	private static class PolynomialGroup	{
		private double v;
		public PolynomialGroup(double v)	{
			this.v=v;
		}
		private Polynomial getBX()	{
			return new Polynomial(2-3*v,3*v-3,0,1);
		}
		private Polynomial getBY()	{
			return new Polynomial(3*v-2,3-6*v,3*v,0);
		}
		private Polynomial getdBX()	{
			return new Polynomial(6-9*v,6*v-6,0);
		}
		private Polynomial getdBY()	{
			return new Polynomial(9*v-6,6-12*v,3*v);
		}
		public double integrateArea(int resolution)	{
			double res=integrate(getBX(),getBY(),resolution);
			System.out.println(""+v+" => "+res);
			return res;
			// return integrate(getBX(),getBY(),resolution);
		}
		public double integrateLength(int resolution)	{
			double increment=1/(double)resolution;
			double xVal=increment/2.0;
			double result=0.0;
			Polynomial p1=getdBX();
			Polynomial p2=getdBY();
			for (int i=0;i<resolution;++i)	{
				double i1=p1.eval(xVal);
				double i2=p2.eval(xVal);
				result+=sqrt(i1*i1+i2*i2);
				xVal+=increment;
			}
			return result*increment;
		}
	}
	
	private static double integrate(Polynomial xPol,Polynomial yPol,int resolution)	{
		double increment=1/(double)resolution;
		double xVal=increment/2.0;
		double result=0.0;
		for (int i=0;i<resolution;++i)	{
			double t=xPol.findRoot(xVal,0.0,1.0,ROOT_TOLERANCE);
			result+=yPol.eval(t);
			xVal+=increment;
		}
		return result*increment;
	}
	public static double findV(double v0,double v1,double expected)	{
		PolynomialGroup g0=new PolynomialGroup(v0);
		PolynomialGroup g1=new PolynomialGroup(v1);
		double f0=g0.integrateArea(INTEGRATION_RES)-expected;
		if (f0==0) return v0;
		double f1=g1.integrateArea(INTEGRATION_RES)-expected;
		if (f1==0) return v1;
		assert(signum(f0)!=signum(f1));
		for (;;)	{
			double v2=(v0+v1)/2.0;
			double f2=new PolynomialGroup(v2).integrateArea(INTEGRATION_RES)-expected;
			if (abs(f2)<ROOT_TOLERANCE) return v2;
			if (signum(f0)==signum(f2)) v0=v2;
			else v1=v2;
		}
	}
	
	public static void main(String[] args)	{
		// double v=findV(0.5,0.6,PI/4);
		double v=2-sqrt((22-5*PI)/3);
		PolynomialGroup group=new PolynomialGroup(v);
		double length=group.integrateLength(INTEGRATION_RES);
		double result=(200.0*length/PI-100.0);
		System.out.println(result);
	}
}
