package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.BiPredicate;

import com.rosalind.utils.IoUtils;

public class RosalindLgis {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lgis.txt";
	
	private static class IncreasingSequence	{
		private final int[] sequence;
		public IncreasingSequence(int in)	{
			this(new int[]{in});
		}
		private IncreasingSequence(int[] sequence)	{
			this.sequence=sequence;
		}
		public IncreasingSequence addNumber(int in)	{
			int[] newArray=Arrays.copyOf(sequence,1+sequence.length);
			newArray[sequence.length]=in;
			return new IncreasingSequence(newArray);
		}
		public int size()	{
			return sequence.length;
		}
		@Override
		public String toString()	{
			return IoUtils.toStringWithSpaces(sequence);
		}
	}
	
	private static IncreasingSequence getLongestIncreasingSequence(int[] in,BiPredicate<Integer,Integer> pred)	{
		int N=in.length;
		IncreasingSequence[] sequences=new IncreasingSequence[N];
		IncreasingSequence best=new IncreasingSequence(in[0]);
		sequences[0]=best;
		for (int i=1;i<N;++i)	{
			IncreasingSequence currentBest=new IncreasingSequence(in[i]);
			for (int j=i-1;j>=0;--j)	{
				if (currentBest.size()>=(j+2)) break;
				else if (pred.test(in[i],in[j])) continue;
				else if (sequences[j].size()>=currentBest.size()) currentBest=sequences[j].addNumber(in[i]);
			}
			sequences[i]=currentBest;
			if (currentBest.size()>best.size()) best=currentBest;
		}
		return best;
	}
	
	public static void main(String[] args) throws IOException	{
		int[] sequence;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			int n=Integer.parseInt(reader.readLine());
			sequence=IoUtils.parseStringAsArrayOfInts(reader.readLine(),n);
		}
		IncreasingSequence increasing=getLongestIncreasingSequence(sequence,(Integer a,Integer b)->a<b);
		IncreasingSequence decreasing=getLongestIncreasingSequence(sequence,(Integer a,Integer b)->a>b);
		System.out.println(increasing);
		System.out.println(decreasing);
	}
}
