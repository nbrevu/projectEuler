package com.rosalind;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;

public class RosalindKmp_2 {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_kmp.txt";
	
	private static int[] kmpSearch(String S)	{
		int N=S.length();
		int[] result=new int[1+N];
		result[0]=-1;
		result[1]=0;
		int pos=2;
		int cond=0;
		while (pos<=N)	{
			if (S.charAt(pos-1)==S.charAt(cond))	{
				++cond;
				result[pos]=cond;
				++pos;
			}	else if (cond>0) cond=result[cond];
			else	{
				result[pos]=0;
				++pos;
			}
		}
		int[] trueResult=new int[N];
		System.arraycopy(result,1,trueResult,0,N);
		return trueResult;
	}
	
	public static void main(String[] args) throws IOException	{
		// ZUTUN! Pues está mal :O.
		try (PrintStream out=new PrintStream("F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\OutFiles\\rosalind_kmp.txt"))	{
			FastaReader reader=new FastaReader(Paths.get(FILE));
			reader.read();
			List<FastaEntry> entries=reader.getEntries();
			String singleton=entries.get(0).getContent();
			int[] result=kmpSearch(singleton);
			for (int v:result) out.print(" "+v);
			out.println();
		}
	}
}
