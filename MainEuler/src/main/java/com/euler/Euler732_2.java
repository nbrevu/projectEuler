package com.euler;

import java.util.Arrays;

public class Euler732_2 {
	private final static int N=1000;
	
	private static class PseudoRandomGenerator	{
		private long r;
		public PseudoRandomGenerator()	{
			r=1l;
		}
		public int next()	{
			long result=(r%101)+50;
			r=(r*5l)%1_000_000_007l;
			return (int)result;
		}
	}
	private static class Troll	{
		public final int h;
		public final int l;
		public final int q;
		public Troll(int h,int l,int q)	{
			this.h=h;
			this.l=l;
			this.q=q;
		}
	}
	
	private static int solveKnapsack(Troll[] trolls,int space)	{
		int n=trolls.length;
		int[][] data=new int[n+1][space+1];
		for (int i=1;i<=n;++i)	{
			int h=trolls[i-1].h;
			System.arraycopy(data[i-1],0,data[i],0,h);
			for (int j=h;j<=space;++j) data[i][j]=Math.max(data[i-1][j],data[i-1][j-h]+trolls[i-1].q);
		}
		return data[n][space];
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		PseudoRandomGenerator gen=new PseudoRandomGenerator();
		Troll[] trolls=new Troll[N];
		int totalH=0;
		for (int i=0;i<N;++i)	{
			int h=gen.next();
			int l=gen.next();
			int q=gen.next();
			trolls[i]=new Troll(h,l,q);
			totalH+=h;
		}
		int d=(int)(Math.ceil(Math.sqrt(0.5)*totalH));
		Troll[] restrictedSet=Arrays.copyOfRange(trolls,1,trolls.length);
		int result=trolls[0].q+solveKnapsack(restrictedSet,totalH-d-trolls[0].l);
		for (int i=0;i<restrictedSet.length;++i)	{
			restrictedSet[i]=trolls[i];
			int thisResult=trolls[i+1].q+solveKnapsack(restrictedSet,totalH-d+trolls[i+1].l);
			result=Math.max(result,thisResult);
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
