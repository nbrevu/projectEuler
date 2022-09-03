package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiFunction;

import com.rosalind.aminoacids.AminoacidData;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindGlob {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_glob.txt";
	private final static int GAP_PENALTY=-5;
	
	private static int[][] computeModifiedLevenshteinArray(String s1,String s2,int gapPenalty,BiFunction<Character,Character,Integer> fun)	{
		int N=s1.length();
		int M=s2.length();
		int[][] d=new int[N+1][M+1];
		for (int i=1;i<=N;++i) d[i][0]=d[i-1][0]+gapPenalty;
		for (int j=1;j<=M;++j) d[0][j]=d[0][j-1]+gapPenalty;
		for (int i=1;i<=N;++i) for (int j=1;j<=M;++j)	{
			int cost1=d[i-1][j]+gapPenalty;
			int cost2=d[i][j-1]+gapPenalty;
			int cost3=d[i-1][j-1]+fun.apply(s1.charAt(i-1),s2.charAt(j-1));
			d[i][j]=Math.max(Math.max(cost1,cost2),cost3);
		}
		return d;
	}
	
	private static int getModifiedLevenshteinDistance(String s1,String s2,int gapPenalty,BiFunction<Character,Character,Integer> fun)	{
		int[][] array=computeModifiedLevenshteinArray(s1,s2,gapPenalty,fun);
		return array[s1.length()][s2.length()];
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		assert entries.size()==2;
		int result=getModifiedLevenshteinDistance(entries.get(0).getContent(),entries.get(1).getContent(),GAP_PENALTY,(Character c1,Character c2)->AminoacidData.getBlosumScore(c1,c2));
		System.out.println(result);
	}
}
