package com.euler;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.google.common.math.DoubleMath;
import com.google.common.math.LongMath;

public class Euler192 {
	// TODO! https://shreevatsa.wordpress.com/2011/01/10/not-all-best-rational-approximations-are-the-convergents-of-the-continued-fraction/
	private final static long BOUND=1000000000000l;
	private final static long RANGE=100000l;
	private final static double EPS=1e-9;
	
	private static double divide(long a,long b)	{
		return ((double)a)/((double)b);
	}
	
	private static class Fraction implements Comparable<Fraction>	{
		public final long num;
		public final long den;
		public Fraction(long num,long den)	{
			this.num=num;
			this.den=den;
		}
		@Override
		public int compareTo(Fraction other)	{
			if (den<other.den) return -1;
			if (den>other.den) return 1;
			if (num<other.num) return -1;
			if (num>other.num) return 1;
			return 0;
		}
		public double toDouble()	{
			return divide(num,den);
		}
	}
	
	private static class ContinuedFraction	{
		public final long intPart;
		public final Fraction fraction;
		public ContinuedFraction(long intPart,Fraction fraction)	{
			this.intPart=intPart;
			this.fraction=fraction;
		}
	}
	
	private static List<ContinuedFraction> getContinuedFractions(double d,long bound)	{
		Fraction prev2=new Fraction(0l,1l);
		Fraction prev=new Fraction(1l,0l);
		List<ContinuedFraction> res=new ArrayList<>();
		for (;;)	{
			long a=DoubleMath.roundToLong(d,RoundingMode.DOWN);
			long num=a*prev.num+prev2.num;
			long den=a*prev.den+prev2.den;
			Fraction curr=new Fraction(num,den);
			prev2=prev;
			prev=curr;
			res.add(new ContinuedFraction(a,curr));
			if (den>bound) break;	// So the last one doesn't count.
			double diff=d-(double)a;
			if (Math.abs(diff)<EPS) break;
			d=1.0/(d-a);
		}
		return res;
	}
	
	private static NavigableSet<Fraction> getAllBestApproximations(double d,long bound)	{
		List<ContinuedFraction> continued=getContinuedFractions(d,bound);
		NavigableSet<Fraction> result=new TreeSet<>();
		for (ContinuedFraction frac:continued) result.add(frac.fraction);
		for (int i=0;i<continued.size()-1;++i)	{
			long prevN,prevD;
			if (i==0)	{
				prevN=1l;
				prevD=0l;
			}	else	{
				Fraction prev=continued.get(i-1).fraction;
				prevN=prev.num;
				prevD=prev.den;
			}
			long nextIntPart=continued.get(i+1).intPart;
			ContinuedFraction frac=continued.get(i);
			{
				long n=nextIntPart/2;
				long num=n*frac.fraction.num+prevN;
				long den=n*frac.fraction.den+prevD;
				if (Math.abs(d-divide(num,den))<Math.abs(d-frac.fraction.toDouble())) result.add(new Fraction(num,den));
			}
			for (long n=(nextIntPart+2)/2;n<=nextIntPart;++n)	{
				long num=n*frac.fraction.num+prevN;
				long den=n*frac.fraction.den+prevD;
				result.add(new Fraction(num,den));
			}
		}
		return result.descendingSet();
	}
	
	private static long getBestDenominator(long n,long bound)	{
		NavigableSet<Fraction> bestFractions=getAllBestApproximations(Math.sqrt((double)n),bound);
		for (Fraction frac:bestFractions) if (frac.den<=bound) return frac.den;
		throw new IllegalArgumentException("Algo ha ido mal de cojones.");
	}
	
	public static void main(String[] args)	{
		long sum=0l;
		for (long n=2;n<=RANGE;++n)	{
			long sq=LongMath.sqrt(n, RoundingMode.DOWN);
			if (sq*sq==n) continue;
			sum+=getBestDenominator(n,BOUND);
		}
		System.out.println(sum);
	}
}
