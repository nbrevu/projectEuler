package com.euler;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class Euler456 {
	private final static int POINTS=2_000_000;
	
	private static class IntPoint	{
		public final int x;
		public final int y;
		public IntPoint(int x,int y)	{
			this.x=x;
			this.y=y;
		}
	}
	
	private static class PointGenerator	{
		private int x;
		private int y;
		public PointGenerator()	{
			x=1;
			y=1;
		}
		public IntPoint next()	{
			x*=1248;
			x%=32323;
			y*=8421;
			y%=30103;
			return new IntPoint(x-16161,y-15051);
		}
	}
	
	public static void main(String[] args)	{
		/*
		 * This is doable in O(n^2log(n)). 4 seconds just to generate the array... but it's peanuts compared to the real run time.
		 * It might be a good idea to use rational values for the angles (i.e. storing the ratios and being very careful about signs) to avoid
		 * precision errors. This is promising but it's far from being completed. 
		 */
		long tic=System.nanoTime();
		PointGenerator gen=new PointGenerator();
		SortedMultiset<Double> pointAngles=TreeMultiset.create();
		for (int i=0;i<POINTS;++i)	{
			IntPoint p=gen.next();
			if ((p.x==0)&&(p.y==0)) System.out.println("Hostia, un cero.");
			else	{
				double ang=Math.atan2(p.y,p.x);
				pointAngles.add(ang);
			}
		}
		double[] pointAnglesArray=pointAngles.stream().mapToDouble(Double::doubleValue).toArray();
		long tac=System.nanoTime();
		System.out.println(pointAnglesArray.length);
		System.out.println(pointAnglesArray[0]);
		System.out.println(pointAnglesArray[pointAnglesArray.length-1]);
		System.out.println(String.format("Elapsed %f seconds.",1e-9*(tac-tic)));
	}
}
