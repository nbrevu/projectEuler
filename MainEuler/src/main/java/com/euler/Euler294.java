package com.euler;

import java.util.Arrays;
import java.util.function.Consumer;

public class Euler294 {
	/*
	 * First idea:
	 * 1) Generate all the numbers with exactly 22 digits that verify the conditions (maybe there arent SO MANY?)
	 * 	Generating them fast might be tricky.
	 * 2) Use combinatorics, expanding each digit for all the "space". For example, if the limit is 50, then the digit in position
	 * 	2 could be "divided" between digits 2, 24 and 46, while the digit in position 10 could only be "divided" between positions
	 *  10 and 32 (54 would be outside limits). So if the digit in position 2 is a 6, it can be divided in C(6+3-1,3-1)=28 cases,
	 *  while if the digit in position 10 is a 5 it can be divided in C(5+2-1,2-1)=6 cases. And so on!
	 * HOWEVER! Each digit could be as high as 23, but when dividing, we have to take care of that. This is probably the trickiest
	 * part.
	 * 
	 * There are nchoosek(23+22-1,22-1)~=2e12 cases. This is not feasible :(.
	 */
	private final static int N=9;
	private final static int PRIME=23;

	private static int[] getModValues(int n,int prime)	{
		int[] result=new int[n];
		result[0]=1;
		for (int i=1;i<n;++i) result[i]=(result[i-1]*10)%prime;
		return result;
	}
	
	private static class SubArrayPermutator	{
		private final int[] array;
		private final int startPosition;
		private SubArrayPermutator(int[] array,int startPosition)	{
			this.array=array;
			this.startPosition=startPosition;
		}
		public SubArrayPermutator(int length,int value)	{
			array=new int[length];
			array[0]=value;
			startPosition=0;
		}
		public void forEach(Consumer<int[]> consumer)	{
			int size=array.length-startPosition;
			if (size==2)	{
				for (int i=array[startPosition];i>=0;--i)	{
					consumer.accept(array);
					--array[startPosition];
					++array[startPosition+1];
				}
			}	else	{
				int origValue=array[startPosition];
				for (int i=origValue;i>=0;--i)	{
					array[startPosition]=i;
					array[1+startPosition]=origValue-i;
					Arrays.fill(array,2+startPosition,array.length,0);
					new SubArrayPermutator(array,1+startPosition).forEach(consumer);
				}
			}
		}
	}
	
	private static class Analyzer implements Consumer<int[]>	{
		private final int prime;
		private final int[] mods;
		private long totalCount=0;
		private long multipleCount=0;
		
		public Analyzer(int n,int prime)	{
			this.prime=prime;
			mods=getModValues(n,prime);
			totalCount=0;
			multipleCount=0;
		}
		@Override
		public void accept(int[] t) {
			++totalCount;
			int sum=0;
			for (int i=0;i<t.length;++i) sum+=t[i]*mods[i];
			if ((sum%prime)==0) ++multipleCount;
		}
		@Override
		public String toString()	{
			double factor=((double)totalCount)/(double)multipleCount;
			return "Total: "+totalCount+"; multiples="+multipleCount+"; factor="+factor+".";
		}
	}
	
	public static void main(String[] args)	{
		long tic=System.nanoTime();
		SubArrayPermutator generator=new SubArrayPermutator(N,PRIME);
		Analyzer counter=new Analyzer(N,PRIME);
		generator.forEach(counter);
		long tac=System.nanoTime();
		double seconds=((tac-tic)*1e-9);
		System.out.println(counter);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
