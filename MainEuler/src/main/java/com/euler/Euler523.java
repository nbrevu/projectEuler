package com.euler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.euler.common.EulerUtils;

public class Euler523 {
	private final static int MAX_SIZE=10;
	
	private static class FirstSortSolver	{
		private final Map<int[],Integer> cache;
		public FirstSortSolver()	{
			cache=new HashMap<>();
		}
		public int solve(int[] in)	{
			return cache.computeIfAbsent(in,(int[] array)->{
				return doSolve(in);
			});
		}
		private int doSolve(int[] array)	{
			int N=array.length;
			int[] copy=Arrays.copyOf(array,array.length);
			for (int i=1;i<N;++i) if (copy[i]<copy[i-1])	{
				int moving=copy[i];
				for (int j=i;j>0;--j) copy[j]=copy[j-1];
				copy[0]=moving;
				return 1+solve(copy);
			}
			return 0;
		}
		public void clear()	{
			cache.clear();
		}
	}
	
	private static int[] baseArray(int size)	{
		int[] result=new int[size];
		for (int i=0;i<size;++i) result[i]=i+1;
		return result;
	}
	
	public static void main(String[] args)	{
		try (PrintStream ps=new PrintStream("C:\\out523.txt"))	{
			System.setOut(ps);
			FirstSortSolver solver=new FirstSortSolver();
			for (int i=2;i<=MAX_SIZE;++i)	{
				System.err.println(""+i+"...");
				int[] base=baseArray(i);
				SortedMap<Integer,Integer> counter=new TreeMap<>();
				int num=0;
				int den=0;
				for (int[] array:new EulerUtils.DestructiveIntArrayPermutationGenerator(base))	{
					int[] copy=Arrays.copyOf(array,i);
					int F=solver.doSolve(copy);
					EulerUtils.increaseCounter(counter,F);
					num+=F;
					++den;
				}
				System.out.println(""+i+": ");
				for (Map.Entry<Integer,Integer> entry:counter.entrySet()) System.out.println("\tF="+entry.getKey()+": "+entry.getValue()+" cases.");
				int gcd=EulerUtils.gcd(num,den);
				int numX=num/gcd;
				int denX=den/gcd;
				double result=((double)numX)/((double)denX);
				System.out.println("\t\tExpected value: "+num+'/'+den+" = "+numX+'/'+denX+" = "+result);
				solver.clear();
			}
		}	catch (IOException exc)	{
			System.out.println("D'oh!");
		}
	}
}
