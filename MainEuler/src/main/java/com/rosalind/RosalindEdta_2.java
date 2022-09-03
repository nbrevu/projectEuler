package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindEdta_2 {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_edta.txt";
	
	private static class StringPair	{
		public final int distance;
		public final String s1;
		public final String s2;
		public StringPair(int distance,String s1,String s2)	{
			this.distance=distance;
			assert s1.length()==s2.length();
			this.s1=s1;
			this.s2=s2;
		}
		public StringPair advance(char symbol1,char symbol2)	{
			int newDistance=distance+((symbol1==symbol2)?0:1);
			String newS1=s1+symbol1;
			String newS2=s2+symbol2;
			return new StringPair(newDistance,newS1,newS2);
		}
	}
	
	private static StringPair computeLevenshteinDistanceExtended(String s1,String s2)	{
		int N=s1.length();
		int M=s2.length();
		StringPair[][] d=new StringPair[N+1][M+1];
		d[0][0]=new StringPair(0,"","");
		StringBuilder b1=new StringBuilder();
		StringBuilder b2=new StringBuilder();
		for (int i=1;i<=N;++i)	{
			b1.append(s1.charAt(i-1));
			b2.append('-');
			d[i][0]=new StringPair(i,b1.toString(),b2.toString());
		}
		b1=new StringBuilder();
		b2=new StringBuilder();
		for (int j=1;j<=M;++j)	{
			b1.append('-');
			b2.append(s2.charAt(j-1));
			d[0][j]=new StringPair(j,b1.toString(),b2.toString());
		}
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j)	{
			int substitutionCost=(s1.charAt(i-1)==s2.charAt(j-1))?0:1;
			int d0=d[i-1][j-1].distance+substitutionCost;
			int d1=d[i-1][j].distance;
			int d2=d[i][j-1].distance;
			int minDist=Math.min(Math.min(d1,d2),d0);
			if (minDist==d0) d[i][j]=d[i-1][j-1].advance(s1.charAt(i-1),s2.charAt(j-1));
			else if (minDist==d1) d[i][j]=d[i-1][j].advance(s1.charAt(i-1),'-');
			else d[i][j]=d[i][j-1].advance('-',s2.charAt(j-1));
		}
		return d[N][M];
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=2) throw new IllegalStateException();
		String s1=entries.get(0).getContent();
		String s2=entries.get(1).getContent();
		StringPair result=computeLevenshteinDistanceExtended(s1,s2);
		System.out.println(result.distance);
		System.out.println(result.s1);
		System.out.println(result.s2);
	}
}
