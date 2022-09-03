package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindLcsq {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_lcsq.txt";

	private static class MaxSubseqFinder	{
		public String s1;
		public String s2;
		public int[][] maxLengths;
		public MaxSubseqFinder(String s1,String s2)	{
			this.s1=s1;
			this.s2=s2;
			maxLengths=new int[1+s1.length()][1+s2.length()];
		}
		public void fillArray()	{
			int N1=s1.length();
			int N2=s2.length();
			for (int i=0;i<N1;++i) for (int j=0;j<N2;++j) if (s1.charAt(i)==s2.charAt(j)) maxLengths[i+1][j+1]=1+maxLengths[i][j];
			else maxLengths[i+1][j+1]=Math.max(maxLengths[i][j+1],maxLengths[i+1][j]);
		}
		public String backtrack()	{
			StringBuilder result=new StringBuilder();
			int i=s1.length()-1;
			int j=s2.length()-1;
			while ((i>=0)&&(j>=0)) if (s1.charAt(i)==s2.charAt(j))	{
				result.append(s1.charAt(i));
				--i;
				--j;
			}	else if (maxLengths[i+1][j]>=maxLengths[i][j+1]) --j;
			else --i;
			return result.reverse().toString();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=2) throw new IllegalStateException();
		MaxSubseqFinder finder=new MaxSubseqFinder(entries.get(0).getContent(),entries.get(1).getContent());
		finder.fillArray();
		System.out.println(finder.backtrack());
	}
}
