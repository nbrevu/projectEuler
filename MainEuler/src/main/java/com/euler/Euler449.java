package com.euler;

import java.util.ArrayList;
import java.util.List;

public class Euler449 {
	private final static double A=1;
	private final static double B=1;
	
	private final static int POINTS=100000000;
	
	/*
	 * R and h are built from the top, so that the first element is R=0, h=b and the last one is R=a, h=0.
	 */
	private static abstract class Integrator	{
		protected static double sq(double in)	{
			return in*in;
		}
		protected abstract double getAugend(double[] r,double[] h,int index);
		public final double integrate(double[] r,double[] h)	{
			int N=r.length;
			assert N==h.length;
			List<Double> augends=new ArrayList<>();
			for (int i=0;i<N-1;++i) augends.add(getAugend(r,h,i));
			augends.sort(null);
			double result=0.0;
			for (double d:augends) result+=d;
			return 2*result*Math.PI;
		}
	}
	private static abstract class FixedHeightTubeIntegrator extends Integrator	{
		protected abstract double getH(double[] h,int index);
		@Override
		protected double getAugend(double[] r,double[] h,int index)	{
			double rInt=r[index];
			double rExt=r[index+1];
			double hh=getH(h,index);
			return (sq(rExt)-sq(rInt))*hh;
		}
	}
	private static class MaxHeightTubeIntegrator extends FixedHeightTubeIntegrator	{
		@Override
		protected double getH(double[] h,int index)	{
			return h[index];
		}
	}
	private static class MinHeightTubeIntegrator extends FixedHeightTubeIntegrator	{
		@Override
		protected double getH(double[] h,int index)	{
			return h[index+1];
		}
	}
	private static class MeanHeightTubeIntegrator extends FixedHeightTubeIntegrator	{
		@Override
		protected double getH(double[] h,int index)	{
			return (h[index]+h[index+1])/2.0;
		}
	}
	private static class SlopeBasedTubeIntegrator extends Integrator	{
		@Override
		protected double getAugend(double[] r, double[] h, int index) {
			double r1=r[index];
			double r2=r[index+1];
			double h1=h[index];
			double h2=h[index+1];
			double sR1=sq(r1);
			double sR2=sq(r2);
			return h1*(sR2-sR1)+(2*sR2-r1*r2-sR1)*(h2-h1)/3.0;
		}
	}
	private static abstract class FixedWidthDiscIntegrator extends Integrator	{
		protected abstract double getR(double[] r,int index);
		@Override
		protected double getAugend(double[] r,double[] h,int index)	{
			double rr=getR(r,index);
			double hUpper=h[index];
			double hLower=h[index+1];
			return sq(rr)*(hUpper-hLower);
		}
	}
	private static class MaxWidthDiscIntegrator extends FixedWidthDiscIntegrator	{
		@Override
		protected double getR(double[] r, int index) {
			return r[index+1];
		}
	}
	private static class MinWidthDiscIntegrator extends FixedWidthDiscIntegrator	{
		@Override
		protected double getR(double[] r, int index) {
			return r[index];
		}
	}
	private static class MeanWidthDiscIntegrator extends FixedWidthDiscIntegrator	{
		@Override
		protected double getR(double[] r, int index) {
			return (r[index]+r[index+1])/2.0;
		}
	}
	private static class SlopeBasedDiscIntegrator extends Integrator	{
		@Override
		protected double getAugend(double[] r, double[] h, int index) {
			double r1=r[index];
			double r2=r[index+1];
			double h1=h[index];
			double h2=h[index+1];
			double sR1=sq(r1);
			double sR2=sq(r2);
			return (sR1+r1*r2+sR2)*(h1-h2)/3.0;
		}
	}
	
	private static class ProfileGenerator	{
		private final double[] h;
		private final double[] r;
		private final int N;
		public ProfileGenerator(int N)	{
			h=new double[N];
			r=new double[N];
			this.N=N;
		}
		public void generate(double a,double b)	{
			for (int i=0;i<N;++i)	{
				double phi=i*Math.PI/(2*N);
				double h0=a*Math.cos(phi);
				double r0=b*Math.sin(phi);
				double D=Math.sqrt(h0*h0+r0*r0);
				double factor=1+1/D;
				h[i]=h0*factor;
				r[i]=r0*factor;
			}
		}
		public double[] getH()	{
			return h;
		}
		public double[] getR()	{
			return r;
		}
	}
	
	public static void main(String[] args)	{
		/*
		Valor real:   33.510321638291124.
		Integrador 0: 33.51032190147854.
		Integrador 1: 33.510321375116426.
		Integrador 2: 33.51032163828289.
		Integrador 3: 33.51032163828289.
		Integrador 4: 33.510321111920334.
		Integrador 5: 33.51032058553245.
		Integrador 6: 33.510320848718905.
		Integrador 7: 33.510320848718905.
		AND THE WINNER IS: integrador 2 ó integrador 3 (MeanHeightTubeIntegrator / SlopeBasedTubeIntegrator).
		 */
		Integrator[] integrators=new Integrator[8];
		integrators[0]=new MaxHeightTubeIntegrator();
		integrators[1]=new MinHeightTubeIntegrator();
		integrators[2]=new MeanHeightTubeIntegrator();
		integrators[3]=new SlopeBasedTubeIntegrator();
		integrators[4]=new MaxWidthDiscIntegrator();
		integrators[5]=new MinWidthDiscIntegrator();
		integrators[6]=new MeanWidthDiscIntegrator();
		integrators[7]=new SlopeBasedDiscIntegrator();
		ProfileGenerator generator=new ProfileGenerator(POINTS);
		generator.generate(A,B);
		double[] r=generator.getR();
		double[] h=generator.getH();
		double[] results=new double[8];
		for (int i=0;i<8;++i) results[i]=integrators[i].integrate(r,h);
		double actualValue=32.0*Math.PI/3.0;
		System.out.println("Valor real: "+actualValue+".");
		for (int i=0;i<8;++i) System.out.println("Integrador "+i+": "+results[i]+".");
	}
}
