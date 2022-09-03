package com.rosalind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RosalindSeto {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_seto.txt";
	
	private final static String OUT_FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_seto.txt";
	
	private static class IntSet	{
		private final boolean[] contents;
		public IntSet(int universeSize,int[] intContents)	{
			contents=new boolean[universeSize];
			for (int c:intContents) contents[c-1]=true;
		}
		private IntSet(boolean[] contents)	{
			this.contents=contents;
		}
		public IntSet union(IntSet other)	{
			int N=contents.length;
			assert N==other.contents.length;
			boolean[] result=new boolean[N];
			for (int i=0;i<N;++i) result[i]=contents[i]||other.contents[i];
			return new IntSet(result);
		}
		public IntSet intersection(IntSet other)	{
			int N=contents.length;
			assert N==other.contents.length;
			boolean[] result=new boolean[N];
			for (int i=0;i<N;++i) result[i]=contents[i]&&other.contents[i];
			return new IntSet(result);
		}
		public IntSet difference(IntSet other)	{
			int N=contents.length;
			assert N==other.contents.length;
			boolean[] result=new boolean[N];
			for (int i=0;i<N;++i) result[i]=contents[i]&&!other.contents[i];
			return new IntSet(result);
		}
		public IntSet complement()	{
			int N=contents.length;
			boolean[] result=new boolean[N];
			for (int i=0;i<N;++i) result[i]=!contents[i];
			return new IntSet(result);
		}
		@Override
		public String toString()	{
			StringBuilder result=new StringBuilder();
			result.append("{");
			boolean first=true;
			for (int i=0;i<contents.length;++i) if (contents[i])	{
				if (first) first=false;
				else result.append(", ");
				result.append(i+1);
			}
			result.append("}");
			return result.toString();
		}
	}
	
	private static int[] readSet(String in)	{
		// Remove curly braces
		in=in.substring(1,in.length()-1);
		String[] split=in.split(",");
		int[] result=new int[split.length];
		for (int i=0;i<split.length;++i) result[i]=Integer.parseInt(split[i].trim());
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int N;
		int[] s1;
		int[] s2;
		try (BufferedReader reader=Files.newBufferedReader(Paths.get(FILE)))	{
			N=Integer.parseInt(reader.readLine());
			s1=readSet(reader.readLine());
			s2=readSet(reader.readLine());
		}
		IntSet set1=new IntSet(N,s1);
		IntSet set2=new IntSet(N,s2);
		try (PrintStream out=new PrintStream(OUT_FILE))	{
			out.println(set1.union(set2));
			out.println(set1.intersection(set2));
			out.println(set1.difference(set2));
			out.println(set2.difference(set1));
			out.println(set1.complement());
			out.println(set2.complement());
		}
	}
}
