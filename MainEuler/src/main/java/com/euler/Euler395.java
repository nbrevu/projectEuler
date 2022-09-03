package com.euler;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Euler395 {
	private final static double DIST_X=0.64;
	private final static double DIST_Y=0.48;
	private final static double TOL=5e-14;
	
	private static class Point	{
		public final double x;
		public final double y;
		public Point(double x,double y)	{
			this.x=x;
			this.y=y;
		}
		public static Point leftmost(Point p1,Point p2)	{
			return (p1.x<p2.x)?p1:p2;
		}
		public static Point rightmost(Point p1,Point p2)	{
			return (p1.x>p2.x)?p1:p2;
		}
		public static Point upmost(Point p1,Point p2)	{
			return (p1.y>p2.y)?p1:p2;
		}
		public static Point downmost(Point p1,Point p2)	{
			return (p1.y<p2.y)?p1:p2;
		}
		public static double angle(Point p1,Point p2)	{
			return Math.atan2(p2.y-p1.y,p2.x-p1.x);
		}
		public static double distance(Point p1,Point p2)	{
			double dx=p2.x-p1.x;
			double dy=p2.y-p1.y;
			return Math.sqrt(dx*dx+dy*dy);
		}
		public Point move(double dx,double dy)	{
			return new Point(x+dx,y+dy);
		}
		@Override
		public String toString()	{
			return "("+x+","+y+")";
		}
	}
	private static class Segment	{
		public final Point p1;
		public final Point p2;
		public Segment(Point p1,Point p2)	{
			this.p1=p1;
			this.p2=p2;
		}
		public double length()	{
			return Point.distance(p1,p2);
		}
		@Override
		public String toString()	{
			return "{"+p1+" - "+p2+" => "+length()+"}";
		}
	}
	
	private static class SquareBase	{
		public final Segment base;
		public SquareBase(Segment base)	{
			this.base=base;
		}
		public Collection<SquareBase> getChildren()	{
			double baseAngle=Point.angle(base.p1,base.p2);
			double angle=baseAngle+PI/2;
			double distance=Point.distance(base.p1,base.p2);
			double dx=distance*cos(angle);
			double dy=distance*sin(angle);
			Point base1=base.p1.move(dx,dy);
			Point base2=base.p2.move(dx,dy);
			dx=(DIST_Y*cos(angle)+DIST_X*cos(baseAngle))*distance;
			dy=(DIST_Y*sin(angle)+DIST_X*sin(baseAngle))*distance;
			Point apex=base1.move(dx,dy);
			Segment s1=new Segment(base1,apex);
			Segment s2=new Segment(apex,base2);
			return Arrays.asList(new SquareBase(s1),new SquareBase(s2));
		}
		public Point leftmost()	{
			return Point.leftmost(base.p1,base.p2);
		}
		public Point rightmost()	{
			return Point.rightmost(base.p1,base.p2);
		}
		public Point upmost()	{
			return Point.upmost(base.p1,base.p2);
		}
		public Point downmost()	{
			return Point.downmost(base.p1,base.p2);
		}
		@Override
		public String toString()	{
			return base.toString();
		}
	}
	
	private static Comparator<SquareBase> LEFTMOST_COMPARATOR=new Comparator<SquareBase>()	{
		@Override
		public int compare(SquareBase o1, SquareBase o2) {
			Point p1=o1.leftmost();
			Point p2=o2.leftmost();
			if (p1==p2) return 0;
			return (Point.leftmost(p1,p2)==p1)?-1:1;
		}
	};
	
	private static Comparator<SquareBase> RIGHTMOST_COMPARATOR=new Comparator<SquareBase>()	{
		@Override
		public int compare(SquareBase o1, SquareBase o2) {
			Point p1=o1.rightmost();
			Point p2=o2.rightmost();
			if (p1==p2) return 0;
			return (Point.rightmost(p1,p2)==p1)?-1:1;
		}
	};
	
	private static Comparator<SquareBase> UPMOST_COMPARATOR=new Comparator<SquareBase>()	{
		@Override
		public int compare(SquareBase o1, SquareBase o2) {
			Point p1=o1.upmost();
			Point p2=o2.upmost();
			if (p1==p2) return 0;
			return (Point.upmost(p1,p2)==p1)?-1:1;
		}
	};
	
	private static Comparator<SquareBase> DOWNMOST_COMPARATOR=new Comparator<SquareBase>()	{
		@Override
		public int compare(SquareBase o1, SquareBase o2) {
			Point p1=o1.downmost();
			Point p2=o2.downmost();
			if (p1==p2) return 0;
			return (Point.downmost(p1,p2)==p1)?-1:1;
		}
	};
	
	private static SquareBase mostExtreme(SquareBase initial,Comparator<SquareBase> sorter)	{
		List<SquareBase> list=Arrays.asList(initial);
		for (;;)	{
			Collections.sort(list,sorter);
			if (list.size()>5) list=list.subList(0,5);
			if (list.get(0).base.length()<TOL) break;
			List<SquareBase> newList=new ArrayList<>();
			for (SquareBase base:list) newList.addAll(base.getChildren());
			list=newList;
		}
		Collections.sort(list,sorter);
		return list.get(0);
	}
	
	public static void main(String[] args)	{
		SquareBase initial=new SquareBase(new Segment(new Point(0.0,0.0),new Point(1.0,0.0)));
		SquareBase leftmost=mostExtreme(initial,LEFTMOST_COMPARATOR);
		SquareBase rightmost=mostExtreme(initial,RIGHTMOST_COMPARATOR);
		SquareBase upmost=mostExtreme(initial,UPMOST_COMPARATOR);
		SquareBase downmost=mostExtreme(initial,DOWNMOST_COMPARATOR);
		double x1=leftmost.leftmost().x;
		double x2=rightmost.rightmost().x;
		double y1=downmost.downmost().y;
		double y2=upmost.upmost().y;
		// Ich muss eine Englische Locale benutzen, denn ich Punkte und nicht Kommas mag.
		System.out.println(String.format(Locale.UK,"%.10f",(x2-x1)*(y2-y1)));
	}
}
