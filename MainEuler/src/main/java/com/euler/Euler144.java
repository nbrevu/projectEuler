package com.euler;

public class Euler144 {
	private static class Point	{
		public final double x;
		public final double y;
		public Point(double x,double y)	{
			this.x=x;
			this.y=y;
		}
		public boolean exits()	{
			return (x>=-0.01)&&(x<=0.01)&&(y>0);
		}
	}
	
	private static Point reflect(Point p1,Point p2)	{
		double x1=p1.x;
		double x2=p2.x;
		double y1=p1.y;
		double y2=p2.y;
	    double mp=(0.25*y2/x2-(y2-y1)/(x2-x1))/(1+0.25*y2*(y2-y1)/(x2*(x2-x1)));
	    double m=(mp+y2*0.25/x2)/(1-y2*mp*0.25/x2);
	    double t=-(2*y2*m+8*x2)/(4+m*m);
	    double x3=x2+t;
	    double y3=y2+m*t;
	    return new Point(x3,y3);
	}
	
	public static void main(String[] args)	{
		Point p1=new Point(0d,10.1d);
		Point p2=new Point(1.4d,-9.6d);
		int count=1;
		for (;;)	{
			Point p3=reflect(p1,p2);
			if (p3.exits()) break;
			++count;
			p1=p2;
			p2=p3;
		}
		System.out.println(count);
	}
}
