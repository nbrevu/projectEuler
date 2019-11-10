package com.euler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.euler.common.EulerUtils;
import com.euler.common.Rational;
import com.euler.common.Timing;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

public class Euler165 {
	private final static int N=5000;
	
	private static class Slope	{
		public final long dx;
		public final long dy;
		private final int hash;
		public Slope(long dx,long dy)	{
			if (dx==0)	{
				this.dx=0;
				this.dy=1;
			}	else if (dy==0)	{
				this.dx=1;
				this.dy=0;
			}	else	{
				long g=EulerUtils.gcd(dx,dy);
				dx/=g;
				dy/=g;
				if (dx<0)	{
					dx=-dx;
					dy=-dy;
				}
				this.dx=dx;
				this.dy=dy;
			}
			hash=Long.hashCode(1000*this.dx+this.dy);
		}
		@Override
		public int hashCode()	{
			return hash;
		}
		@Override
		public boolean equals(Object other)	{
			Slope sOther=(Slope)other;
			return (dx==sOther.dx)&&(dy==sOther.dy);
		}
	}
	
	private static class Point	{
		public final Rational x;
		public final Rational y;
		private final int hash;
		public Point(Rational x,Rational y)	{
			this.x=x;
			this.y=y;
			hash=37*x.hashCode()+y.hashCode();
		}
		@Override
		public int hashCode()	{
			return hash;
		}
		@Override
		public boolean equals(Object other)	{
			Point pOther=(Point)other;
			return x.equals(pOther.x)&&y.equals(pOther.y);
		}
	}
	
	private static class Segment	{
		public final long x1;
		public final long y1;
		public final long ix;
		public final long iy;
		public final Slope slope;
		public Segment(long x1,long y1,long x2,long y2)	{
			this.x1=x1;
			this.y1=y1;
			ix=x2-x1;
			iy=y2-y1;
			slope=new Slope(ix,iy);
		}
		public Point intersection(Segment other)	{
			long den=other.ix*iy-ix*other.iy;
			// if (den==0) return null;	// Never happens!
			long dx=other.x1-x1;
			long dy=other.y1-y1;
			long numT=other.ix*dy-other.iy*dx;
			Rational t=new Rational(numT,den);
			if (!isValid(t)) return null;
			long numU=ix*dy-iy*dx;
			Rational u=new Rational(numU,den);
			if (!isValid(u)) return null;
			// x+t*ix = x+(n/d)*ix = (x*d+n*ix)/d.
			Rational x=new Rational(x1*t.den+ix*t.num,t.den);
			Rational y=new Rational(y1*t.den+iy*t.num,t.den);
			return new Point(x,y);
		}
		private static boolean isValid(Rational t)	{
			return (Rational.ZERO.compareTo(t)<0)&&(t.compareTo(Rational.ONE)<0);
		}
	}
	
	private static class BlumBlumShub	{
		private final static long INITIAL=290797l;
		private final static long MOD1=50515093l;
		private final static long MOD2=500l;
		private long current;
		public BlumBlumShub()	{
			current=INITIAL;
		}
		public long next()	{
			current=(current*current)%MOD1;
			return current%MOD2;
		}
		public Segment nextSegment()	{
			return new Segment(next(),next(),next(),next());
		}
	}
	
	private static List<Map.Entry<Slope,Collection<Segment>>> getAllSegments(int n)	{
		ListMultimap<Slope,Segment> result=MultimapBuilder.hashKeys().arrayListValues().build();
		BlumBlumShub generator=new BlumBlumShub();
		for (int i=0;i<n;++i)	{
			Segment s=generator.nextSegment();
			result.put(s.slope,s);
		}
		return new ArrayList<>(result.asMap().entrySet());
	}
	
	private static long solve()	{
		List<Map.Entry<Slope,Collection<Segment>>> segments=getAllSegments(N);
		int s=segments.size();
		Set<Point> result=new HashSet<>();
		for (int i=0;i<s;++i)	{
			Collection<Segment> si=segments.get(i).getValue();
			for (int j=i+1;j<s;++j)	{
				Collection<Segment> sj=segments.get(j).getValue();
				for (Segment a:si) for (Segment b:sj)	{
					Point intersection=a.intersection(b);
					if (intersection!=null) result.add(intersection);
				}
			}
		}
		return result.size();
	}
	
	public static void main(String[] args)	{
		Timing.time(Euler165::solve);
	}
}
