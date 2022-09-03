package com.euler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Euler689_2 {
	private final static double LIMIT=0.5;
	private final static double BASEL_SOLUTION=square(Math.PI)/6d;
	
	private static double square(double in)	{
		return in*in;
	}
	
	private static class Addend	{
		public final double sumSoFar;
		public final double potentialSum;
		public final double maxSum;
		public Addend()	{
			this(0,BASEL_SOLUTION);
		}
		private Addend(double sumSoFar,double potentialSum)	{
			this.sumSoFar=sumSoFar;
			this.potentialSum=potentialSum;
			maxSum=sumSoFar+potentialSum;
		}
		public List<Addend> getChildren(int nextI)	{
			double nextAddend=1/square(nextI);
			double remaining=potentialSum-nextAddend;
			Addend child1=new Addend(sumSoFar+nextAddend,remaining);
			Addend child2=new Addend(sumSoFar,remaining);
			return Arrays.asList(child1,child2);
		}
	}
	
	public static void main(String[] args)	{
		List<Addend> queue=new ArrayList<>();
		List<Addend> swapper=new ArrayList<>();
		queue.add(new Addend());
		double accumulated=0.0;
		double probability=1d;
		for (int i=1;!queue.isEmpty();++i,probability*=0.5d)	{
			int addNow=0;
			for (Addend candidate:queue)	{
				if (candidate.sumSoFar>=LIMIT) ++addNow;
				else for (Addend child:candidate.getChildren(i)) if (child.maxSum>=LIMIT) swapper.add(child);
			}
			double toAdd=addNow*probability;
			accumulated+=toAdd;
			System.out.println(String.format("Adding %.8f: sum=%.8f...",toAdd,accumulated));
			List<Addend> swap=swapper;
			swapper=queue;
			queue=swap;
			swapper.clear();
		}
	}
}
