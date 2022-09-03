package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import com.euler.common.EulerUtils;

public class Euler622_3 {
	private static class Shuffler	{
		private final int N;
		private final int N2;
		private int[] current;
		private int[] shuffler;
		public Shuffler(int N)	{
			if ((N%2)!=0) throw new IllegalArgumentException();
			this.N=N;
			N2=N/2;
			current=createOrderedArray(N);
			shuffler=new int[N];
		}
		private static int[] createOrderedArray(int N)	{
			int[] result=new int[N];
			for (int i=0;i<N;++i) result[i]=i;
			return result;
		}
		private void interleave(int[] in,int[] out)	{
			for (int i=0;i<N2;++i)	{
				out[2*i]=in[i];
				out[2*i+1]=in[i+N2];
			}
		}
		private void shuffle()	{
			interleave(current,shuffler);
			int[] middle=current;
			current=shuffler;
			shuffler=middle;
		}
		private int countShuffles()	{
			int counter=0;
			do	{
				shuffle();
				++counter;
			}	while (!isOrdered(current));
			return counter;
		}
		private int countShufflesPartial()	{
			int counter=0;
			int index=1;
			do	{
				index=nextInt(index);
				++counter;
			}	while (index!=1);
			return counter;
		}
		private int nextInt(int i)	{
			if (i<N2) return i+i;
			else return i+i+1-N;
		}
		private static boolean isOrdered(int[] in)	{
			int N=in.length;
			for (int i=0;i<N;++i) if (in[i]!=i) return false;
			return true;
		}
		public static int countShuffles(int N,boolean partial)	{
			Shuffler shuffler=new Shuffler(N);
			return partial?shuffler.countShufflesPartial():shuffler.countShuffles();
		}
	}
	
	public static void main(String[] args)	{
		Map<Integer,Integer> counter=new TreeMap<>();
		int LIMIT=1<<18;
		for (int i=2;i<=LIMIT;i+=2)	{
			int s2=Shuffler.countShuffles(i,true);
			EulerUtils.increaseCounter(counter,s2);
		}
		try (PrintStream ps=new PrintStream("C:\\out622_3.txt"))	{
			for (Map.Entry<Integer,Integer> entry:counter.entrySet()) ps.println("Hay "+entry.getValue()+" valores con s(x)="+entry.getKey()+".");
		}	catch (IOException exc)	{
			System.out.println("No.");
		}
	}
}
