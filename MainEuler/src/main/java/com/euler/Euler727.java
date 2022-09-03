package com.euler;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.euler.common.EulerUtils;

public class Euler727 {
	private final static int MAX_RADIUS=100;
	
	private static double sq(double x)	{
		return x*x;
	}
	
	private static class Centre	{
		public final double x;
		public final double y;
		public Centre(double x,double y)	{
			this.x=x;
			this.y=y;
		}
		public double getDistance(Centre other)	{
			return Math.sqrt(sq(x-other.x)+sq(y-other.y));
		}
	}
	
	private static class TriangleProblem	{
		private final double r1;
		private final double r2;
		private final double r12;	// AKA r.
		private final double r13;	// AKA q.
		private final double r23;	// AKA p.
		private final double r4;
		private final Centre thirdPoint;
		public TriangleProblem(double r1,double r2,double r3)	{
			// r3 is not stored because it's not used at any point.
			this.r1=r1;
			this.r2=r2;
			r12=r1+r2;
			r13=r1+r3;
			r23=r2+r3;
			r4=getInternalRadius(r1,r2,r3);
			thirdPoint=getThirdPoint(r12,r13,r23);
		}
		private static Centre getThirdPoint(double r12,double r13,double r23)	{
			double x=(sq(r12)+sq(r13)-sq(r23))/(2*r12);
			double y=Math.sqrt(sq(r13)-sq(x));
			return new Centre(x,y);
		}
		private static double getInternalRadius(double r1,double r2,double r3)	{
			double k1=1/r1;
			double k2=1/r2;
			double k3=1/r3;
			double k4=k1+k2+k3+2*Math.sqrt(k1*k2+k2*k3+k3*k1);
			return 1/k4;
		}
		public Centre getIncentre()	{
			/*
			 * (p+q+r)*x = (p*x1+q*x2+r*x3), but x1=0. x2 is r12 and x3 is thirdPoint.x. So num=r13*r12+r12*x3=r12*(r13+x3).
			 * (p+q+r)*y = (p*y1+q*y2+r*y3), but y1=y2=0. y3 is is thirdPoint.y.
			 */
			double pqr=r12+r13+r23;
			double numX=r12*(r13+thirdPoint.x);
			double numY=r12*thirdPoint.y;
			return new Centre(numX/pqr,numY/pqr);
		}
		public Centre getInnerCircleCentre()	{
			return getThirdPoint(r12,r1+r4,r2+r4);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		int count=0;
		double sum=0d;
		for (int i=1;i<MAX_RADIUS;++i) for (int j=i+1;j<MAX_RADIUS;++j) for (int k=j+1;k<=MAX_RADIUS;++k) if (EulerUtils.areCoprime(i,j,k))	{
			TriangleProblem problem=new TriangleProblem(i,j,k);
			Centre d=problem.getIncentre();
			Centre e=problem.getInnerCircleCentre();
			++count;
			sum+=d.getDistance(e);
		}
		double result=sum/count;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		NumberFormat format=DecimalFormat.getInstance();
		format.setMinimumFractionDigits(8);
		System.out.println(format.format(result));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
