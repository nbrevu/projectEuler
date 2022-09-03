package com.euler;

import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public class Euler689 {
	private final static double LIMIT=0.5;
	private final static double BASEL_SOLUTION=square(Math.PI)/6d;
	
	private static double square(double in)	{
		return in*in;
	}
	
	private static class Addend implements Comparable<Addend>	{
		public final double sumSoFar;
		public final double potentialSum;
		public final double probability;
		private final int nextI;
		public final double maxSum;
		public Addend()	{
			this(0,BASEL_SOLUTION,1,1);
		}
		private Addend(double sumSoFar,double potentialSum,double probability,int nextI)	{
			this.sumSoFar=sumSoFar;
			this.potentialSum=potentialSum;
			this.probability=probability;
			this.nextI=nextI;
			maxSum=sumSoFar+potentialSum;
		}
		@Override
		public int compareTo(Addend other)	{
			int c1=Double.compare(other.probability,probability);
			return (c1!=0)?c1:Double.compare(other.maxSum,maxSum);
		}
		public List<Addend> getChildren()	{
			double nextAddend=1/square(nextI);
			double remaining=potentialSum-nextAddend;
			double halfP=probability/2;
			int ii=1+nextI;
			Addend child1=new Addend(sumSoFar+nextAddend,remaining,halfP,ii);
			Addend child2=new Addend(sumSoFar,remaining,halfP,ii);
			return Arrays.asList(child1,child2);
		}
	}
	
	public static void main(String[] args)	{
		NavigableSet<Addend> queue=new TreeSet<>();
		queue.add(new Addend());
		double accumulated=0.0;
		while (!queue.isEmpty())	{
			Addend candidate=queue.pollFirst();
			if (candidate.sumSoFar>=LIMIT)	{
				accumulated+=candidate.probability;
				System.out.println(String.format("Sumo %.20f y queda %.8f...", candidate.probability,accumulated));
			}
			else for (Addend a:candidate.getChildren()) if (a.maxSum>LIMIT) queue.add(a);
		}
	}
}
