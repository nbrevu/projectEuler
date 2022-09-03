package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.euler.common.EulerUtils.Pair;
import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.ProteinUtils;

public class RosalindEdta {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_edta.txt";
	
	// La idea está bien, pero esto está mal :(.
	private static Pair<String,String> backtrack(int[][] array,String s1,String s2)	{
		StringBuilder result1=new StringBuilder();
		StringBuilder result2=new StringBuilder();
		int i=s1.length();
		int j=s2.length();
		while ((i>0)&&(j>0)) if (array[i][j]==array[i-1][j-1])	{
			result1.append(s1.charAt(i-1));
			result2.append(s2.charAt(j-1));
			--i;
			--j;
		}	else if (array[i][j]==1+array[i-1][j-1])	{
			result1.append(s1.charAt(i-1));
			result2.append(s2.charAt(j-1));
			--i;
			--j;
		}	else if (array[i][j]==1+array[i-1][j])	{
			result1.append(s1.charAt(i-1));
			result2.append('-');
			--i;
		}	else	{
			result1.append('-');
			result2.append(s2.charAt(j-1));
			--j;
		}
		while (i>0)	{
			result1.append(s1.charAt(i-1));
			result2.append('-');
			--i;
		}
		while (j>0)	{
			result1.append('-');
			result2.append(s2.charAt(j-1));
			--j;
		}
		return new Pair<>(result1.reverse().toString(),result2.reverse().toString());
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=2) throw new IllegalStateException();
		String s1=entries.get(0).getContent();
		String s2=entries.get(1).getContent();
		int[][] levenshteinArray=ProteinUtils.computeLevenshteinArray(s1,s2);
		Pair<String,String> result=backtrack(levenshteinArray,s1,s2);
		System.out.println(result.first);
		System.out.println(result.second);
	}
}
