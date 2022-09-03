package com.rosalind;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.rosalind.utils.FastaReader;
import com.rosalind.utils.FastaReader.FastaEntry;
import com.rosalind.utils.IoUtils;

public class RosalindSseq {
	private final static String FILE="F:\\Trabajo\\Programación\\Java\\workspace-euler\\Euler\\src\\com\\rosalind\\InFiles\\rosalind_sseq.txt";
	
	private static int[] getSubseqPosition(String base,String subseq)	{
		int N=subseq.length();
		int[] result=new int[N];
		int j=0;
		for (int i=0;i<N;++i)	{
			char symbol=subseq.charAt(i);
			for (;;)	{
				if (j>base.length()) throw new IllegalStateException();
				if (base.charAt(j)==symbol)	{
					result[i]=j+1;
					++j;
					break;
				}
				++j;
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		FastaReader reader=new FastaReader(Paths.get(FILE));
		reader.read();
		List<FastaEntry> entries=reader.getEntries();
		if (entries.size()!=2) throw new IllegalArgumentException();
		String base=entries.get(0).getContent();
		String subseq=entries.get(1).getContent();
		System.out.println(IoUtils.toStringWithSpaces(getSubseqPosition(base,subseq)));
	}
}
