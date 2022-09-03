package com.euler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.euler.common.Rational;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Euler259 {
	private static Rational getConcatenated(int start,int end)	{
		long result=0;
		for (int i=start;i<=end;++i) result=10*result+i;
		return new Rational(result);
	}
	
	private static Table<Integer,Integer,Set<Rational>> cache=HashBasedTable.create();
	
	private static class Partition	{
		private final int start;
		private final int end;
		public Partition(int start,int end)	{
			// System.out.println("Creating division: ["+start+","+end+"]");
			this.start=start;
			this.end=end;
		}
		private List<PartitionPair> getSubdivisions()	{
			List<PartitionPair> subdivisions=new ArrayList<>();
			for (int i=start+1;i<=end;++i)	{
				Partition first=new Partition(start,i-1);
				Partition second=new Partition(i,end);
				subdivisions.add(new PartitionPair(first,second));
			}
			return subdivisions;
		}
		public Set<Rational> getAllPossibilities()	{
			if (cache.contains(start,end)) return cache.get(start,end);
			Set<Rational> result=new HashSet<>();
			result.add(getConcatenated(start,end));
			for (PartitionPair pp:getSubdivisions()) result.addAll(pp.getAllPossibilities());
			cache.put(start,end,result);
			return result;
		}
	}
	
	private static class PartitionPair	{
		private final Partition first;
		private final Partition second;
		public PartitionPair(Partition first,Partition second)	{
			this.first=first;
			this.second=second;
		}
		public Set<Rational> getAllPossibilities()	{
			Set<Rational> result=new HashSet<>();
			Set<Rational> left=first.getAllPossibilities();
			Set<Rational> right=second.getAllPossibilities();
			for (Rational a:left) for (Rational b:right)	{
				result.add(a.sum(b));
				result.add(a.subtract(b));
				result.add(a.multiply(b));
				if (b.isNotZero()) result.add(a.divide(b));
			}
			return result;
		}
	}
	
	public static void main(String[] args)	{
		Partition part=new Partition(1,9);
		long sum=0;
		for (Rational r:part.getAllPossibilities()) if (r.isInteger())	{
			long intValue=r.getIntegerValue();
			if (intValue>0) sum+=intValue;
		}
		System.out.println(sum);
		/*
		try (PrintStream out=new PrintStream(new File("D:\\out259.txt")))	{
			for (int i=1;i<=9;++i) for (int j=i;j<=9;++j) out.println("["+i+", "+j+"]: "+cache.get(i,j));
		}	catch (IOException exc)	{
			System.out.println("D'oh!");
		}
		*/
	}
}
