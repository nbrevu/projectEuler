package com.euler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class Euler630 {
	private final static int N=2500;
	
	private static class GeneratorS	{
		private long s;
		public GeneratorS()	{
			s=290797;
		}
		public long next()	{
			s=(s*s)%50515093;
			return s;
		}
	}
	private static class GeneratorT	{
		private final GeneratorS generator;
		public GeneratorT()	{
			generator=new GeneratorS();
		}
		public int next()	{
			long s=generator.next();
			return (int)((s%2000)-1000);
		}
	}
	private static class Point	{
		public final int x,y;
		public Point(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public int hashCode()	{
			return Integer.hashCode(x)+Integer.hashCode(y);
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				Point pOther=(Point)other;
				return (x==pOther.x)&&(y==pOther.y);
			}	catch (ClassCastException cce)	{
				return false;
			}
		}
	}
	private static class PointGenerator	{
		private final GeneratorT generator;
		public PointGenerator()	{
			generator=new GeneratorT();
		}
		public Point next()	{
			int num=generator.next();
			int den=generator.next();
			return new Point(num,den);
		}
	}
	private static class SpecialFraction	{
		/*
		 * This fraction allows the special case where the denominator is 0. It's used for
		 * vertical lines. In any case the fraction is always stored as an irreductible one
		 * (i.e. 4/6 gets converted to 2/3), following this criterion:
		 * 	1) If numerator is 0, fraction is 0/1.
		 * 	2) If denominator is 0, fraction is 1/0.
		 * 	3) 0/0 is not allowed!
		 * 	4) In any other case, the denominator will ALWAYS be positive. For example,
		 * 		calling the constructor with (6,-10) will result in (-3,5). 
		 */
		public final int num,den;
		public SpecialFraction(int num,int den)	{
			if (num==0)	{
				this.num=0;
				this.den=1;
			}	else if (den==0)	{
				this.num=1;
				this.den=0;
			}	else	{
				boolean diffSign=((num*den)<0);
				num=Math.abs(num);
				den=Math.abs(den);
				int gcd=EulerUtils.gcd(num,den);
				num/=gcd;
				den/=gcd;
				if (diffSign) num=-num;
				this.num=num;
				this.den=den;
			}
		}
		@Override
		public int hashCode()	{
			return 2000*num+den;
			// return Integer.hashCode(num)+Integer.hashCode(den);
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				SpecialFraction sfOther=(SpecialFraction)other;
				return (num==sfOther.num)&&(den==sfOther.den);
			}	catch (ClassCastException cce)	{
				return false;
			}
		}
		public boolean isVertical()	{
			return den==0;
		}
	}
	public static class Line	{
		/*
		 *  By making the slope a fraction and using it as key, we avoid defining an 
		 *  isParallel() function, which we might need to define in other languages. 
		 */
		public final SpecialFraction slope;
		/*
		 * The member "origin" is a fraction but can't actually ever be 1/0.
		 * If the slope is VERTICAL, then "origin" is the cross with the X axis (Y=0).
		 * Otherwise, it's the cross with the Y axis (i.e. X=0).
		 */
		private final SpecialFraction origin;
		private Line(SpecialFraction slope,SpecialFraction origin)	{
			this.slope=slope;
			this.origin=origin;
		}
		@Override
		public int hashCode()	{
			return slope.hashCode()+origin.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			try	{
				Line lOther=(Line)other;
				return slope.equals(lOther.slope)&&origin.equals(lOther.origin);
			}	catch (ClassCastException cce)	{
				return false;
			}
		}
		public static Line getLine(Point p1,Point p2)	{
			// Should never be called with two equal points!
			int dx=p2.x-p1.x;
			int dy=p2.y-p1.y;
			SpecialFraction slope=new SpecialFraction(dy,dx);
			SpecialFraction origin=null;
			if (slope.isVertical()) origin=new SpecialFraction(p1.x,1);
			else	{
				int num=p1.y*slope.den-p1.x*slope.num;
				origin=new SpecialFraction(num,slope.den);
			}
			return new Line(slope,origin);
		}
	}
	
	public static List<Point> getAllPoints(int n)	{
		// Let the Set object manage duplicates...
		Set<Point> res=new HashSet<>();
		PointGenerator gen=new PointGenerator();
		for (int i=0;i<n;++i) res.add(gen.next());
		return new ArrayList<>(res);
	}
	
	public static SetMultimap<SpecialFraction,Line> getAllLines(List<Point> points)	{
		// Behold Guava magic doing all the duplication management!
		SetMultimap<SpecialFraction,Line> knownLines=HashMultimap.create();
		int n=points.size();
		int n_=n-1;
		for (int i=0;i<n_;++i)	{
			Point p1=points.get(i);
			for (int j=i+1;j<n;++j)	{
				Point p2=points.get(j);
				Line l=Line.getLine(p1,p2);
				knownLines.put(l.slope,l);
			}
		}
		return knownLines;
	}
	
	public static long getCrossPoints(SetMultimap<SpecialFraction,Line> allLines)	{
		long res=0;
		long cumulative=0;
		Map<SpecialFraction,Collection<Line>> lineMap=allLines.asMap();
		for (Map.Entry<SpecialFraction,Collection<Line>> entry:lineMap.entrySet())	{
			long newLines=entry.getValue().size();
			res+=newLines*cumulative;
			cumulative+=newLines;
		}
		return res;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		List<Point> points=getAllPoints(N);
		SetMultimap<SpecialFraction,Line> lines=getAllLines(points);
		System.out.println("Lines: "+lines.size()+".");
		System.out.println("Different slopes: "+lines.keySet().size()+".");
		long crossPoints=getCrossPoints(lines);
		System.out.println("Cross points: "+crossPoints+".");
		System.out.println("Double cross points: "+(2*crossPoints)+".");
		long tac=System.nanoTime();
		double seconds=(tac-tic)/1e9;
		System.out.println("Seconds: "+seconds+".");
	}
}
