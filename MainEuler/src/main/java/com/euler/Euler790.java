package com.euler;

public class Euler790 {
	private final static long SIZE=50515093l;
	private final static long SEED=290797l;
	
	private static class Bounds	{
		public final long x0;
		public final long x1;
		public final long y0;
		public final long y1;
		public Bounds(long xA,long xB,long yA,long yB)	{
			if (xA<=xB)	{
				x0=xA;
				x1=xB;
			}	else	{
				x0=xB;
				x1=xA;
			}
			if (yA<=yB)	{
				y0=yA;
				y1=yB;
			}	else	{
				y0=yB;
				y1=yA;
			}
		}
	}
	
	private static class RandomGenerator	{
		private long value;
		public RandomGenerator()	{
			value=SEED;
		}
		public long next()	{
			long result=value;
			value=(value*value)%SIZE;
			return result;
		}
	}
	
	public static Bounds[] generateBounds(int size) {
		Bounds[] result=new Bounds[size];
		RandomGenerator gen=new RandomGenerator();
		for (int i=0;i<size;++i)	{
			long xA=gen.next();
			long xB=gen.next();
			long yA=gen.next();
			long yB=gen.next();
			result[i]=new Bounds(xA,xB,yA,yB);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		// I can do this for a moderate size. The issue is that maybe the obvious algorithm doesn't work for 10^N.
		long v1=12*SIZE*SIZE;
		Bounds[] bounds=generateBounds(1);
		Bounds b0=bounds[0];
		long v2=v1-11*((b0.x1+1-b0.x0)*(b0.y1+1-b0.y0));
		System.out.println(v1);
		System.out.println(v2);
	}
}
