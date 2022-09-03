package com.euler;

import java.math.RoundingMode;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.google.common.math.DoubleMath;

public class Euler461_2 {
	private final static int N=10000;

	private static int getMaxPossibleNumber(int n)	{
		// e^(result/n)-1<=pi -> result/n<=log(pi-1)
		return DoubleMath.roundToInt(n*Math.log(Math.PI+1),RoundingMode.DOWN);
	}
	
	private static double[] calculateAugends(int limit,int n)	{
		double[] result=new double[1+limit];
		for (int i=0;i<=limit;++i) result[i]=Math.exp((double)i/(double)n)-1;
		return result;
	}
	
	private static class Euler461Pair	{
		public final int n1;
		public final int n2;
		public Euler461Pair(int n1,int n2)	{
			this.n1=n1;
			this.n2=n2;
		}
	}
	
	private static NavigableMap<Double,Euler461Pair> getAllPairs(int n)	{
		NavigableMap<Double,Euler461Pair> result=new TreeMap<>();
		int limit=getMaxPossibleNumber(n);
		System.out.println("Limit="+limit+".");
		double[] augends=calculateAugends(limit,n);
		for (int i=0;i<=limit;++i) for (int j=i;j<=limit;++j)	{
			Euler461Pair pair=new Euler461Pair(i,j);
			double sum=augends[i]+augends[j];
			if (sum<=Math.PI) result.put(sum,pair);
			else break;	// Next i.
		}
		return result;
	}
	
	private static Map.Entry<Double,Euler461Pair> findClosestEntry(double value,NavigableMap<Double,Euler461Pair> pairs)	{
		Map.Entry<Double,Euler461Pair> floorEntry=pairs.floorEntry(value);
		Map.Entry<Double,Euler461Pair> ceilEntry=pairs.ceilingEntry(value);
		if (floorEntry==null) return ceilEntry;
		else if (ceilEntry==null) return floorEntry;
		double diff1=Math.abs(floorEntry.getKey()-value);
		double diff2=Math.abs(ceilEntry.getKey()-value);
		return (diff2>diff1)?floorEntry:ceilEntry;
	}
	
	private static long square(long in)	{
		return in*in;
	}
	
	private static long getResult(Euler461Pair p1,Euler461Pair p2)	{
		// System.out.println(""+p1.n1+", "+p1.n2+", "+p2.n1+", "+p2.n2+".");
		return square(p1.n1)+square(p1.n2)+square(p2.n1)+square(p2.n2);
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		NavigableMap<Double,Euler461Pair> allPairs=getAllPairs(N);
		System.out.println("Found "+allPairs.size()+" suitable pairs.");
		Euler461Pair p1=null;
		Euler461Pair p2=null;
		double minDiff=Math.PI;
		for (Map.Entry<Double,Euler461Pair> entry:allPairs.entrySet())	{
			if (entry.getKey()>Math.PI/2) break;
			Map.Entry<Double,Euler461Pair> complementary=findClosestEntry(Math.PI-entry.getKey(),allPairs);
			double diff=Math.abs(Math.PI-entry.getKey()-complementary.getKey());
			if (diff<minDiff)	{
				p1=entry.getValue();
				p2=complementary.getValue();
				minDiff=diff;
			}
		}
		System.out.println(getResult(p1,p2));
		long tac=System.nanoTime();
		double seconds=(tac-tic)*1e-9;
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
