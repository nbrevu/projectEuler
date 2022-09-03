package com.euler;

import java.util.Locale;

public class Euler262_2 {
	private final static double START_POS=200l;
	private final static double END_POS=1400l;
	
	// Min position found using mathematica.
	private final static double MIN_POS=895.483409898746;
	
	private final static double EPSILON=1e-14;
	private final static double COARSE_EPSILON=5e-5;
	
	private static class Point	{
		public final double x;
		public final double y;
		public Point(double x,double y)	{
			this.x=x;
			this.y=y;
		}
		public Point move(Vector2d vector)	{
			return new Point(x+vector.dx,y+vector.dy);
		}
		@Override
		public String toString()	{
			return String.format(Locale.UK,"(%.20f,%.20f).",x,y);
		}
		public double distance(Point other)	{
			return Math.hypot(other.x-x,other.y-y);
		}
	}
	
	private static class Vector2d	{
		public final double dx;
		public final double dy;
		public Vector2d(double dx,double dy)	{
			this.dx=dx;
			this.dy=dy;
		}
		public static Vector2d diff(Point p1,Point p2)	{
			return new Vector2d(p2.x-p1.x,p2.y-p1.y);
		}
		public double scalarProduct(Vector2d other)	{
			return dx*other.dx+dy*other.dy;
		}
		private Vector2d scale(double scale)	{
			return new Vector2d(dx*scale,dy*scale);
		}
		private Vector2d rotateAndScale(boolean turnLeft,double scale)	{
			if (!turnLeft) scale=-scale;	// Yes! This is enough.
			double scaledX=dx*scale;
			double scaledY=dy*scale;
			return new Vector2d(-scaledY,scaledX);
		}
	}
	
	private static class Segment	{
		public final Point start;
		public final Vector2d direction;
		public Segment(Point start,Vector2d direction)	{
			this.start=start;
			this.direction=direction;
		}
		public static Segment between(Point start,Point end)	{
			return new Segment(start,Vector2d.diff(start,end));
		}
		public Segment rescale(double scale)	{
			Vector2d newDirection=new Vector2d(direction.dx*scale,direction.dy*scale);
			return new Segment(start,newDirection);
		}
	}
	
	private static class PositionFinder	{
		private static double h(double x,double y)	{
			double x2=x*x;
			double y2=y*y;
			double sxy=x+y;
			double sxy2=x2+y2;
			return (5000-0.005*(sxy2+x*y)+12.5*sxy)*Math.exp(-(0.000001*sxy2-0.0015*sxy+0.7));
		}
		private static Vector2d directionalDerivative(Point p)	{
			return directionalDerivative(p.x,p.y);
		}
		private static Vector2d directionalDerivative(double x,double y)	{
			double sxy=x+y;
			double sxy2=x*x+y*y;
			double term1=Math.exp(-0.7+0.0015*sxy-1e-6*sxy2);
			double term2x=12.5-0.005*(2*x+y);
			double term2y=12.5-0.005*(2*y+x);
			double term3b=5000+12.5*sxy-0.005*(sxy2+x*y);
			double term3ax=0.0015-2e-6*x;
			double term3ay=0.0015-2e-6*y;
			double term3x=term3ax*term3b;
			double term3y=term3ay*term3b;
			double dx=term1*(term2x+term3x);
			double dy=term1*(term2y+term3y);
			return new Vector2d(dx,dy);
		}
		private final double epsilon;
		private final double coarseEpsilon;
		private final double fMin;
		public PositionFinder(Point basePoint,double epsilon,double coarseEpsilon)	{
			this.epsilon=epsilon;
			this.coarseEpsilon=coarseEpsilon;
			fMin=h(basePoint.x,basePoint.y);
		}
		public Point binarySearch(Segment s)	{
			double tStart=0;
			double fStart=h(s.start.x,s.start.y)-fMin;
			double tEnd=1;
			double fEnd=h(s.start.x+s.direction.dx,s.start.y+s.direction.dy)-fMin;
			if (fStart*fEnd>0) throw new ArithmeticException("Ill-defined problem");
			while (tEnd-tStart>epsilon)	{
				double tMiddle=(tStart+tEnd)*0.5;
				double f=h(s.start.x+tMiddle*s.direction.dx,s.start.y+tMiddle*s.direction.dy)-fMin;
				if (f==0) return new Point(s.start.x+tMiddle*s.direction.dx,s.start.y+tMiddle*s.direction.dy);
				if (fStart*f>0)	{
					tStart=tMiddle;
					fStart=f;
				}	else	{
					tEnd=tMiddle;
					fEnd=f;
				}
			}
			// double tMiddle=(tStart+tEnd)*0.5;
			double tMiddle=tStart;
			return new Point(s.start.x+tMiddle*s.direction.dx,s.start.y+tMiddle*s.direction.dy);
		}
		// Advances an approximate distance equal to "length" until f(x,y)=fMin.
		public Point advance(Point p,double length,boolean workInReverse)	{
			Vector2d gradient=directionalDerivative(p);
			double factor=length/Math.hypot(gradient.dx,gradient.dy);
			Vector2d toAdvance=gradient.rotateAndScale(workInReverse,factor);
			Point basePoint=p.move(toAdvance);
			try	{
				return binarySearch(new Segment(basePoint,gradient.scale(factor)));
			}	catch (RuntimeException exc)	{
				System.out.println("Ay, mecachis: "+p+".");
				throw exc;
			}
		}
		// Looks for a point where f(x,y)=fMin and so that the vector p-(x,y) is tangent to the h(x,y)==fMin curve.
		public Point findTangentialPoint(Point origin,Point startingPoint,double initialStepLength,boolean workInReverse)	{
			double product=calculateOrthogonality(origin,startingPoint);
			Point currentPoint=startingPoint;
			for (;;)	{
				Point nextPoint=advance(currentPoint,initialStepLength,workInReverse);
				double nextProduct=calculateOrthogonality(origin,nextPoint);
				if (nextProduct*product<0) return findTangentialPointBounded(origin,currentPoint,initialStepLength,workInReverse);
				currentPoint=nextPoint;
			}
		}
		private double calculateOrthogonality(Point origin,Point p)	{
			Vector2d gradient=directionalDerivative(p);
			Vector2d baseVector=Vector2d.diff(origin,p);
			return gradient.scalarProduct(baseVector);
		}
		private Point findTangentialPointBounded(Point origin,Point p1,double toAdvance,boolean workInReverse)	{
			double product1=calculateOrthogonality(origin,p1);
			while (toAdvance>coarseEpsilon)	{
				Point p2=advance(p1,toAdvance,workInReverse);
				double product2=calculateOrthogonality(origin,p2);
				double pProduct=product1*product2;
				if (pProduct==0) return p2;
				else if (pProduct>0) p1=p2;
				toAdvance*=0.5;
			}
			return p1;
		}
		public Point findFirstTangentPoint(Point origin,Point searchEnd,boolean workInReverse)	{
			Segment searcher=Segment.between(origin,searchEnd).rescale(0.95);
			Point contact=binarySearch(searcher);
			return findTangentialPoint(origin,contact,0.5,workInReverse);
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		Point corner=new Point(MIN_POS,0d);
		PositionFinder finder=new PositionFinder(corner,EPSILON,COARSE_EPSILON);
		Point initialPoint=new Point(START_POS,START_POS);
		Point endPoint=new Point(END_POS,END_POS);
		Point tangentStart=finder.findFirstTangentPoint(initialPoint,corner,false);
		Point tangentEnd=finder.findFirstTangentPoint(endPoint,corner,true);
		double result=initialPoint.distance(tangentStart)+tangentEnd.distance(endPoint);
		Point currentPoint=tangentStart;
		while (currentPoint.distance(tangentEnd)>COARSE_EPSILON)	{
			Point nextPoint=finder.advance(currentPoint,COARSE_EPSILON,false);
			result+=currentPoint.distance(nextPoint);
			currentPoint=nextPoint;
		}
		result+=currentPoint.distance(tangentEnd);
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
		System.out.println(String.format(Locale.UK,"%.3f.",result));
	}
}
