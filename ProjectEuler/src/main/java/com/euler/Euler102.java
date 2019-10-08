package com.euler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.euler.common.Rational;
import com.euler.common.Timing;

public class Euler102 {
	private static class Point	{
		public final Rational x;
		public final Rational y;
		public Point(long x,long y)	{
			this(new Rational(x),new Rational(y));
		}
		public Point(Rational x,Rational y)	{
			this.x=x;
			this.y=y;
		}
		public static Point center(Point... points)	{
			Rational sumX=Rational.ZERO;
			Rational sumY=Rational.ZERO;
			for (Point p:points)	{
				sumX=sumX.sum(p.x);
				sumY=sumY.sum(p.y);
			}
			Rational denom=new Rational(points.length);
			return new Point(sumX.divide(denom),sumY.divide(denom));
		}
	}
	
	private static class Line	{
		private final Rational a;
		private final Rational b;
		private final Rational c;
		public Line(Point p,Point q)	{
			a=p.y.subtract(q.y);
			b=q.x.subtract(p.x);
			c=q.y.multiply(p.x).subtract(q.x.multiply(p.y));
		}
		private Rational applyLine(Point p)	{
			return a.multiply(p.x).sum(b.multiply(p.y)).sum(c);
		}
		public int getSide(Point p)	{
			return applyLine(p).signum();
		}
		public boolean sameSide(Point p1,Point p2)	{
			return getSide(p1)==getSide(p2);
		}
	}
	
	private static class Triangle	{
		private final Line[] lines;
		private final Point center;
		public Triangle(String in)	{
			String[] split=in.split(",");
			if (split.length!=6) throw new RuntimeException("Wrong input.");
			Point p1=new Point(Long.parseLong(split[0]),Long.parseLong(split[1]));
			Point p2=new Point(Long.parseLong(split[2]),Long.parseLong(split[3]));
			Point p3=new Point(Long.parseLong(split[4]),Long.parseLong(split[5]));
			lines=new Line[] {new Line(p1,p2),new Line(p2,p3),new Line(p3,p1)};
			center=Point.center(p1,p2,p3);
		}
		public boolean contains(Point p)	{
			for (int i=0;i<3;++i) if (!lines[i].sameSide(center,p)) return false;
			return true;
		}
	}
	
	private static long solve()	{
		try	{
			URL resource=Euler102.class.getResource("in102.txt");
			List<String> lines=Files.lines(Paths.get(resource.toURI())).collect(Collectors.toList());
			Point center=new Point(0,0);
			int count=0;
			for (String line:lines) if (new Triangle(line).contains(center)) ++count;
			return count;
		}	catch (IOException|URISyntaxException exc)	{
			throw new RuntimeException(exc);
		}
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler102::solve);
	}
}
