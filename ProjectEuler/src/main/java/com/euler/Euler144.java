package com.euler;

import java.awt.geom.Point2D;

import com.euler.common.Timing;

public class Euler144 {
	private final static Point2D.Double P1=new Point2D.Double(0d,10.1d);
	private final static Point2D.Double P2=new Point2D.Double(1.4,-9.6d);
	
	private static long solve()	{
		double mx=P2.x-P1.x;
		double my=P2.y-P1.y;
		double x=P2.x;
		double y=P2.y;
		for (int count=1;;++count)	{
			double nx=-y;
			double ny=4*x;
			double sn=nx*nx+ny*ny;
			double prod=(mx*nx+my*ny)/sn;
			mx-=2*prod*nx;
			my-=2*prod*ny;
			double t=-(8*x*mx+2*y*my)/(4*mx*mx+my*my);
			x+=t*mx;
			y+=t*my;
			if ((y>0)&&(Math.abs(x)<=0.01)) return count;
		}
	}

	public static void main(String[] args)	{
		Timing.time(Euler144::solve);
	}
}
