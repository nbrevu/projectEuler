package com.euler;

public class Euler449_2 {
	// Mierda, esto sólo funciona para A=1 y B=1, por alguna estúpida razón :(((.
	private final static double A=2;
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
			/*
			int N=r.length;
			assert N==h.length;
			List<Double> augends=new ArrayList<>();
			for (int i=0;i<N-1;++i) augends.add(getAugend(r,h,i));
			augends.sort(null);
			double result=0.0;
			for (double d:augends) result+=d;
			return 2*result*Math.PI;
			*/
			int N=r.length;
			assert N==h.length;
			double result=0.0;
			for (int i=0;i<N-1;++i) result+=getAugend(r,h,i);
			return 2*result*Math.PI;
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
				double h0=b*Math.cos(phi);
				double r0=a*Math.sin(phi);
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
	
	private static class VolumeCalculator	{
		private final double a;
		private final double b;
		private final int points;
		public VolumeCalculator(double a,double b,int points)	{
			this.a=a;
			this.b=b;
			this.points=points;
		}
		public double getInnerEllipsoidVolume()	{
			return Math.PI*a*a*b*4.0/3.0;
		}
		public <T extends Integrator> double getTotalVolume(T integrator)	{
			ProfileGenerator generator=new ProfileGenerator(points);
			generator.generate(a,b);
			return integrator.integrate(generator.getR(),generator.getH());
		}
	}
	
	public static void main(String[] args) throws ReflectiveOperationException	{
		long tic=System.nanoTime();
		VolumeCalculator calc=new VolumeCalculator(A,B,POINTS);
		double ellipsoid=calc.getInnerEllipsoidVolume();
		double totalVolume=calc.getTotalVolume(new SlopeBasedTubeIntegrator());
		double result=totalVolume-ellipsoid;
		long tac=System.nanoTime();
		System.out.println("Total: "+totalVolume+"; inner: "+ellipsoid+".");
		System.out.println(result);
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
