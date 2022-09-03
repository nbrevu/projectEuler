package com.euler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.euler.common.Rational;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

public class Euler794 {
	private final static int N=17;
	
	private static class Interval	{
		public final Rational a;
		public final Rational b;
		public Interval(Rational a,Rational b)	{
			this.a=a;
			this.b=b;
		}
		public Interval intersection(Interval other)	{
			Rational lower=Rational.max(a,other.a);
			Rational upper=Rational.min(b,other.b);
			return (lower.compareTo(upper)<0)?new Interval(lower,upper):null;
		}
		@Override
		public String toString()	{
			return String.format("[%s,%s)",a.toString(),b.toString());
		}
	}
	
	private static class RationalCache	{
		private final LongObjMap<LongObjMap<Rational>> rationals;
		public RationalCache()	{
			rationals=HashLongObjMaps.newMutableMap();
		}
		public Rational of(long a,long b)	{
			return rationals.computeIfAbsent(a,(long unused)->HashLongObjMaps.newMutableMap()).computeIfAbsent(b,(long unused)->new Rational(a,b));
		}
	}
	
	private static class IntervalCalculator	{
		private final RationalCache rationals;
		private final int maxN;
		public IntervalCalculator(int maxN)	{
			rationals=new RationalCache();
			this.maxN=maxN;
		}
		private boolean canFit(Interval[] intervals,int n)	{
			int index=0;
			Rational start=Rational.ZERO;
			for (int i=1;i<=n;++i)	{
				Rational end=rationals.of(i,n);
				boolean found=false;
				for (;index<intervals.length;++index) if (intervals[index].a.compareTo(end)>=0) return false;
				else if (intervals[index].b.compareTo(start)>0)	{
					found=true;
					break;
				}
				if (!found) return false;
			}
			return true;
		}
		public boolean isFeasible(Interval[] intervals)	{
			for (int i=intervals.length+1;i<=maxN;++i) if (!canFit(intervals,i)) return false;
			return true;
		}
		public Interval[] initial()	{
			return new Interval[] {new Interval(Rational.ZERO,rationals.of(1,1))};
		}
		private Interval[] tryDivide(Interval[] base,int index)	{
			int n=base.length+1;
			int writeIndex=0;
			Interval[] result=new Interval[n];
			for (int i=0;i<base.length;++i)	{
				int times=(i==index)?2:1;
				for (int j=0;j<times;++j)	{
					Interval inter=new Interval(rationals.of(writeIndex,n),rationals.of(writeIndex+1,n));
					result[writeIndex]=inter.intersection(base[i]);
					if (result[writeIndex]==null) return null;
					++writeIndex;
				}
			}
			return isFeasible(result)?result:null;
		}
		private Interval[] tryShoehorn(Interval[] base,int index)	{
			int n=base.length+1;
			int writeIndex=0;
			Interval[] result=new Interval[n];
			for (int i=0;i<base.length;++i)	{
				if (i==index)	{
					Interval inter=new Interval(rationals.of(writeIndex,n),rationals.of(writeIndex+1,n));
					Interval phantom=new Interval(base[i-1].b,base[i].a);
					result[writeIndex]=inter.intersection(phantom);
					if (result[writeIndex]==null) return null;
					++writeIndex;
				}
				Interval inter=new Interval(rationals.of(writeIndex,n),rationals.of(writeIndex+1,n));
				result[writeIndex]=inter.intersection(base[i]);
				if (result[writeIndex]==null) return null;
				++writeIndex;
			}
			return isFeasible(result)?result:null;
		}
		public void findDivisions(Interval[] base,List<Interval[]> result)	{
			for (int i=0;i<base.length;++i)	{
				Interval[] child=tryDivide(base,i);
				if (child!=null) result.add(child);
			}
			for (int i=1;i<base.length;++i) if (base[i-1].b.compareTo(base[i].a)<0)	{
				Interval[] child=tryShoehorn(base,i);
				if (child!=null) result.add(child);
			}
		}
	}
	
	/*
	 * This is actually VERY inefficient, but the problem space is so small that it doesn't matter! Feels like a 1-50 problem :O. 
	 */
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		IntervalCalculator calcu=new IntervalCalculator(N);
		// List.of did something unexpected here :D.
		List<Interval[]> cases=new ArrayList<>();
		cases.add(calcu.initial());
		for (int i=2;i<=N;++i)	{
			List<Interval[]> newCases=new ArrayList<>();
			for (Interval[] prev:cases) calcu.findDivisions(prev,newCases);
			cases=newCases;
		}
		Rational result=new Rational(N,1);
		for (Interval[] partition:cases)	{
			Rational x=Rational.ZERO;
			for (Interval i:partition) x=x.sum(i.a);
			result=Rational.min(result,x);
			// if (result.equals(x)) System.out.println(Arrays.toString(partition)+"=>"+result+".");
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(String.format(Locale.UK,"%.12f",result.toDouble()));
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
