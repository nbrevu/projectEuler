package com.euler;

import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.RowSortedTable;
import com.google.common.collect.TreeBasedTable;
import com.google.common.math.LongMath;

public class Euler617_2 {
	private final static long LIMIT=LongMath.pow(10l,18);
	
	private static Set<Long> generatePowers(long max)	{
		Set<Long> result=new TreeSet<>();
		int maxPower=LongMath.log2(max,RoundingMode.DOWN);
		for (int i=2;i<=maxPower;++i) for (long n=2;;++n)	{
			long pow=LongMath.pow(n,i);
			if (pow>max) break;	// Next i.
			else result.add(pow);
		}
		return result;
	}
	
	private static int getLog(long power,long base)	{
		int log=(int)Math.round(Math.log(power)/Math.log(base));
		return (LongMath.pow(base,log)==power)?log:0;
	}
	
	private static int getPrefixes(int m,int e)	{
		int result=0;
		while ((m%e)==0)	{
			++result;
			m/=e;
		}
		return result;
	}
	
	/*
	 * This calculates how many cases of the form n^k + n^m can we found, using exponent e.
	 */
	private static int howManyCases(int k,int m,int e)	{
		int r=getLog(k/m,e);
		if (r==0) return 0;
		return r+getPrefixes(m,e);
	}
	
	private static int howManyCases(int k,int m)	{
		if ((k%m)!=0) return 0;
		int result=0;
		for (int e=2;e<=k/m;++e) result+=howManyCases(k,m,e);
		return result;
	}
	
	// For keys k and m, returns howManyCases(k,m).
	private static RowSortedTable<Integer,Integer,Integer> generateCases(long max)	{
		RowSortedTable<Integer,Integer,Integer> result=TreeBasedTable.create();
		int maxK=LongMath.log2(max,RoundingMode.DOWN);
		for (int k=2;k<=maxK;++k) for (int m=1;m<k;++m)	{
			int cases=howManyCases(k,m);
			if (cases!=0) result.put(k,m,cases);
		}
		return result;
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		long result=0;
		RowSortedTable<Integer,Integer,Integer> cases=generateCases(LIMIT);
		long maxBase=LongMath.sqrt(LIMIT,RoundingMode.DOWN);
		Set<Long> forbidden=generatePowers(maxBase);
		double maxLog=Math.log(LIMIT);
		for (long b=2;b<=maxBase;++b) if (!forbidden.contains(b))	{
			double baseLog=Math.log(b);
			for (Map.Entry<Integer,Map<Integer,Integer>> row:cases.rowMap().entrySet())	{
				int k=row.getKey();
				double powLog=k*baseLog;
				if (powLog>maxLog) break;
				Map<Integer,Integer> subcases=row.getValue();
				long basePow=LongMath.pow(b,k);
				for (Map.Entry<Integer,Integer> column:subcases.entrySet())	{
					int m=column.getKey();
					long subPow=LongMath.pow(b,m);
					if (basePow+subPow<=LIMIT) result+=column.getValue();
					else break;
				}
			}
		}
		long tac=System.nanoTime();
		double seconds=((double)(tac-tic))*1e-9;
		System.out.println(result);
		System.out.println("Time: "+seconds+" seconds.");
	}
}
